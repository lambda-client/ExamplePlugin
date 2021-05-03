import com.lambda.client.event.SafeClientEvent
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.threads.defaultScope
import com.lambda.client.util.threads.onMainThreadSafe
import kotlinx.coroutines.launch
import net.minecraft.entity.Entity
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand

internal object ModuleExample: PluginModule(
    name = "ModuleExample",
    category = Category.MISC,
    description = "Example module which mounts entities using packets",
    pluginMain = PluginExample
) {
    private val maxReach by setting("Max Reach", 4.9f, 0.0f..8.0f, 0.1f, description = "Player's Max Reach")
    private val mountEntity = setting("Mount Entity", false, description = "Mounts the saved entity or falls back to the closest one")
    private val saveEntity = setting("Save Entity", false, description = "Saves the entity to mount")

    private var entityToMount: Entity? = null

    init {
        saveEntity.consumers.add {_, it ->
            if (it) {
                if (mc.objectMouseOver.entityHit is Entity) {
                    entityToMount = mc.objectMouseOver.entityHit as Entity
                    MessageSendHelper.sendChatMessage("$chatName Entity saved ${entityToMount!!.positionVector} ID: ${entityToMount!!.entityId}")
                } else {
                    MessageSendHelper.sendChatMessage("$chatName No Entity was found at your current cursor position!")
                }
            }
            false
        }

        mountEntity.consumers.add { _, it ->
            if (it) {
                defaultScope.launch {
                    entityToMount?.let {
                        mc.connection?.sendPacket(CPacketUseEntity(it, EnumHand.MAIN_HAND))
                    } ?: run {
                        MessageSendHelper.sendChatMessage("No entity was saved, falling back to the closest entity.")
                        onMainThreadSafe {
                            mountClosestEntity<Entity>()
                        }
                    }
                }
            }
            false
        }
    }

    private inline fun <reified T: Entity> SafeClientEvent.mountClosestEntity() {
        world.loadedEntityList.filterIsInstance<T>().filter {
            it != player.ridingEntity
        }.minByOrNull {
            it.positionVector.distanceTo(player.positionVector)
        }?.let {
            if (it.positionVector.distanceTo(player.positionVector) < maxReach || maxReach == 0.0f) {
                MessageSendHelper.sendChatMessage("$chatName Mounting: ${T::class.simpleName}@${it.positionVector}")
                defaultScope.launch {
                    connection.sendPacket(CPacketUseEntity(it, EnumHand.MAIN_HAND))
                }
            } else {
                MessageSendHelper.sendChatMessage("$chatName Closest entity too far away: ${T::class.simpleName}@${it.positionVector.distanceTo(player.positionVector)}")
            }
        } ?: run {
            MessageSendHelper.sendChatMessage("$chatName Can't find any ${T::class.simpleName} in world.")
        }
    }
}