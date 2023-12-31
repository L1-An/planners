package com.bh.planners.command

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.addPoint
import com.bh.planners.api.hasJob
import com.bh.planners.api.setPoint
import com.bh.planners.core.kether.bukkitPlayer
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("plannerspoint")
object PlannersPointCommand {

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
                        player.sendLang("player-get-point", argument)
                        player.plannersProfile.addPoint(Coerce.toInteger(argument))
                    }
                }
            }
            execute<ProxyCommandSender> { _, _, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-get-point", 1)
                    player.plannersProfile.addPoint(1)
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
                        player.sendLang("player-take-point", argument)
                        player.plannersProfile.addPoint(-Coerce.toInteger(argument))
                    }
                }
            }
            execute<ProxyCommandSender> { _, _, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-take-point", 1)
                    player.plannersProfile.addPoint(-1)
                }
            }
        }
    }

    @CommandBody
    val set = subCommand {
        player {
            dynamic("value") {
                execute<ProxyCommandSender> { _, context, argument ->
                    val player = context.player("player").bukkitPlayer()!!
                    if (player.hasJob) {
                        player.sendLang("player-set-point", argument)
                        player.plannersProfile.setPoint(Coerce.toInteger(argument))
                    }
                }
            }
            execute<ProxyCommandSender> { _, _, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-clear-point", 0)
                    player.plannersProfile.setPoint(0)
                }
            }
        }
    }

    @CommandBody
    val clear = subCommand {
        player {
            execute<ProxyCommandSender> { _, _, argument ->
                val player = Bukkit.getPlayerExact(argument)!!
                if (player.hasJob) {
                    player.sendLang("player-clear-point", 0)
                    player.plannersProfile.setPoint(0)
                }
            }
        }
    }
}
