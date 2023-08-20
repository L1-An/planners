package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.entity.ProxyAdyeshachEntity
import com.bh.planners.api.entity.ProxyEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import com.bh.planners.core.kether.common.MultipleKetherParser
import ink.ptms.adyeshach.common.entity.EntityTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*

/**
 * adyeshach spawn type name tick
 * adyeshach follow <option: action> [owner:first] [selector:entity]
 *
 * adyeshach script file args[] selector
 *
 * adyeshach remove [selector]
 *
 */
@CombinationKetherParser.Used
object ActionAdyeshach : MultipleKetherParser("ady", "adyeshach") {

    val spawn = simpleKetherParser<List<ProxyEntity>> {
        it.group(text(), text(), long(), containerOrSender()).apply(it) { type, name, timeout, container ->
            now {
                val adyType = EntityTypes.valueOf(type.uppercase())
                ActionAdyeshachSpawn.spawn(
                    adyType,
                    container.mapNotNull { loc -> loc.getLocation() },
                    name,
                    timeout
                ).get()
            }
        }
    }

    fun Target.Container.foreachAdyEntity(block: ProxyAdyeshachEntity.() -> Unit) {
        this.forEach<Target.Entity> {
            if (this.proxy is ProxyAdyeshachEntity) {
                block(this.proxy)
            }
        }
    }

    fun ScriptFrame.execAdyeshachEntity(selector: ParsedAction<*>, call: ProxyAdyeshachEntity.() -> Unit) {
        exec(selector) {
            if (this is Target.Entity && this.proxy is ProxyAdyeshachEntity) {
                call(this.proxy)
            }
        }
    }


//object ActionAdyeshach {



//    @KetherParser(["adyeshach", "ady"], namespace = NAMESPACE, shared = true)
//    fun parser() = scriptParser {
//        it.switch {
//            case("spawn") {
//                ActionAdyeshachSpawn().apply {
//                    this.type = it.nextParsedAction()
//                    this.name = it.nextParsedAction()
//                    this.timeout = it.nextParsedAction()
//                    this.selector = it.nextSelectorOrNull()
//                }
//            }
//            case("follow") {
//                ActionAdyeshachFollow(
//                    it.nextParsedAction(),
//                    it.nextParsedAction(),
//                    it.nextOptionalAction(arrayOf("option", "params"), "EMPTY")!!
//                )
//            }
//            case("script") {
//                ActionAdyeshachScript(
//                    it.nextParsedAction(),
//                    it.next(ArgTypes.listOf(ArgTypes.ACTION)),
//                    it.nextSelectorOrNull()
//                )
//            }
//            case("remove") {
//                ActionAdyeshachRemove(it.nextSelector())
//            }
//        }
//    }

}