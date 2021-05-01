import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.text.MessageSendHelper

internal object TestModule: PluginModule(
    name = "TestModule",
    category = Category.MISC,
    description = "We do a little testing",
    pluginMain = TestPlugin
) {
    init {
        onEnable {
            MessageSendHelper.sendChatMessage("Hi :)")
        }

        onDisable {
            MessageSendHelper.sendChatMessage("Bye :(")
        }
    }
}