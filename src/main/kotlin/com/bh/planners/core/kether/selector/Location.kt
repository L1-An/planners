package com.bh.planners.core.kether.selector

import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.toLocation
import com.bh.planners.core.pojo.Session

/**
 * 选中具体坐标
 * -@loc world,0,0,0
 * -@location world,0,0,0
 * -@l world,0,0,0
 */
object Location : Selector {
    override val names: Array<String>
        get() = arrayOf("loc", "location", "l")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        container.add(args.toLocation().toTarget())
    }


}
