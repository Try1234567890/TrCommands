package me.tr.trCommands.command;

import me.tr.trCommands.TrCommands;
import me.tr.trCommands.command.permission.TrPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the Command with additional properties
 */
public abstract class TrCommand {
    private TrCommands main = TrCommands.getInstance();
    private String uuid;
    private String label;
    private String[] name;
    private Map<Integer, Set<String>> aliases;
    private boolean onlyConsole;
    private boolean onlyPlayer;
    private TrPermission permission;
    private int targetIndex;
    private int minArgs;
    private String[] desc;
    private String[] help;

    /**
     * Create new {@link TrCommand} instance with all properties.
     *
     * @param uuid        ID used to identify command uniquely.
     * @param label       Represents command label, used to register command in Bukkit.
     * @param name        Represent command args name.
     * @param aliases     Represent command args aliases.
     * @param onlyConsole If true, command can be executed only by console.
     * @param onlyPlayer  If true, command can be executed only by players.
     * @param permission  The permissions needed for this command.
     * @param targetIndex The index inside args that contains target name.
     *                    (If this is -1 and target name is present inside args, it will be recognized automatically).
     * @param minArgs     The minimum args length needed to work properly.
     * @param desc        Command description, sent if one args is '!' (Configurable).
     * @param help        Command description, sent if one args is '?' (Configurable).
     */
    public TrCommand(String uuid,
                     String label,
                     String[] name,
                     Map<Integer, Set<String>> aliases,
                     boolean onlyConsole,
                     boolean onlyPlayer,
                     TrPermission permission,
                     int targetIndex,
                     int minArgs,
                     String[] desc,
                     String[] help) {
        this.uuid = uuid;
        this.label = label;
        this.name = name;
        this.aliases = aliases;
        this.onlyConsole = onlyConsole;
        this.onlyPlayer = onlyPlayer;
        this.permission = permission;
        this.targetIndex = targetIndex;
        this.minArgs = minArgs;
        this.desc = desc;
        this.help = help;
    }

    /**
     * Create new {@link TrCommand} instance with minimum properties and id.
     *
     * @param uuid    ID used to identify command uniquely.
     * @param label   Represents command label, used to register command in Bukkit.
     * @param name    Represent command args name.
     * @param minArgs The minimum args length needed to work properly.
     */
    public TrCommand(String uuid, String label, String[] name, int minArgs) {
        this.uuid = uuid;
        this.label = label;
        this.name = name;
        this.aliases = new HashMap<>();
        this.onlyConsole = false;
        this.onlyPlayer = false;
        this.permission = null;
        this.targetIndex = -1;
        this.minArgs = minArgs;
        this.desc = new String[0];
        this.help = new String[0];
    }

    /**
     * Create new {@link TrCommand} instance with minimum properties and id.
     *
     * @param label   Represents command label, used to register command in Bukkit.
     * @param name    Represent command args name.
     * @param minArgs The minimum args length needed to work properly
     */
    public TrCommand(String label, String[] name, int minArgs) {
        this.uuid = UUID.randomUUID().toString();
        this.label = label;
        this.name = name;
        this.aliases = new HashMap<>();
        this.onlyConsole = false;
        this.onlyPlayer = false;
        this.permission = null;
        this.targetIndex = -1;
        this.minArgs = minArgs;
        this.desc = new String[0];
        this.help = new String[0];
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public Map<Integer, Set<String>> getAliases() {
        return aliases;
    }

    public void setAliases(Map<Integer, Set<String>> aliases) {
        this.aliases = aliases;
    }

    public boolean isOnlyConsole() {
        return onlyConsole;
    }

    public void setOnlyConsole(boolean onlyConsole) {
        this.onlyConsole = onlyConsole;
    }

    public boolean isOnlyPlayer() {
        return onlyPlayer;
    }

    public void setOnlyPlayer(boolean onlyPlayer) {
        this.onlyPlayer = onlyPlayer;
    }

    public TrPermission getPermission() {
        return permission;
    }

    public void setPermission(TrPermission permission) {
        this.permission = permission;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public String[] getDesc() {
        return desc;
    }

    public void setDesc(String[] desc) {
        this.desc = desc;
    }

    public String[] getHelp() {
        return help;
    }

    public void setHelp(String[] help) {
        this.help = help;
    }

    public boolean matches(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String input = args[i].toLowerCase();
            main.getLogger().debug("Command Input (or Args[%d]): %s".formatted(i, input));
            String namePart = null;
            Set<String> argAliases = null;
            if (i < this.name.length) {
                namePart = (name[i] != null ? name[i] : "").toLowerCase();
                main.getLogger().debug("Command Name[%d]: %s".formatted(i, namePart));
            }
            if (i < this.aliases.size()) {
                argAliases = aliases.get(i);
                argAliases = argAliases == null ? null : argAliases.stream().map(String::toLowerCase).collect(Collectors.toSet());
                main.getLogger().debug("Command Alias[%d]: %s".formatted(i, argAliases != null ? argAliases : "Nothing"));
            }

            if (input.equals(namePart) || (argAliases != null && argAliases.contains(input))) {
                main.getLogger().debug("Input (\"%s\") is not equals NamePart (\"%s\") and ArgAlias (\"%s\") is null or not contains it, continuing..."
                        .formatted(!input.isEmpty() ? input : "Nothing",
                                namePart != null ? namePart : "Nothing",
                                argAliases != null ? argAliases : "Nothing")
                );
                return true;
            }
        }
        return false;
    }

    public Player getTarget(String[] args, Player def) {
        if (targetIndex == -1) {
            main.getLogger().debug("Target index is not set, trying to got automatically...");
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                Player target = Bukkit.getPlayer(arg);
                if (target != null) {
                    main.getLogger().debug("Target found at %d [%s]: %s".formatted(i, arg, target.getName()));
                    return target;
                }
            }
            return def;
        }
        if ((args.length + 1) < targetIndex) {
            main.getLogger().debug("Args length %d is minus of %d, returning def...".formatted(args.length + 1, targetIndex));
            return def;
        }
        Player target = Bukkit.getPlayer(args[targetIndex]);
        if (target == null) {
            main.getLogger().debug("Target is not null at index %d".formatted(targetIndex));
            return def;
        }
        return target;
    }

    public boolean isDesc(String[] args) {
        for (String arg : args) {
            if (arg.equals(TrCommands.getInstance().getDesc())) {
                return true;
            }
        }
        return false;
    }

    public boolean isHelp(String[] args) {
        for (String arg : args) {
            if (arg.equals(TrCommands.getInstance().getHelp())) {
                return true;
            }
        }
        return false;
    }


    public abstract boolean execute(CommandSender sender, @Nullable Player player, @Nullable Player target, String[] args);
}
