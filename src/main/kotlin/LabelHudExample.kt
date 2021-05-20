import com.lambda.client.event.SafeClientEvent
import com.lambda.client.plugin.api.PluginLabelHud

internal object LabelHudExample: PluginLabelHud(
    name = "LabelHudExample",
    category = Category.MISC,
    description = "Simple hud example",
    pluginMain = PluginExample
) {
    private val prefix by setting("Prefix", "Hello")
    private val suffix by setting("Suffix", "World")

    override fun SafeClientEvent.updateText() {
        displayText.add(prefix, primaryColor)
        displayText.add(suffix, secondaryColor)
    }
}