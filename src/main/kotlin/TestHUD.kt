import com.lambda.client.plugin.api.PluginHudElement

internal object TestHUD: PluginHudElement(
    name = "TestHUD",
    category = Category.CLIENT,
    description = "Shows Player username",
    pluginMain = TestPlugin
) {
    private val prefix = setting("Prefix", "Welcome")
    private val suffix = setting("Suffix", "")

//    override fun SafeClientEvent.updateText() {
//        displayText.add(prefix.value, primaryColor)
//        displayText.add(mc.session.username, secondaryColor)
//        displayText.add(suffix.value, primaryColor)
//    }
}