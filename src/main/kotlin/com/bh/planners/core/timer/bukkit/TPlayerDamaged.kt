package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.module.kether.ScriptContext


object TPlayerDamaged : AbstractTimer<EntityDamageEvent>() {
    override val name: String
        get() = "player damaged"
    override val eventClazz: Class<EntityDamageEvent>
        get() = EntityDamageEvent::class.java

    override fun check(e: EntityDamageEvent): Target? {
        return (e.entity as? Player)?.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: EntityDamageEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["event"] = e
        context.rootFrame().variables()["cause"] = e.cause.toString()
    }
}