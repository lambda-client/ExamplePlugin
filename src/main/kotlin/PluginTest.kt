import com.lambda.client.module.Category
import com.lambda.client.plugin.api.Plugin
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.text.MessageSendHelper

internal class TestPlugin: Plugin() {

    override fun onLoad() {
        this.modules.add(TestModule())
        MessageSendHelper.sendChatMessage("Hi")
    }

    override fun onUnload() {
        MessageSendHelper.sendChatMessage("Bye")
    }
}

internal class TestModule: PluginModule(
    name = "TestModule",
    category = Category.MISC,
    description = "We do a little testing",
    pluginMain = TestPlugin()
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