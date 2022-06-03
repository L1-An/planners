package com.bh.planners.core.kether

import com.bh.planners.api.GlobalVarTable
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.actions.LiteralAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGlobalVariable {

    class Get(val action: ParsedAction<*>) : ScriptAction<String>() {
        override fun run(frame: ScriptFrame): CompletableFuture<String> {
            return frame.newFrame(action).run<String>().thenApply {
                GlobalVarTable.get(it)?.data.toString()
            }
        }
    }

    class Set(val action: ParsedAction<*>, val value: ParsedAction<*>, val timeout: ParsedAction<*>) :
        ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.newFrame(action).run<String>().thenAccept { key ->
                frame.newFrame(value).run<Any>().thenAccept { value ->
                    frame.newFrame(timeout).run<String>().thenAccept { timeout ->
                        GlobalVarTable.set(key, value, Coerce.toLong(timeout))
                    }
                }
            }

            return CompletableFuture.completedFuture(null)
        }
    }

    class Keys(val action: ParsedAction<*>) : ScriptAction<List<String>>() {

        override fun run(frame: ScriptFrame): CompletableFuture<List<String>> {
            val keys = GlobalVarTable.keys()
            return frame.newFrame(action).run<String>().thenApply { keyword ->
                if (keyword == "*") {
                    keys
                } else {
                    if (keyword[0] == '!') {
                        val keyword = keyword.substring(1)
                        keys.filter { keyword !in it }
                    } else {
                        keys.filter { keyword in it }
                    }
                }
            }
        }

    }

    companion object {

        /**
         * 公共变量
         *
         * 取
         * global get [key]
         *
         * 存
         * global set [key] to [value]
         *
         * 所有键 check 匹配关键字
         * global keys [check]
         */
        @KetherParser(["global"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("get") {
                    Get(it.next(ArgTypes.ACTION))
                }
                case("set") {
                    val key = it.next(ArgTypes.ACTION)
                    it.expect("to")
                    val value = it.next(ArgTypes.ACTION)
                    mark()
                    val timeout = try {
                        it.expect("timeout")
                        it.next(ArgTypes.ACTION)
                    } catch (e: Exception) {
                        reset()
                        ParsedAction(LiteralAction<Long>("-1"))
                    }
                    Set(key, value, timeout)
                }
                case("keys") {
                    mark()
                    val check = try {
                        expect("check")
                        it.next(ArgTypes.ACTION)
                    } catch (e: Exception) {
                        reset()
                        ParsedAction(LiteralAction<String>("*"))
                    }
                    Keys(check)
                }
            }
        }

    }


}