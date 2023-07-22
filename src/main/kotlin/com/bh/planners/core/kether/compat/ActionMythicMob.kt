package com.bh.planners.core.kether.compat

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.nextSelectorOrNull
import com.bh.planners.core.kether.origin
import io.lumine.xikage.mythicmobs.MythicMobs
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionMythicMob {


    class MythicMobSpawn(val mob: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Target.Container>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {
            val future = CompletableFuture<Target.Container>()
            frame.newFrame(mob).run<Any>().thenAccept { mob ->
                val container = Target.Container()
                if (selector != null) {
                    frame.createContainer(selector).thenAccept { selector ->
                        selector.forEachLocation {
                            container += api.mobManager.spawnMob(mob.toString(), this).entity.bukkitEntity.toTarget()
                        }
                        future.complete(container)
                    }
                } else {
                    container += api.mobManager.spawnMob(
                        mob.toString(),
                        frame.origin().value
                    ).entity.bukkitEntity.toTarget()
                    future.complete(container)
                }

            }
            return future
        }
    }


    companion object {

        val api: MythicMobs by lazy { MythicMobs.inst() }

        @KetherParser(["mm"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("spawn") {
                    MythicMobSpawn(it.nextParsedAction(), nextSelectorOrNull())
                }
            }
        }

    }

}