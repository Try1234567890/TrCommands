package me.tr.trCommands;


import me.tr.trCommands.command.TrCommandRegister;
import me.tr.trCommands.file.Config;
import me.tr.trFiles.TrFiles;
import me.tr.trLogger.logger.BukkitLogger;
import org.bukkit.plugin.Plugin;

public final class TrCommands {
    private static TrCommands instance;
    private Plugin plugin;
    private BukkitLogger logger;
    private TrFiles trFiles;
    private boolean autoRegisterCommands = true;
    private String help = "?";
    private String desc = "!";

    private TrCommands() {
    }

    public void init(Plugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.logger = new BukkitLogger();
        if (plugin == null) {
            throw new NullPointerException("Plugin cannot be null");
        }
        this.trFiles = new TrFiles();
        this.autoRegisterCommands = Config.AUTO_REGISTER.getBoolean();
        this.help = Config.CHAR_HELP.getString();
        this.desc = Config.CHAR_DESC.getString();
        if (isAutoRegisterCommands()) {
            TrCommandRegister.autoRegister();
        }
    }

    public static TrCommands getInstance() {
        if (instance == null) {
            instance = new TrCommands();
        }
        return instance;
    }

    public boolean isAutoRegisterCommands() {
        return autoRegisterCommands;
    }

    public void setAutoRegisterCommands(boolean autoRegisterCommands) {
        this.autoRegisterCommands = autoRegisterCommands;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public BukkitLogger getLogger() {
        return logger;
    }

    public TrFiles getTrFiles() {
        return trFiles;
    }
}
