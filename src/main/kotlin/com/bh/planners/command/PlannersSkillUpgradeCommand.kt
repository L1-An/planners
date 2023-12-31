package com.bh.planners.command

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.add
import com.bh.planners.api.hasJob
import com.bh.planners.api.set
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.ui.Faceplate
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

@CommandHeader("plannersskillup")
object PlannersSkillUpgradeCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val ui = subCommand {

        player {
            dynamic("skill") {
                suggestion<ProxyCommandSender> { _, context ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }

                execute<ProxyCommandSender> { _, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        Faceplate(player, PlannersAPI.getSkill(argument) ?: return@execute).open()
                    }
                }
            }
        }
    }

    @CommandBody
    val set = subCommand {

        player {
            dynamic("skill") {
                suggestion<ProxyCommandSender> { _, context ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }

                dynamic("number") {
                    execute<ProxyCommandSender> { _, context, argument ->
                        val player = context.player("player").bukkitPlayer()!!
                        if (player.hasJob) {
                            val profile = player.plannersProfile
                            val skill = profile.getSkill(context["skill"]) ?: return@execute
                            profile.set(skill, argument.toInt())
                        }
                    }
                }
            }
        }
    }

    @CommandBody
    val add = subCommand {

        player {
            dynamic("skill") {
                suggestion<ProxyCommandSender> { _, context ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.plannersProfile.getSkills().map { it.key }
                    } else emptyList()
                }

                dynamic("number") {
                    execute<ProxyCommandSender> { _, context, argument ->
                        val player = context.player("player").bukkitPlayer()!!
                        if (player.hasJob) {
                            val profile = player.plannersProfile
                            val skill = profile.getSkill(context["skill"]) ?: return@execute
                            profile.add(skill, argument.toInt())
                        }
                    }
                }
            }
        }
    }
}