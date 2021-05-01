import com.lambda.client.plugin.api.Plugin
import com.lambda.client.util.text.MessageSendHelper

internal object TestPlugin: Plugin() {

    override fun onLoad() {
        modules.add(TestModule)
        commands.add(TestCommand)

        MessageSendHelper.sendChatMessage("Hi")
    }

    override fun onUnload() {
        MessageSendHelper.sendChatMessage("Bye")
    }
}