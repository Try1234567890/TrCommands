package me.tr.trCommands.command.permission;

import me.tr.trCommands.utility.Utility;
import org.bukkit.entity.Player;

public class TrPermission {
    private final String[] permissions;
    private final int atLast;
    private final String others;

    public TrPermission(int atLast, String others, String... permissions) {
        this.permissions = permissions;
        this.atLast = atLast;
        this.others = others;
    }

    public TrPermission(int atLast, String... permissions) {
        this.permissions = permissions;
        this.atLast = atLast;
        this.others = permissions.length > 0 ? permissions[0] : "";
    }

    public TrPermission(String other, String... permissions) {
        this.permissions = permissions;
        this.atLast = 1;
        this.others = other;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public String getOthers() {
        return others;
    }

    public int getAtLast() {
        return atLast;
    }

    public boolean has(Player player) {
        if (player == null) return false;
        int matches = 0;
        for (String permission : this.permissions) {
            if (matches == this.atLast) {
                return true;
            }
            if (player.hasPermission(permission)) {
                matches++;
            }
        }
        return false;
    }

    public boolean hasOthers(Player player) {
        if (player == null) return false;
        if (Utility.isNull(this.others)) return true;
        return player.hasPermission(this.others);
    }

}
