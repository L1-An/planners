package com.bh.planners.core.kether.game

import com.bh.planners.api.common.Operator
import com.bh.planners.core.kether.*
import com.bh.planners.util.eval
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionHealth {

    class HealthOperation(val mode: Operator, val value: ParsedAction<*>, val selector: ParsedAction<*>?) :
        ScriptAction<Void>() {

        fun execute(entity: LivingEntity, value: String) {
            val newvalue = value.eval(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0)
            val result = when (mode) {
                Operator.ADD -> entity.health + newvalue
                Operator.TAKE -> entity.health - newvalue
                Operator.SET -> newvalue
                else -> entity.health
            }.coerceAtLeast(0.0).coerceAtMost(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0)
            if (!entity.isDead && entity.health > 0) {
                entity.health = result
            }
        }

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(value).run<Any>().thenAccept {
                catchRunning {
                    if (selector != null) {
                        frame.execLivingEntity(selector) { execute(this, it.toString()) }
                    } else {
                        execute(frame.bukkitPlayer() ?: return@catchRunning, it.toString())
                    }
                }
            }
        }


    }

    companion object {

        /**
         * health add 10 <they "@range 3">
         * health take 10 <they "@range 3">
         * health set 10 <they "@range 3">
         */
        @KetherParser(["health"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val operator = when (it.nextToken()) {
                "add" -> Operator.ADD
                "set" -> Operator.SET
                "take" -> Operator.TAKE
                else -> error("error of case!")
            }
            HealthOperation(operator, it.nextParsedAction(), it.nextSelectorOrNull())
        }

    }


}