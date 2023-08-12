package com.bh.planners.core.kether.common

import taboolib.library.kether.*
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture


abstract class ParameterKetherParser(vararg id: String) : MultipleKetherParser(*id) {

    protected lateinit var argument: ParsedAction<*>

    override fun run(): ScriptActionParser<Any?> {
        info("parameter parser")
        return scriptParser {
            this.argument = it.nextParsedAction()
            super.run().resolve(it)
        }
    }
    
    protected fun argumentKetherNow(vararg id: String, func: ScriptFrame.(argument: Any?) -> Any?): SimpleKetherParser {
        return simpleKetherParser(*id) {
            argumentNow { func(this,it) }
        }
    }

    protected fun argumentNow(func: ScriptFrame.(argument: Any?) -> Any?): ScriptActionParser<out Any?> {
        return scriptParser {
            actionNow {
                run(this@ParameterKetherParser.argument).thenApply {
                    func(this, it)
                }
            }
        }
    }

    protected fun <T> ParserHolder.argumentNow(action: ScriptFrame.(argument: Any) -> T): Parser.Action<T> {
        return Parser.Action {
            it.run(argument).thenApply { argument ->
                action(it, argument!!)
            }
        }
    }

    override fun toString(): String {
        return "ParameterKetherParser(argument=$argument)"
    }


}