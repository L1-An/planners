package com.bh.planners.core.feature.damageable

import com.bh.planners.api.common.Demand
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.api.event.entity.EntityDamageableEvent
import com.bh.planners.core.kether.game.doDamage
import com.bh.planners.core.pojo.data.DataContainer.Companion.unsafeData
import com.bh.planners.util.files
import com.bh.planners.util.timing
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.platform.util.removeMeta
import taboolib.platform.util.setMeta
import java.util.concurrent.CompletableFuture

object DamageableDispatcher {

    val models = mutableMapOf<String, DamageableModel>()

    @Awake(LifeCycle.ENABLE)
    fun initModels() {
        models.clear()
        files("damageable", listOf("example.yml")) {
            models[it.nameWithoutExtension] = DamageableModel(it)

        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        initModels()
    }

    fun getModel(id: String): DamageableModel? {
        return models[id]
    }

    fun invokeDamageable(context: Damageable) {
        val model = context.model

        // init attack data
        runStreams(context, 0, CompletableFuture<Void>(), model.attackStreams).thenCompose {
            // init defend data
            runStreams(context, 0, CompletableFuture<Void>(), model.defendStreams)
        }.thenCompose {
            // init post action
            runMetaActions(context, DamageableActionType.PRE_ACTION, CompletableFuture<Void>())
        }.thenCompose {
            // init action
            runMetaActions(context, DamageableActionType.ACTION, CompletableFuture<Void>())
        }.thenCompose {
            // init action
            runMetaActions(context, DamageableActionType.POST_ACTION, CompletableFuture<Void>())
        }.thenAccept {
            info("Damageable model executed ${context.model.id}")
            info("  |- Attacker: ${context.attacker.name}")
            info("  |- Defender: ${context.victim.name}")
            info("  |- Data")
            context.data.map.forEach {
                info("    - ${it.key} : ${it.value.data}")
            }
            info("  |- Meta size ${context.metas.size}")
            info("  |- Damage source")
            context.damageSources.forEach {
                info("    - ${it.key} : ${it.value.value}")
            }
            info("  |- Damage count: ${context.countDamage}")
            info("  |- Cancel meta: ${context.metaCancel != null}")
            info("  |---- Timing: ${timing(context.timing)}/s")
            if (EntityDamageableEvent(context).call()) {

                // fix minecraft
                context.attacker.setMeta("@PlanenrsDamageable", true)
                val minecraftDamageEvent = EntityDamageByEntityEvent(context.attacker, context.victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, context.countDamage)
                Bukkit.getPluginManager().callEvent(minecraftDamageEvent)
                if (minecraftDamageEvent.isCancelled) {
                    return@thenAccept
                }
                context.attacker.removeMeta("@PlannersDamageable")

                doDamage(context.attacker, context.victim, minecraftDamageEvent.finalDamage)
            }
        }.exceptionally {
            it.printStackTrace()
            null
        }

    }

    fun submitDamageable(id: String, attacker: LivingEntity, victim: LivingEntity, demand: Demand) {
        val damageableModel = getModel(id)
        if (damageableModel == null) {
            warning("Damageable model $id not found.")
            return
        }
        val damageable = Damageable(attacker, victim, damageableModel)
        // 合并
        demand.dataMap.forEach {
            damageable.data[it.key] = it.value.first().unsafeData()
        }
        invokeDamageable(damageable)

    }

    private fun runMetaActions(
        context: Damageable,
        type: DamageableActionType,
        future: CompletableFuture<Void>,
        index: Int = 0,
    ): CompletableFuture<Void> {
        // 模型流提前结束
        if (context.metaCancel != null) {
            future.complete(null)
            return future
        }

        val metas = context.metas
        if (metas.isEmpty() || index >= metas.size) {
            future.complete(null)
            return future
        }
        val meta = metas[index]!!
        val stream = meta.stream
        val metaFuture = when (type) {
            DamageableActionType.PRE_ACTION -> DamageableScript.invokeMetaScript(stream.preAction, context, meta)
            DamageableActionType.POST_ACTION -> DamageableScript.invokeMetaScript(stream.postAction, context, meta)
            DamageableActionType.ACTION -> DamageableScript.invokeMetaScript(stream.actionBuffer, context, meta)
        }
        metaFuture.thenAccept {
            runMetaActions(context, type, future, index + 1)
        }
        return future
    }

    private fun runStreams(
        context: Damageable,
        index: Int,
        future: CompletableFuture<Void>,
        streams: List<DamageableModel.Stream>,
    ): CompletableFuture<Void> {

        if (streams.isEmpty() || index >= streams.size) {
            future.complete(null)
            return future
        }
        val stream = streams[index]
        DamageableScript.createScriptStream(context, stream).thenAccept {
            context.metas[context.metaIndex++] = it
            runStreams(context, index + 1, future, streams)
        }
        return future
    }

}