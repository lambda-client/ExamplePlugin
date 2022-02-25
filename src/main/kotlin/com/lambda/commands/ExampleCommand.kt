package com.lambda.commands

import com.lambda.client.command.ClientCommand
import com.lambda.client.manager.managers.FriendManager
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.managers.ExampleManager

object ExampleCommand : ClientCommand(
    name = "example",
    description = "Example command"
) {
    init {
        literal("add", "new", "+") {
            player("player") { playerArg ->
                execute("Add a friend") {
                    val name = playerArg.value.name
                    if (FriendManager.isFriend(name)) {
                        MessageSendHelper.sendChatMessage("That player is already your friend.")
                    } else {
                        if (FriendManager.addFriend(name)) {
                            MessageSendHelper.sendChatMessage("&7${name}&r has been friended.")
                        } else {
                            MessageSendHelper.sendChatMessage("Failed to find UUID of $name")
                        }
                    }
                }
            }
        }

        execute("Shows game loop start time") {
            MessageSendHelper.sendChatMessage("The game loop started exactly at ${ExampleManager.gameLoopStartTime}.")
        }
    }
}