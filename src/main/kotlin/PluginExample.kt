import com.lambda.client.plugin.api.Plugin

internal object PluginExample: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(ModuleExample)
        commands.add(CommandExample)
        hudElements.add(LabelHudExample)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}