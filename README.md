# Lambda Plugin Example

This project in an example to show how a proper plugin for [Lambda Client](https://github.com/lambda-client/lambda) is set up.
The advantage of plugins for a utility mod is that they allow the user to decide what features their personalized client will have. Plugins work in plug and play manner, and can be downloaded and activated inside the ingame menu without reloading the client as long as no mixins are used for the plugin.
If you are scared about the Kotlin in this project be aware that Kotlin is a wrapper language for Java. That means that plugins can also be natively written in Java.

## Setup

To achieve coding building and publishing your own plugin most of the following steps are required.

### Fork
This is a template repository and can be used as a base to create a custom plugin. Press the `Use as template` button on GitHub to automatically create a linked fork to this repository.

### Clone Repository

Clone the repository to your local machine. Use the link of either your fork or the main repository.
```
git clone https://github.com/lambda-client/ExamplePlugin
```

### Setup IDE

In this guide we will use [IntelliJ IDEA](https://www.jetbrains.com/idea/) as IDE.
1. Open the project from `File > Open...`
2. Let the IDE collect dependencies and index the code.
3. Goto `File > Project Structure... > SDKs` and make sure an SDK for Java 8 is installed and selected, if not download
   it [here](https://adoptopenjdk.net/index.html?variant=openjdk8&jvmVariant=hotspot).
4. Run the `genIntellijRuns` Gradle task, or run `./gradlew genIntellijRuns`.
5. Open with `CTRL+left_click` on `PluginModule` in `ExampleModule` to open the decompiled lambda dependency. To see the original source code go in the top right corner you can add `Sources...` and select the `lambda-*-api-source.jar`.

### Configure Gradle

Test if the environment is set up correctly by building the plugin jar using the Gradle tab on the right side of the IDE.
1. Go to `ExamplePlugin > Tasks > build > build` in the Gradle tab and run the script
2. IntelliJ will create a new directory called `build`. The final built jar will be in `build/libs`

### Config

Configure the metadata of your plugin in `plugin_info.json`.
The flag `main_class` must contain the target main class `Plugin` in this case it is `PluginExample.kt`

### Plugin

The plugin main class will act as a register for the functions a plugin can provide.
For example when a new module class is created you have to add this to the `onLoad()` function of the plugin class.
```
modules.add(ExampleModule)
```
Every service is required to be added to the main class in order to index the contents.

### PluginModule

A module represents a utility module inside the game.
The `PluginModule` class acts as a wrapper for `Module` class. For many examples on how a module can work check out the [native modules](https://github.com/lambda-client/lambda/tree/master/src/main/kotlin/com/lambda/client/module/modules) of lambda, or the given example in this project.
The difference from the native `Module` class is that each component of a plugin requires a reference to the main `Plugin` class.
```
pluginMain = ExamplePlugin
```
Every PluginModule class will need to be registered to the main plugin class

### ClientCommand

Plugins use the same class as the native client for registering commands. Feel free to check out the [commands of Lambda Client](https://github.com/lambda-client/lambda/tree/master/src/main/kotlin/com/lambda/client/command/commands) as a reference. 

### PluginLabelHud

A LabelHud is used to display information in the player GUI.
The `PluginLabelHud` class acts as a wrapper for `LabelHud` class. For many examples on how a hud can work check out the [native hud elements](https://github.com/lambda-client/lambda/tree/master/src/main/kotlin/com/lambda/client/gui/hudgui/elements) of lambda, or the given example in this project.
The difference to the native `LabelHud` class is that a referral to the main plugin class is given in the object data.
```
pluginMain = ExamplePlugin
```
Every `PluginLabelHud` class will need to be registered to the main `Plugin` class.

### Background Jobs

If coroutines are needed background jobs can be registered using
```
bgJobs.add(BackgroundJob)
```

### Mixins

To use mixins, you have to:
1. Register the files in the `plugin_info.json` in the `mixins` list.
2. Add the `refmap` path from `mixins.ExamplePlugin.json` to the `build.gradle` source sets.

### Build

1. Go to `ExamplePlugin > Tasks > shadow > shadowJar` in the Gradle tab and run the script
2. IntelliJ will create a new directory called `build` the final built jar will be in `build/libs`
3. Put the `ExamplePlugin-1.0.jar` into your `./minecraft/lambda/plugins` folder and run the game.

### Publish

Publish your repository in Discord, and we may fork you, or transfer your repository to official
plugin [organization of Lambda](https://github.com/lambda-plugins/). After review, your plugin may get added to the native
marketplace.
