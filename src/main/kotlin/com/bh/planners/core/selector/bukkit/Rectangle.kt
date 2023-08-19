package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.effect.isInAABB
import com.bh.planners.core.selector.Selector
import org.bukkit.util.Vector
import taboolib.common.platform.function.submitAsync
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 视角前长方形
 * Long 长
 * wide 宽
 * high 高
 * forward 向前偏移
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

        return createAwaitVoidFuture {

            location.world?.livingEntities?.forEach {

                submitAsync {
                    val offset = sqrt(it.width.pow(2)*2)

                    val vectorX1 = location.direction.clone().setY(0).normalize().multiply(forward+offset)
                    val vectorY1 = Vector(0.0,-(high/2+it.height),0.0)
                    val vectorZ1 = location.direction.clone().setY(0).crossProduct(Vector(0,1,0)).normalize().multiply(wide/2+offset)

                    val vector1 = location.clone().add(vectorX1).add(vectorY1).add(vectorZ1)

                    val vectorX2 = location.direction.clone().setY(0).normalize().multiply(forward+long+offset)
                    val vectorY2 = Vector(0.0,high/2-it.height,0.0)
                    val vectorZ2 = location.direction.clone().setY(0).crossProduct(Vector(0,1,0)).normalize().multiply(-(wide/2+offset))

                    val vector2 = location.clone().add(vectorX2).add(vectorY2).add(vectorZ2)

                    if (it.location.isInAABB(vector1, vector2).apply { data.context.player?.sendMessage("$this, ${it.location}") }) {
                        data.container += it.toTarget()
                    }
                }

            }
        }

    }

}