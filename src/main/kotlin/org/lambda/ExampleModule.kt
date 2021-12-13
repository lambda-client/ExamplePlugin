package org.lambda

import com.lambda.client.event.SafeClientEvent
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.text.MessageSendHelper.sendChatMessage
import com.lambda.client.util.threads.onMainThreadSafe
import kotlinx.coroutines.runBlocking
import net.minecraft.entity.Entity
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand

internal object ExampleModule: PluginModule(
    name = "ExampleModule",
    category = Category.MISC,
    description = "Example module which mounts entities using packets",
    pluginMain = ExamplePlugin
) {
    private val maxReach by setting("Max Reach", 4.9f, 0.0f..8.0f, 0.1f, description = "Player's Max Reach")
    private val mountEntity = setting("Mount Entity", false, description = "Mounts the saved entity or falls back to the closest one")
    private val saveEntity = setting("Save Entity", false, description = "Saves the entity to mount that intersects with player view")

    private var entityToMount: Entity? = null

    init {
        saveEntity.consumers.add { _, it ->
            if (it) {
                mc.objectMouseOver?.entityHit?.let {
                    entityToMount = it
                    sendChatMessage("$chatName Entity saved ${it.positionVector} ID: ${it.entityId}")
                } ?: run {
                    sendChatMessage("$chatName No Entity was found at your current cursor position!")
                }
            }
            false
        }

        mountEntity.consumers.add { _, it ->
            if (it) {
                runBlocking {
                    entityToMount?.let {
                        mc.connection?.sendPacket(CPacketUseEntity(it, EnumHand.MAIN_HAND))
                    } ?: run {
                        sendChatMessage("No entity was saved, falling back to the closest entity.")
                        onMainThreadSafe {
                            mountClosestEntity()
                        }
                    }
                }
            }
            false
        }
    }

    private fun SafeClientEvent.mountClosestEntity() {
        world.loadedEntityList
            .filter {
                it != player.ridingEntity
            }.minByOrNull {
                player.getDistanceSq(it)
            } ?.let {
                if (maxReach == 0.0f || player.getDistance(it) <= maxReach) {
                    sendChatMessage("$chatName Mounting: ${it.name}@${it.positionVector}")
                    connection.sendPacket(CPacketUseEntity(it, EnumHand.MAIN_HAND))
                } else {
                    sendChatMessage("$chatName Closest entity too far away: ${it.name}@${it.positionVector.distanceTo(player.positionVector)}")
                }
            } ?: run {
                sendChatMessage("$chatName Can't find any Entity in world.")
            }
    }
}