package com.bh.planners.core.kether.compat.attribute

import me.skymc.customized.monsteritem.api.event.AttributeLoadedEvent
import me.skymc.customized.monsteritem.core.attribute.Attribute
import me.skymc.customized.monsteritem.core.attribute.Value
import me.skymc.customized.monsteritem.core.attribute.function.FunctionProfile
import me.skymc.customized.monsteritem.core.attribute.source.DefaultSource
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.registerBukkitListener
import java.util.*

class MonsterItemBridge : AttributeBridge {

    val listener = registerBukkitListener(AttributeLoadedEvent.Post::class.java) { event ->
        AttributeBridge.updateJob(event.player)
        AttributeBridge.updateSkill(event.player)
    }

    override fun addAttributes(uuid: UUID, timeout: Long, reads: List<String>) {
        this.addAttributes(UUID.randomUUID().toString(), uuid, timeout, reads)
    }

    override fun addAttributes(source: String, uuid: UUID, timeout: Long, reads: List<String>) {
        val profile = FunctionProfile.getPlayerProfileByUUID(uuid) ?: return
        val monsterItemSource = DefaultSource()
        reads.forEach {
            val split = it.split(":")
            val id = split[0].trim().uppercase(Locale.getDefault())
            val value = split[1].trim()
            monsterItemSource.addValue(Attribute.valueOf(id), Value.read(value)!!)
        }
        if (timeout == -1L) {
            profile.putAttributeSource(source, monsterItemSource, true)
        } else {
            profile.putTemporaryAttributeSource(source, monsterItemSource, timeout)
        }
    }

    override fun removeAttributes(uuid: UUID, source: String) {
        FunctionProfile.getMobProfileByUUID(uuid)?.removeAttributeSource(source)
    }

    override fun update(entity: LivingEntity) {
    }

    override fun update(uuid: UUID) {
    }

    override fun get(uuid: UUID, keyword: String): Any {
        return FunctionProfile.getMobProfileByUUID(uuid)?.getAttributeValue(Attribute.valueOf(keyword))?.print() ?: "__NULL__"
    }
}