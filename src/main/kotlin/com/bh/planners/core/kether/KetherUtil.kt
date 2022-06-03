package com.bh.planners.core.kether

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.util.StringNumber
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.kether.QuestContext
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.type.BukkitPlayer
import taboolib.platform.util.toBukkitLocation

const val NAMESPACE = "Planners"

val namespaces = listOf(NAMESPACE)


fun ScriptFrame.getSession(): Session {
    return rootVariables().get<Session>("@Session").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.executor(): ProxyCommandSender {
    return getSession().executor
}

fun ProxyCommandSender.asPlayer() : Player? {
    if (this is BukkitPlayer) {
        return player
    }
    return null
}

fun ScriptFrame.asPlayer(): Player {
    return getSession().asPlayer
}

fun ScriptFrame.getSkill(): PlayerJob.Skill {
    return rootVariables().get<PlayerJob.Skill>("@Skill").orElse(null) ?: error("Error running environment !")
}

fun ScriptFrame.toOriginLocation(): Target.Location? {

    val optional = rootVariables().get<Location>("@Origin")
    if (optional.isPresent) {
        return optional.get().toTarget()
    }
    val executor = executor()
    if (executor is BukkitPlayer) {
        return executor.player.toTarget()
    }
    return Any().toLocation().toTarget()
}

fun ScriptFrame.rootVariables(): QuestContext.VarTable {
    var vars = variables()
    var parent = parent()
    while (parent.isPresent) {
        vars = parent.get().variables()
        parent = parent.get().parent()
    }
    return vars
}

fun Any?.increaseAny(any: Any): Any {
    this ?: return any
    return StringNumber(toString()).add(any.toString()).get()
}

fun evalKether(player: Player, action: String, skill: PlayerJob.Skill): String? {
    return try {
        KetherShell.eval(action, sender = adaptPlayer(player), namespace = namespaces) {
            this.rootFrame().variables()["@Skill"] = skill
        }.get()?.toString()
    } catch (e: Throwable) {
        e.printKetherErrorMessage()
        return null
    }
}


fun Any.toLocation(): Location {
    return when (this) {
        is Location -> this
        is String -> {
            val split = split(",")
            Location(
                Bukkit.getWorld(split[0]),
                Coerce.toDouble(split[1]),
                Coerce.toDouble(split[2]),
                Coerce.toDouble(split[3])
            )
        }
        else -> Location(null, 0.0, 0.0, 0.0)
    }
}