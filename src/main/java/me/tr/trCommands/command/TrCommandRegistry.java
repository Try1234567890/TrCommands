package me.tr.trCommands.command;

import me.tr.trCommands.TrCommands;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a registry that contains all command registered to this TrCommands instance.
 */
public class TrCommandRegistry {
    private static final Map<String, TrCommand> commands = new HashMap<>();

    public static Map<String, TrCommand> getCommands() {
        return commands;
    }

    public static void add(TrCommand command) {
        if (commands.containsKey(command.getUuid())) {
            TrCommands.getInstance().getLogger().warn("Command with identifier \"%s\" already exists. Make sure the commands identifies are unique.".formatted(command.getUuid()));
            return;
        }
        commands.put(command.getUuid(), command);
    }

    public static @Nullable TrCommand get(String uuid) {
        return commands.get(uuid);
    }

    public static void remove(String uuid) {
        commands.remove(uuid);
    }

    public static void modify(String uuid, TrCommand command) {
        remove(uuid);
        add(command);
    }
}
