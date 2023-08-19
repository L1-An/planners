package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

// 过滤器 过滤pvp
object PVP : Selector {
    override val names: Array<String>
        get() = arrayOf("pvp")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        data.container.removeIf { it.getPlayer()?.world?.pvp != true }
        return CompletableFuture.completedFuture(null)
    }


}