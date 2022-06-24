package com.bh.planners.core.pojo

import com.bh.planners.core.timer.Template
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration

open class Skill(val key: String, val config: ConfigurationSection) {

    open val option = Option(config.getConfigurationSection("__option__") ?: config.createSection("__option__"))
    open val action = config.getString("action", "")!!

    open val events = config.getConfigurationSection("events")?.getKeys(false)?.map {
        EventProcessor(it, config.getConfigurationSection("events.$it")!!)
    } ?: emptyList()

    open class Option(val root: ConfigurationSection) {
        open val name = root.getString("name", root.name)!!
        open val levelCap = root.getInt("level-cap", 5)
        open val async = root.getBoolean("async", false)
        open val isBind = root.getBoolean("bind", false)

        @Suppress("UNCHECKED_CAST")
        open val upgradeConditions = root.getMapList("upgrade-condition").map {
            UpgradeCondition(Configuration.fromMap(it))
        }

        open val variables = root.getConfigurationSection("variables")?.getKeys(false)?.map {
            Variable(it, root.getString("variables.$it")!!)
        } ?: emptyList()

    }

    /**
     * 目的是为了以后有拓展不影响其他条件条目
     */
    class UpgradeCondition(option: ConfigurationSection) : Condition(option)

    class Variable(val key: String, val expression: String)

    class EventProcessor(id: String, root: ConfigurationSection) : Template(id, root) {
        override val triggers: List<String>
            get() = emptyList()


    }

    class Empty : Skill("", Configuration.empty()) {

        override val action: String = ""

        override val option: Option = EmptyOption()

    }

    class EmptyOption : Option(Configuration.empty()) {
        override val name: String = ""
        override val levelCap: Int = -1
        override val variables: List<Variable> = emptyList()
    }

}