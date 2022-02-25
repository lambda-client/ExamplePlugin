import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import com.google.gson.JsonParser
import com.lambda.client.commons.utils.ClassUtils.instance
import com.lambda.client.plugin.api.Plugin

class ClassTest {

    @Test
    fun testPluginClass() {
        val gson = JsonParser()

        val pluginInfo = this.javaClass.classLoader.getResourceAsStream("plugin_info.json")
            ?: fail("plugin_info.json not found.")

        val pluginInfoJson = gson.parse(pluginInfo.reader()).asJsonObject.get("main_class")?.asString
            ?: fail("main_class key not found in plugin_info.json.")

        try {
            Class.forName(pluginInfoJson)
        } catch (e: ClassNotFoundException) {
            fail("Class $pluginInfoJson not found in the plugin classpath.")
        }

        if (Class.forName(pluginInfoJson).instance !is Plugin) {
            fail("Class $pluginInfoJson is not a subclass of Plugin.")
        }
    }
}