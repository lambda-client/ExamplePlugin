import com.lambda.client.event.SafeClientEvent
import com.lambda.client.plugin.api.PluginLabelHud

internal object TestHUD: PluginLabelHud(
    name = "TestHUD",
    category = Category.MISC,
    description = "Shows Player username",
    pluginMain = TestPlugin
) {
    private val prefix = setting("Prefix", "Welcome")
    private val suffix = setting("Suffix", "")

    override fun SafeClientEvent.updateText() {
        displayText.add(prefix.value, primaryColor)
        displayText.add(suffix.value, primaryColor)
    }
}