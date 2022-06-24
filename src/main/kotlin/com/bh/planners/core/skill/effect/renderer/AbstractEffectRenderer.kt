package com.bh.planners.core.skill.effect.renderer

import com.bh.planners.core.skill.effect.EffectOption
import com.bh.planners.core.skill.effect.EffectSpawner
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.applyBukkitVector
import com.bh.planners.core.skill.effect.common.Matrix
import org.bukkit.Location

abstract class AbstractEffectRenderer(val target: Target, val container: Target.Container, val option: EffectOption) :
    EffectRenderer {


    protected var matrix: Matrix? = null

    val spawner = EffectSpawner(option)

    fun hasMatrix(): Boolean {
        return matrix != null
    }

    /**
     * 通过给定一个坐标就可以使用已经指定的参数来播放粒子
     * @param location 坐标
     */
    fun spawnParticle(origin: Location? = null, location: Location) {
        var showLocation = location
        if (hasMatrix() && origin != null) {
            val vector = location.clone().subtract(origin).toVector()
            val changed = matrix!!.applyBukkitVector(vector)
            showLocation = origin.clone().add(changed)
        }
        spawner.spawn(showLocation)
    }

}