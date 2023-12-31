package com.bh.planners.command

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.addExperience
import com.bh.planners.api.hasJob
import com.bh.planners.core.kether.bukkitPlayer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("plannersexp")
object PlannersExperienceCommand {


    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val give = subCommand {
        player {
            dynamic("value") {
                execute<ProxyCommandSender> { _, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.plannersProfile.addExperience(Coerce.toInteger(argument))
                        player.sendLang("player-get-experience", argument, player.plannersProfile.level)
                    }
                }
            }
        }
    }

    @CommandBody
    val take = subCommand {
        player {
            dynamic("value") {
                execute<ProxyCommandSender> { _, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.sendLang("player-take-experience", argument, player.plannersProfile.level)
                        player.plannersProfile.addExperience(-Coerce.toInteger(argument))
                    }
                }
            }
        }
    }


}