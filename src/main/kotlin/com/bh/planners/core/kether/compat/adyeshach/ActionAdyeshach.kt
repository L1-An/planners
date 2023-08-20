package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.api.entity.ProxyAdyeshachEntity
import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.ParameterKetherParser
import ink.ptms.adyeshach.common.entity.EntityTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*

@CombinationKetherParser.Used
object ActionAdyeshach : ParameterKetherParser("ady", "adyeshach") {
    val spawn = argumentKetherParser {
        val reader = this
        actionNow {
            run(it).str {
                val type = EntityTypes.valueOf(it.uppercase(Locale.getDefault()))
                run(reader.nextParsedAction()).str { name ->
                    run(reader.nextParsedAction()).long { timer ->
                        containerOrSender(reader.nextSelectorOrNull()).thenAccept { container ->
                            val entity = container.filterIsInstance<Target.Location>().map { it.value }
                            ActionAdyeshachSpawn().apply {
                                this.type = type
                                this.name = name
                                this.timeout = timer
                                this.selector = entity
                            }
                        }
                    }
                }
            }
        }
    }


//object ActionAdyeshach {


    /**
     * adyeshach spawn type name tick
     * adyeshach follow <option: action> [owner:first] [selector:entity]
     *
     * adyeshach script file args[] selector
     *
     * adyeshach remove [selector]
     *
     */
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


}