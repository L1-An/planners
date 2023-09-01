package com.bh.planners.core.effect

import com.bh.planners.api.PlannersOption
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submit
import taboolib.common.util.sync
import taboolib.common5.Coerce
import taboolib.module.effect.math.Matrix
import java.util.concurrent.CompletableFuture

// 粒子渲染周期间隔
val EffectOption.period: String
    get() = this.demand.get("period", "0")!!

val EffectOption.isAnimation: Boolean
    get() = period == "0"

val EffectOption.startAngle: Double
    get() = Coerce.toDouble(this.demand.get("start", "0.0"))

val EffectOption.angle: Double
    get() = Coerce.toDouble(this.demand.get(listOf("angle", "a"), "1.0"))

val EffectOption.radius: Double
    get() = Coerce.toDouble(this.demand.get(listOf("radius", "r"), "1.0"))

val EffectOption.step: Double
    get() = Coerce.toDouble(this.demand.get(listOf("step", "s"), "1.0"))

val EffectOption.spread: Double
    get() = Coerce.toDouble(this.demand.get("spread", "0.0"))

val EffectOption.slope: Double
    get() = Coerce.toDouble(this.demand.get("slope", "0.0"))

val EffectOption.rotateAxisX: Double
    get() = Coerce.toDouble(this.demand.get(listOf("rax", "rotateAxisX"), "0"))

val EffectOption.rotateAxisY: Double
    get() = Coerce.toDouble(this.demand.get(listOf("ray", "rotateAxisY"), "0"))

val EffectOption.rotateAxisZ: Double
    get() = Coerce.toDouble(this.demand.get(listOf("raz", "rotateAxisZ"), "0"))

val EffectOption.amount: Int
    get() = Coerce.toInteger(this.demand.get("amount", "5.0"))

val EffectOption.sample: Int
    get() = Coerce.toInteger(demand.get("sample", "50"))

fun Matrix.applyBukkitVector(vector: Vector): Vector {
    if (row == 2 && column == 2) {
        return applyInBukkit2DVector(vector)
    } else if (row == 3 && column == 3) {
        return applyInBukkit3DVector(vector)
    }

    throw IllegalArgumentException("当前矩阵非 2*2 或 3*3 的方阵")

}

fun Matrix.applyInBukkit2DVector(vector: Vector): Vector {
    val x = vector.x
    val z = vector.z
    val ax = asArray[0][0] * x
    val ay = asArray[0][1] * z

    val bx = asArray[1][0] * x
    val by = asArray[1][1] * z
    return Vector(ax + ay, vector.y, bx + by)
}

fun Matrix.applyInBukkit3DVector(vector: Vector): Vector {
    val x = vector.x
    val y = vector.y
    val z = vector.z

    val ax = asArray[0][0] * x
    val ay = asArray[0][1] * y
    val az = asArray[0][2] * z

    val bx = asArray[1][0] * x
    val by = asArray[1][1] * y
    val bz = asArray[1][2] * z

    val cx = asArray[2][0] * x
    val cy = asArray[2][1] * y
    val cz = asArray[2][2] * z

    return Vector(ax + ay + az, bx + by + bz, cx + cy + cz)
}

private fun createBoundingBox(location: Location): BoundingBox {
    return BoundingBox.of(
        location,
        PlannersOption.scopeThreshold[0],
        PlannersOption.scopeThreshold[1],
        PlannersOption.scopeThreshold[2]
    )
}

private fun Location.getNearbyEntities(): List<LivingEntity> {
    return world!!.getNearbyEntities(
        this,
        PlannersOption.scopeThreshold[0],
        PlannersOption.scopeThreshold[1],
        PlannersOption.scopeThreshold[2]
    ).filterIsInstance<LivingEntity>()
}

fun Location.getNearbyEntities(radius: Double): List<LivingEntity> {
    return world!!.getNearbyEntities(
        this,
        radius,
        radius,
        radius
    ).filterIsInstance<LivingEntity>()
}

fun createAwaitVoidFuture(block: () -> Unit): CompletableFuture<Void> {
    return if (isPrimaryThread) {
        block()
        CompletableFuture.completedFuture(null)
    } else {
        sync { block() }
        CompletableFuture.completedFuture(null)
    }
}

fun <T> createAwaitFuture(block: () -> T): CompletableFuture<T> {
    if (isPrimaryThread) {
        error("Cannot run sync task in main thread.")
    }
    val future = CompletableFuture<T>()
    submit { future.complete(block()) }
    return future
}

fun Location.capture(): CompletableFuture<List<LivingEntity>> {
    return createAwaitFuture { this.getNearbyEntities() }
}
