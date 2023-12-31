package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.execLocation
import com.bh.planners.core.kether.readAccept
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.SoundType
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionGermSound(
    val name: ParsedAction<*>,
    val type: ParsedAction<*>,
    val volume: ParsedAction<*>,
    val pitch: ParsedAction<*>,
    val selector: ParsedAction<*>,
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.readAccept<String>(name) { name ->
            frame.readAccept<SoundType>(type) { type ->
                frame.readAccept<Float>(volume) { volume ->
                    frame.readAccept<Float>(pitch) { pitch ->
                        frame.execLocation(selector) {
                            GermPacketAPI.playSound(this, name, type, 0, volume, pitch)
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }
}