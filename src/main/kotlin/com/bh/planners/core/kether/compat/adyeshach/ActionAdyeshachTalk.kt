package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.entity.ProxyBukkitEntity
import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelectorOrNull
import ink.ptms.adyeshach.api.AdyeshachAPI
import org.bukkit.Location
import taboolib.library.kether.ParsedAction
import taboolib.module.chat.colored
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

/**
 * Chemdah
 * ink.ptms.chemdah.module.kether.conversation.ConversationTalkPlayer
 *
 * @author sky
 * @since 2021/2/10 6:39 下午
 */
class ActionAdyeshachTalk(val action: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    fun execute(entity: ProxyEntity, message: String) {
        entity.world.players.forEach { player ->
            message.replace("@Target", entity.name).split("\\n").colored().forEachIndexed { index, s ->

                AdyeshachAPI.createHolographic(
                    player,
                    entity.location.clone().add(0.0, entity.height + 0.25 + (index * 0.3), 0.0),
                    40,
                    { it },
                    "§7$s"
                )
            }
        }

    }

    fun execute(location: Location, message: String) {
        location.world!!.players.forEach { player ->
            message.split("\\n").colored().forEach {
                AdyeshachAPI.createHolographic(player, location, 40, { it }, "§7$it")
            }
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(action).run<Any>().thenAccept {
            val message = it.toString()
            if (selector != null) {
                frame.createContainer(selector).thenAccept { container ->
                    container.forEach {
                        if (it is Target.Entity) {
                            execute(it.proxy, message)
                        } else if (it is Target.Location) {
                            execute(it.value, message)
                        }
                    }

                }
            } else {
                execute(ProxyBukkitEntity(frame.bukkitPlayer() ?: return@thenAccept), it.toString())
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    companion object {

        /**
         * talk [message: action]  <selector>
         */
        @KetherParser(["talk"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionAdyeshachTalk(it.nextParsedAction(), it.nextSelectorOrNull())
        }
    }
}