package com.bh.planners.core.effect.renderer

import com.bh.planners.core.effect.*
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.common.PlayerFrontCoordinate
import org.bukkit.util.Vector
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture
import kotlin.math.cos
import kotlin.math.sin

open class ArcRenderer(target: Target, future: CompletableFuture<Target.Container>, option: EffectOption) :
    AbstractEffectRenderer(
        target, future, option
    ) {

    open val EffectOption.startAngle: Double
        get() = Coerce.toDouble(this.demand.get("start", "0.0"))

    open val EffectOption.angle: Double
        get() = Coerce.toDouble(this.demand.get(Effects.ANGLE, "1.0"))

    open val EffectOption.radius: Double
        get() = Coerce.toDouble(this.demand.get(Effects.RADIUS, "1.0"))

    open val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    open val EffectOption.spread: Double
        get() = Coerce.toDouble(this.demand.get("spread", "0.0"))

    // 粒子渲染周期间隔
    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get(listOf("period", "p"), "0"))

    // 倾斜
    open val EffectOption.slope: Double
        get() = Coerce.toDouble(this.demand.get(listOf("slope", "slope"), "0.0"))


    override fun sendTo() {
        getContainer {
            forEachLocation {
                var i = 0.0
                val coordinate = PlayerFrontCoordinate(this)
                val spread = option.spread
                if (option.period == 0L) {
                    while (i < option.angle) {
                        val radians = Math.toRadians(i + option.startAngle)
                        val index = i / option.step
                        val radius = option.radius + (spread * index)
                        val x: Double = radius * cos(radians)
                        val z: Double = radius * sin(radians)
                        val loc = coordinate.newLocation(x, i * option.slope, z)
                        spawnParticle(location = loc)
                        i += option.step
                    }
                } else {
                    var size = option.size
                    submit(async = true, period = option.period) {
                        if (i > option.angle) {
                            if (size == 1) {
                                cancel()
                                return@submit
                            } else {
                                size--
                                i = 0.0
                            }
                        }
                        val radians = Math.toRadians(i + option.startAngle)
                        val index = i / option.step
                        val radius = option.radius + (spread * index)
                        val x: Double = radius * cos(radians)
                        val z: Double = radius * sin(radians)
                        val loc = coordinate.newLocation(x, i * option.slope, z)
                        spawnParticle(loc, loc)
                        i += option.step
                    }
                }
            }
        }
    }

}