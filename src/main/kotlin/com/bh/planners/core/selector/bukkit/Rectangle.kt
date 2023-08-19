package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.effect.isInAABB
import com.bh.planners.core.selector.Selector
import org.bukkit.util.Vector
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 视角前长方形
 * Long 长
 * wide 宽
 * high 高
 * forward 前后偏移
 * offsetY 上下偏移
 *
 * @rectangle Long wide high forward
 */
object Rectangle : Selector {

    override val names: Array<String>
        get() = arrayOf("rectangle", "rec")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)

        val long = data.read<Double>(0, "0.0")
        val wide = data.read<Double>(1, "0.0")
        val high = data.read<Double>(2, "0.0")
        val forward = data.read<Double>(3, "0.0")
        val offsetY = data.read<Double>(4, "0.0")

        return createAwaitVoidFuture {

            val entities = location.world?.livingEntities ?: return@createAwaitVoidFuture

            entities.forEach {

                val offset = sqrt(it.width.pow(2) * 2)

                val vectorX1 = location.direction.setY(0).normalize()
                val vectorZ1 = vectorX1.clone().crossProduct(Vector(0, 1, 0))

                val loc1 = location.clone().add(vectorX1.multiply(forward + offset)).add(vectorZ1.multiply(-(wide / 2 + offset))).apply { y += offsetY }

                val vectorX2 = location.direction.setY(0).normalize()
                val vectorZ2 = vectorX2.clone().crossProduct(Vector(0, 1, 0))

                val loc2 = location.clone().add(vectorX2.multiply(forward + long + offset)).add(vectorZ2.multiply(wide / 2 + offset)).apply { y += (high + it.height + offsetY) }

                data.context.player?.sendMessage("${loc1.x}|${loc1.y}|${loc1.z}, ${loc2.x}|${loc2.y}|${loc2.z}")

                if (it.eyeLocation.isInAABB(loc1, loc2)
                        .apply { data.context.player?.sendMessage("$location, ${data.context.player?.eyeLocation?.direction}, ${data.context.player?.eyeLocation}") }
                ) {
                    data.container += it.toTarget()
                }

            }
        }

    }

}