package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.actionContainer
import com.bh.planners.core.kether.common.KetherHelper.actionContainerOrOrigin
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.game.ActionVelocity.generatedVelocity
import com.bh.planners.core.kether.nextSelector
import com.bh.planners.core.kether.origin
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

@CombinationKetherParser.Used
fun drag() = KetherHelper.simpleKetherParser<Unit>{
    it.group(double(),actionContainer(), command("pos", then = actionContainerOrOrigin()).option()).apply(it) { step, container, target ->
        now {
            val location = target?.firstBukkitLocation() ?: origin().value
            container.forEachProxyEntity {
                val vectorAB = this.location.clone().subtract(location).toVector()
                vectorAB.normalize()
                vectorAB.multiply(step)
                this.velocity = vectorAB
            }
        }
    }
}