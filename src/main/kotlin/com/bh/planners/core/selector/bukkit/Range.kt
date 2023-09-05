package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.effect.getNearbyEntities
import com.bh.planners.core.effect.isInSphere
import com.bh.planners.core.selector.Selector
import org.bukkit.entity.LivingEntity
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 选中根据原点来定义的范围实体
 * @range 10
 * @range 5 5 5
 */
object Range : Selector {

    override val names: Array<String>
        get() = arrayOf("range", "r")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)

        val x = data.read<Double>(0, "0.0")
        val y = data.read<Double>(1, x.toString())
        val z = data.read<Double>(2, x.toString())

        return createAwaitVoidFuture {
            location.world!!.getNearbyEntities(location,x,y,z).filterIsInstance<LivingEntity>().forEach {
                data.container += it.toTarget()
            }
        }
    }
}
