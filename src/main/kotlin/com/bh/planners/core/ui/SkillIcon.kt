package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.library.xseries.parseToMaterial
import taboolib.module.chat.colored
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.kether.printKetherErrorMessage
import taboolib.platform.util.buildItem

class SkillIcon(val player: Player, skillKey: String, val level: Int, conceal: Boolean = false) {

    companion object {

        fun PlayerJob.Skill.toIcon(player: Player, conceal: Boolean = false): SkillIcon {
            return toIcon(player, this.level, conceal)
        }

        fun PlayerJob.Skill.toIcon(player: Player, level: Int, conceal: Boolean = false): SkillIcon {
            return SkillIcon(player, key, level, conceal)
        }

        fun PlayerJob.Skill.buildIconItem(player: Player): ItemStack {
            return toIcon(player).build()
        }

    }

    private val skill = PlannersAPI.getSkill(skillKey)!!

    val context = Context.Impl1(adaptPlayer(player),skill,level)

    private val option = skill.option

    fun build(): ItemStack {
        return buildItem(option.root.getString("icon.material")!!.parseToMaterial()) {
            name = format(option.root.getString("icon.name")!!)
            lore += option.root.getStringList("icon.lore").map { format(it) }
            damage = option.root.getInt("damage")
        }
    }

    fun format(str: String): String {
        return try {
            KetherFunction.parse(str, sender = adaptPlayer(player), namespace = namespaces) {
                rootFrame().rootVariables()["@Context"] = context
                context.variables.forEach {
                    rootFrame().variables()[it.key] = it.value
                }
            }.colored()
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            "&cError: $str".colored()
        }
    }

}