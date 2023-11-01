package com.bh.planners

import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.common5.Mirror
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin

@RuntimeDependency(
    value = "!org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3",
    test = "!kotlinx.coroutines.GlobalScope",
    initiative = true
)
object Planners : Plugin() {

    @Config("config.yml")
    lateinit var config: Configuration

    override fun onEnable() {
        Metrics(15573, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
        console().sendMessage("&a┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".colored())
        console().sendMessage("&a┃&e    _/_/_/    _/                                                           ".colored())
        console().sendMessage("&a┃&e   _/    _/  _/    _/_/_/  _/_/_/    _/_/_/      _/_/    _/  _/_/    _/_/_/".colored())
        console().sendMessage("&a┃&e  _/_/_/    _/  _/    _/  _/    _/  _/    _/  _/_/_/_/  _/_/      _/_/     ".colored())
        console().sendMessage("&a┃&e _/        _/  _/    _/  _/    _/  _/    _/  _/        _/            _/_/  ".colored())
        console().sendMessage("&a┃&e_/        _/    _/_/_/  _/    _/  _/    _/    _/_/_/  _/        _/_/_/     ".colored())
        console().sendMessage("&a┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━".colored())
    }

    override fun onDisable() {
        Mirror.report(adaptCommandSender(Bukkit.getServer().consoleSender))
    }
}
