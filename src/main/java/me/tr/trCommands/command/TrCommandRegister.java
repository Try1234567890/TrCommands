package me.tr.trCommands.command;

import me.tr.trCommands.TrCommands;
import me.tr.trCommands.file.FileLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TrCommandRegister {
    private static final TrCommands main = TrCommands.getInstance();
    private static final FileLoader loader = FileLoader.getInstance();

    /**
     * Register a new command in bukkit too.
     *
     * @param command The {@link TrCommand} to register
     */
    public static void register(TrCommand command) {
        PluginCommand cmd = main.getPlugin().getServer().getPluginCommand(command.getLabel());
        if (cmd == null) {
            try {
                Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
                commandMap.register(command.getLabel(), new Command(String.join(" ", command.getName())) {
                    @Override
                    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
                        return TrCommandHandler.getInstance().onCommand(commandSender, this, s, strings);
                    }
                });
                TrCommandRegistry.add(command);
                return;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        cmd.setExecutor(new TrCommandHandler());
        TrCommandRegistry.add(command);
    }

    /**
     * Try to auto-register all commands of the current plugin.
     */
    public static void autoRegister() {
        if (loader.getJar() == null) {
            main.getLogger().warn("Cannot auto-register commands because jar plugin that use TrCommand is not found.");
            return;
        }
        String mainClass = main.getPlugin().getPluginMeta().getMainClass();
        mainClass = mainClass.substring(0, mainClass.lastIndexOf('.') + 1);
        try (JarFile jar = new JarFile(loader.getJar());
             URLClassLoader classLoader = new URLClassLoader(new URL[]{loader.getJar().toURI().toURL()}, main.getClass().getClassLoader())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".class") || !name.startsWith(mainClass.replace('.', '/'))) continue;
                String className = name.replace('/', '.').substring(0, name.length() - 6);
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    if (TrCommand.class.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                        TrCommand command = (TrCommand) clazz.getConstructor().newInstance();
                        register(command);
                        main.getLogger().info("Registered new command: " + command);
                    }
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
                    main.getLogger().warn(e, "Cannot auto-register command \"%s\" because an error occurs: ".formatted(className));
                }
            }
        } catch (IOException e) {
            main.getLogger().warn("Cannot auto-register commands because an error occurs while opening jar plugin that use TrCommand.");
        }
    }
}
