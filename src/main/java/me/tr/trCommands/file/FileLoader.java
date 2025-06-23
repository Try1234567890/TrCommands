package me.tr.trCommands.file;

import me.tr.trCommands.TrCommands;
import me.tr.trFiles.configuration.file.FileConfiguration;
import me.tr.trFiles.general.managers.FileManager;
import me.tr.trFiles.general.utility.FileUtility;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileLoader {
    private static FileLoader instance;

    public static FileLoader getInstance() {
        if (instance == null) {
            instance = new FileLoader();
            instance.init();
        }
        return instance;
    }


    private FileLoader() {
    }


    private final TrCommands main = TrCommands.getInstance();
    public final String CONFIG_PATH_STR = "TrCommands/";
    public final File CONFIG_PATH = new File(main.getPlugin().getDataFolder(), CONFIG_PATH_STR);
    private final FileManager fm = main.getTrFiles().getFileManager();
    private final Map<String, List<String>> files = new LinkedHashMap<>();

    {
        files.put("", List.of("config.yml", "messages.yml"));
    }

    private final File jar = searchJar();
    private FileConfiguration config;
    private FileConfiguration messages;

    public void init() {
        if (jar == null) {
            main.getLogger().error(main.getPlugin().getName() + " jar file not found or it is not readable (check for warn logs in the previous line).");
            return;
        }
        for (Map.Entry<String, List<String>> entry : files.entrySet()) {
            File path = entry.getValue().isEmpty() ? CONFIG_PATH : new File(CONFIG_PATH, entry.getKey());
            for (String file : entry.getValue()) {
                FileConfiguration configuration;
                File to = new File(path, file);
                File insideJar = new File(CONFIG_PATH_STR, "cmd-" + file);
                main.getLogger().debug("Processing file %s saving to %s from jar %s".formatted(file, to.getPath(), insideJar.getPath()));
                if (!to.exists() || to.length() == 0) {
                    configuration = FileConfiguration.loadFromJar(jar, insideJar, to);
                } else {
                    configuration = FileConfiguration.loadConfiguration(to);
                }
                try {
                    setField(file, configuration);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    main.getLogger().warn("Error while assigning configuration file " + file + " to " + getFieldName(file));
                }
            }
        }
    }

    private void setField(String fileName, FileConfiguration configuration) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(getFieldName(fileName));
        field.setAccessible(true);
        field.set(this, configuration);
    }

    private String getFieldName(String fileName) {
        return FileUtility.getFileNameWithoutExtension(fileName);
    }

    private @Nullable File searchJar() {
        File[] jars = new File("plugins/").listFiles();
        if (jars == null) return null;
        for (File file : jars) {
            if (!FileUtility.isJar(file)) continue;
            try (JarFile jar = new JarFile(file)) {
                String entryName = main.getPlugin().getPluginMeta().getMainClass().replace('.', '/') + ".class";
                JarEntry entry = jar.getJarEntry(entryName);
                if (entry != null)
                    return file;
            } catch (IOException e) {
                String[] msg = {
                        "Error while reading jar " + file.getPath() + ".",
                        "If it is " + main.getPlugin().getName() + " jar",
                        "some features of it may not work correctly.",
                        "",
                        "Check for error logs in the next few lines."
                };
                for (String line : msg) {
                    main.getLogger().warn(line);
                }
            }
        }
        return null;
    }

    public File getJar() {
        return jar;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }
}
