package com.bh.planners.core.kether.enhance

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionSkillCast {

    class TryCast(val skill: ParsedAction<*>, val selector: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(skill).run<Any>().thenAccept { skill ->
                frame.createTargets(selector).thenAccept { container ->
                    container.forEachPlayer {
                        PlannersAPI.cast(this, skill.toString(), true)
                    }
                }
            }
        }
    }

    class DirectCast(val skill: ParsedAction<*>, val level: ParsedAction<*>, val selector: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(skill).run<Any>().thenAccept {
                val skill = PlannersAPI.getSkill(it.toString()) ?: return@thenAccept
                frame.newFrame(level).run<Any>().thenAccept {
                    val level = Coerce.toInteger(it)
                    frame.createTargets(selector).thenAccept { container ->
                        container.forEachPlayer {
                            ContextImpl(adaptPlayer(this), skill, level).cast()
                        }
                    }
                }
            }
        }
    }


    class ContextImpl(executor: ProxyCommandSender, skill: Skill, val level: Int) : Session(executor, skill) {
        override val playerSkill = PlayerJob.Skill(-1, skill.key, level, null)
    }

    companion object {

        /**
         * 为目标玩家尝试释放技能
         *
         * 满足蓝量条件
         * 满足冷却条件
         * 满足拥有该技能条件
         *
         * try-cast "def0" "-@self"
         */
        @KetherParser(["try-cast"], namespace = NAMESPACE)
        fun parser1() = scriptParser {
            TryCast(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }

        /**
         * 为目标玩家释放技能
         * 不参与任何条件限制 直接指定等级释放
         *
         * direct-cast "def0" 1 "-@self"
         */
        @KetherParser(["direct-cast"], namespace = NAMESPACE)
        fun parser2() = scriptParser {
            DirectCast(it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION), it.next(ArgTypes.ACTION))
        }
    }

}