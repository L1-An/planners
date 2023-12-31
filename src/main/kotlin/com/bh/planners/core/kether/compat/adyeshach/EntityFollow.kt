package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.common.Demand.Companion.toDemand
import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.kether.catchRunning
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.RayTrace
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce

/**
 * @author Arasple
 * @date 2020/8/25 14:08
 */
object EntityFollow {

    class Option(var entity: EntityInstance, val source: String) {
        val demand = source.toDemand()
        val distance = Coerce.toDouble(demand.get(listOf("distance", "d"), "1.5"))
        val yaw = Coerce.toFloat(demand.get("yaw", "0"))
        val pitch = Coerce.toFloat(demand.get("pitch", "180"))

    }

    fun process(entity: ProxyEntity, option: Option): Boolean {
        if (option.entity.isDeleted) {
            return false
        }

        val location = entity.location.clone()
        location.pitch = option.pitch
        location.yaw = location.yaw + option.yaw
        val rayTrace = RayTrace(location.toVector(), location.direction)
        val teleport = rayTrace.distance(option.distance).toLocation(entity.world)
        option.entity.teleport(teleport)

        // 位置变换了调整视角
        option.entity.setHeadRotation(entity.eyeLocation.yaw, entity.eyeLocation.pitch)

        return true
    }

    fun select(entity: ProxyEntity, entityInstance: EntityInstance, source: String) {
        catchRunning {
            val handler = Option(entityInstance, source)
            submit(async = true, period = 1) {
                if (entity.isDead || !process(entity, handler)) {
                    cancel()
                }
            }
        }
    }

}