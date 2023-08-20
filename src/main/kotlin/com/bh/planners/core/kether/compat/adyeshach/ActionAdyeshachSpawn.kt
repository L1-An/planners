package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.api.entity.ProxyAdyeshachEntity
import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.target
import com.bh.planners.util.safeSync
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Location
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

object ActionAdyeshachSpawn{

    fun spawn(entityType: EntityTypes, locations: List<Location>, name: String, tick: Long): CompletableFuture<List<ProxyEntity>> {
        val future = CompletableFuture<List<ProxyEntity>>()
        spawn(entityType, locations, block = { it.setCustomName(name) }).thenAccept {
            it.forEach { register(it, tick) }
            future.complete(it)
        }
        return future
    }

    fun spawn(entityType: EntityTypes, locations: List<Location>, block: Consumer<EntityInstance>): CompletableFuture<List<ProxyAdyeshachEntity>> {
        val future = CompletableFuture<List<ProxyAdyeshachEntity>>()
        safeSync {
            future.complete(locations.map { spawn(entityType, it, block) })
        }
        return future
    }

    fun spawn(entityType: EntityTypes, location: Location, block: Consumer<EntityInstance>): ProxyAdyeshachEntity {
        return ProxyAdyeshachEntity(AdyeshachAPI.getEntityManagerPublicTemporary().create(entityType, location, block))
    }


    fun register(entity: ProxyAdyeshachEntity, tick: Long): String {
        // 注册销毁任务
        SimpleTimeoutTask.createSimpleTask(tick, true) {
            if (!entity.isDeleted) {
                entity.instance.delete()
            }
        }
        return "ady:${entity.uniqueId}"
    }
}