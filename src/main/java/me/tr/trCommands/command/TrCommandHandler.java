package me.tr.trCommands.command;

import me.tr.trCommands.TrCommands;
import me.tr.trCommands.file.Messages;
import me.tr.trCommands.utility.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * This class handle all registered commands
 */
public class TrCommandHandler implements CommandExecutor, TabCompleter {
    private static TrCommandHandler instance;

    public static TrCommandHandler getInstance() {
        if (instance == null) {
            instance = new TrCommandHandler();
        }
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        for (TrCommand cmd : TrCommandRegistry.getCommands().values()) {
            TrCommands.getInstance().getLogger().debug("Checking if \"%s\" equals \"%s\"".formatted(cmd, Arrays.toString(args)));
            if (cmd.equals(args)) {
                @Nullable Player player = commandSender instanceof Player ? (Player) commandSender : null;
                @Nullable Player target = cmd.getTarget(args, player);
                if (cmd.isDesc(args)) {
                    Arrays.stream(cmd.getDesc()).forEach(line -> MessageFormatter.getInstance().format(line, player, target, cmd));
                    return true;
                }

                if (cmd.isHelp(args)) {
                    Arrays.stream(cmd.getHelp()).forEach(line -> MessageFormatter.getInstance().format(line, player, target, cmd));
                    return true;
                }

                if (args.length < cmd.getMinArgs()) {
                    commandSender.sendMessage(MessageFormatter.getInstance().format(Messages.COMMAND_NOT_FOUND.getString(), player, target, cmd));
                    return true;
                }

                if (player != null && cmd.isOnlyConsole()) {
                    commandSender.sendMessage(MessageFormatter.getInstance().format(Messages.CONSOLE_ONLY.getString(), player, target, cmd));
                    return true;
                }

                if (player == null && cmd.isOnlyPlayer()) {
                    commandSender.sendMessage(MessageFormatter.getInstance().format(Messages.PLAYER_ONLY.getString(), null, target, cmd));
                    return true;
                }

                if (!cmd.getPermission().has(player)
                        && ((target != null && target != player) && !cmd.getPermission().hasOthers(player))) {
                    commandSender.sendMessage(MessageFormatter.getInstance().format(Messages.NO_PERMISSION.getString(), player, target, cmd));
                    return true;
                }

                return cmd.execute(commandSender, player, target, args);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        for (TrCommand cmd : TrCommandRegistry.getCommands().values()) {
            if (args.length == 1) {
                String name = cmd.getName()[0];
                Set<String> alias = cmd.getAliases().get(0);
                completions.add(name);
                completions.addAll(alias);
            } else {
                if (cmd.equals(args)) {
                    String name = cmd.getName()[args.length - 1];
                    Set<String> alias = cmd.getAliases().get(args.length - 1);
                    completions.add(name);
                    completions.addAll(alias);
                }
            }
        }

        return completions;
    }
}
