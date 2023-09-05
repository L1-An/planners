package com.bh.planners.command

import com.bh.planners.Planners
import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.api.script.ScriptLoader
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common5.Mirror
import taboolib.expansion.createHelper
import taboolib.module.chat.colored
import taboolib.module.kether.runKether

@CommandHeader("planners", aliases = ["ps", "pl"], permission = "planners.command")
object PlannersCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val reload = subCommand {

        execute<ProxyCommandSender> { sender, _, _ ->
            Planners.config.reload()
            PluginReloadEvent().call()
            sender.sendMessage("reload successful.")
        }

    }

    @CommandBody
    val job = PlannersJobCommand

    @CommandBody
    val point = PlannersPointCommand

    @CommandBody
    val level = PlannersLevelCommand

    @CommandBody
    val exp = PlannersExperienceCommand

    @CommandBody
    val transfer = PlannersTransferCommand

    @CommandBody
    val mana = PlannersManaCommand

    @CommandBody
    val skill = PlannersSkillCommand

    @CommandBody
    val shortcut = PlannersShortcutCommand

    @CommandBody
    val info = subCommand {
        player {
            execute<ProxyCommandSender> { sender, _, argument ->
                val playerExact = Bukkit.getPlayerExact(argument) ?: return@execute
                runKether {
                    ScriptLoader.createFunctionScript(ContextAPI.create(playerExact), PlannersOption.infos.get()).forEach {
                        sender.sendMessage(it.colored())
                    }
                }
            }
        }
    }

    @CommandBody
    val report = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            // 打印统计结果
            Mirror.report(sender)
        }
    }

}
