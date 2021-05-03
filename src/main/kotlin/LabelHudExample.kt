import com.lambda.client.event.SafeClientEvent
import com.lambda.client.plugin.api.PluginLabelHud

internal object LabelHudExample: PluginLabelHud(
    name = "LabelHudExample",
    category = Category.MISC,
    description = "Shows Player username",
    pluginMain = PluginExample
) {
    private val prefix = setting("Prefix", "Welcome")
    private val suffix = setting("Suffix", "")

    override fun SafeClientEvent.updateText() {
        displayText.add(prefix.value, primaryColor)
        displayText.add(suffix.value, primaryColor)
    }
}