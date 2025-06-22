package me.tr.trCommands.file;

import me.tr.trFiles.configuration.file.FileConfiguration;

public enum Config {

    AUTO_REGISTER("AutoRegister", true),
    CHAR_HELP("Chars.Help", "?"),
    CHAR_DESC("Chars.Desc", "!")
    ;

    private final FileConfiguration config = FileLoader.getInstance().getMessages();
    private final String path;
    private final Object def;

    Config(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    public String getPath() {
        return path;
    }

    public Object getDef() {
        return def;
    }

    public boolean getBoolean() {
        if (config == null) {
            return (boolean) getDef();
        }
        return config.getBoolean(getPath(), (boolean) getDef());
    }

    public String getString() {
        if (config == null) {
            return (String) getDef();
        }
        return config.getString(getPath(), (String) getDef());
    }
}
