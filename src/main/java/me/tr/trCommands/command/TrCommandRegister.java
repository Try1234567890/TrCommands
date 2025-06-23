package me.tr.trCommands.command;

import me.tr.trCommands.TrCommands;
import me.tr.trCommands.file.FileLoader;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
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
                Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constructor.setAccessible(true);
                cmd = constructor.newInstance(command.getLabel(), main.getPlugin());
                main.getPlugin().getServer().getCommandMap().register(main.getPlugin().getName(), cmd);
                main.getLogger().info(main.getPlugin().getServer().getCommandMap().getKnownCommands().toString());
            } catch (NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                main.getLogger().warn("Error while registering command " + command.getLabel() + " of " + main.getPlugin().getName());
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
                    }
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
                    main.getLogger().warn(e, "Cannot auto-register command \"%s\" because an error occurs: ".formatted(className));
                }
            }
        } catch (IOException e) {
            main.getLogger().warn("Cannot auto-register commands because an error occurs while opening jar plugin of %s.".formatted(main.getPlugin().getName()));
        }
    }
}
