package com.bh.planners.api.common

import com.bh.planners.api.Counting
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

enum class ExecuteResult {

    WAITING {
        override val handler: Player.(Skill) -> Unit
            get() = {

            }
    },
    COOLING {
        override val handler: Player.(Skill) -> Unit
            get() = { skill ->
                sendLang("skill-cast-cooling", skill.option.name, Counting.getCountdown(this, skill) / 1000)
            }
    },
    SUCCESS {
        override val handler: Player.(Skill) -> Unit
            get() = {

            }
    },
    MANA_NOT_ENOUGH {
        override val handler: Player.(Skill) -> Unit
            get() = { skill ->
                sendLang("skill-cast-mana-not-enough", skill.option.name)
            }
    },
    LEVEL_ZERO {
        override val handler: Player.(Skill) -> Unit
            get() = { skill ->
                sendLang("skill-cast-level-zero", skill.option.name)
            }
    },
    CANCELED {
        override val handler: Player.(Skill) -> Unit
            get() = {

            }

    };

    abstract val handler: Player.(Skill) -> Unit

}