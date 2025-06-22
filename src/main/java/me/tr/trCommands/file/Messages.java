package me.tr.trCommands.file;

import me.tr.trFiles.configuration.file.FileConfiguration;
import me.tr.trCommands.TrCommands;

public enum Messages {
    CONSOLE_ONLY("ConsoleOnly", "<red>This command is console only! Type [command] in console."),
    PLAYER_ONLY("PlayerOnly", "<red>This command is player only! Type /[command] in game."),
    NO_PERMISSION("NoPermission", "<red>You do not have permission to use this command!"),
    COMMAND_NOT_FOUND("CommandNotFound", "<red>Command [command] not found!"),
    TARGET_NOT_FOUND("TargetNotFound", "<red>[targetName] not found! Make sure [targetName] is online."),
    CONSOLE_NO_TARGET("ConsoleNoTarget", "<red>Console cannot run commands without a target. Specify a target name at [commandTargetIndex].");

    private final FileConfiguration messages = FileLoader.getInstance().getMessages();
    private final String path;
    private final Object def;

    Messages(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public String getPath() {
        return path;
    }

    public Object getDef() {
        return def;
    }

    public String getString() {
        if (messages == null) {
            return (String) def;
        }
        return messages.getString(getPath(), (String) getDef());
    }
}
