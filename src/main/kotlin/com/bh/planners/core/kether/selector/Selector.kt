package com.bh.planners.core.kether.selector

import com.bh.planners.api.common.Demand
import com.bh.planners.api.common.Plugin
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.EffectOption
import com.bh.planners.core.skill.effect.Target
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

interface Selector {

    companion object {

        val selectors = mutableListOf<Selector>()

        fun getSelector(string: String): Selector {
            return selectors.firstOrNull { string in it.names } ?: error("Selector '${string}' not found")
        }

        fun check(target: Target?, context: Context, option: EffectOption, container: Target.Container) {
            check(target, context, option.demand, container)
        }

        fun check(target: Target?, context: Context, demand: Demand, container: Target.Container) {
            demand.dataMap.keys.filter { it.startsWith('@') }.forEach {
                val selector = getSelector(it.substring(1))
                demand.dataMap[it]!!.forEach { s ->
                    selector.check(it.substring(1), target, s, context, container)
                }
            }
        }

        @Awake(LifeCycle.LOAD)
        fun load() {
            runningClasses.forEach {
                if (Selector::class.java.isAssignableFrom(it)) {
                    if (it.isAssignableFrom(Plugin::class.java)) {
                        val annotation = it.getAnnotation(Plugin::class.java)
                        if (!Bukkit.getPluginManager().isPluginEnabled(annotation.name)) {
                            return@forEach
                        }
                    }

                    (it.getInstance()?.get() as? Selector)?.let { selector ->
                        selectors += selector
                    }
                }
            }
        }

    }

    val names: Array<String>

    fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container)

    fun String.isNon(): Boolean {
        return get(0) == '!'
    }

}
