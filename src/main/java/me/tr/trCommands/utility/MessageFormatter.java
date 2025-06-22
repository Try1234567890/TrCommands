package me.tr.trCommands.utility;

import com.google.common.collect.Multimap;
import me.tr.trCommands.command.TrCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageFormatter {
    private static MessageFormatter instance;

    public static MessageFormatter getInstance() {
        if (instance == null) {
            instance = new MessageFormatter();
        }
        return instance;
    }

    private MessageFormatter() {
    }

    public Component format(@NotNull String msg, Player player, Player target, @NotNull TrCommand command) {
        msg = msg
                .replace("[CommandID]", command.getUuid())
                .replace("[CommandPermissions]", String.join(", ", Arrays.stream(command.getPermission().getPermissions()).toList()))
                .replace("[CommandPermissionOthers]", command.getPermission().getOthers())
                .replace("[CommandPermissionAtLast]", String.valueOf(command.getPermission().getAtLast()))
                .replace("[CommandName]", String.join(" ", command.getName()))
                .replace("[CommandDesc]", String.join("\n", command.getDesc()))
                .replace("[CommandHelp]", String.join("\n", command.getHelp()))
                .replace("[CommandIsOnlyConsole]", String.valueOf(command.isOnlyConsole()))
                .replace("[CommandIsOnlyPlayer]", String.valueOf(command.isOnlyPlayer()))
                .replace("[CommandTargetIndex]", String.valueOf(command.getTargetIndex()))
                .replace("[CommandMinArgs]", String.valueOf(command.getMinArgs()));
        return format(msg, player, target);
    }

    public Component format(@NotNull String msg, Player player, Player target) {
        if (player != null) msg = replace(msg, player, "player");
        if (target != null) msg = replace(msg, target, "target");
        return Component.text(msg);
    }

    private String replace(String msg, Player player, String prefix) {
        Map<String, Object> placeholders = placeholders(player, prefix);
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "UNKNOWN");
        }
        return msg;
    }

    private Map<String, Object> placeholders(Player player, String prefix) {
        Map<String, Object> map = new HashMap<>();
        InetSocketAddress address = player.getAddress();
        WeatherType weather = player.getPlayerWeather();
        Player killer = player.getKiller();
        Entity vehicle = player.getVehicle();
        map.put("[Prefix]", prefix);
        map.put("[" + prefix + "UUID]", player.getUniqueId().toString());
        map.put("[" + prefix + "]", player);
        map.put("[" + prefix + "Name]", player.getName());
        map.put("[" + prefix + "Address]", address != null ? address.toString() : "UNKNOWN");
        map.put("[" + prefix + "AllowFlight]", player.getAllowFlight());
        location(map, player.isSleeping() ? player.getBedLocation() : player.getLocation(), prefix + "BedLocation");
        location(map, player.getCompassTarget(), prefix + "CompassTarget");
        scoreboard(map, player.getScoreboard(), prefix);
        map.put("[" + prefix + "Exhaustion]", player.getExhaustion());
        map.put("[" + prefix + "Exp]", player.getExp());
        map.put("[" + prefix + "FlySpeed]", player.getFlySpeed());
        map.put("[" + prefix + "FoodLevel]", player.getFoodLevel());
        map.put("[" + prefix + "HealthScale]", player.getHealthScale());
        map.put("[" + prefix + "Level]", player.getLevel());
        map.put("[" + prefix + "Time]", player.getPlayerTime());
        map.put("[" + prefix + "TimeOffset]", player.getPlayerTimeOffset());
        map.put("[" + prefix + "Weather]", weather != null ? weather.name() : "UNKNOWN");
        map.put("[" + prefix + "Saturation]", player.getSaturation());
        map.put("[" + prefix + "TotalExp]", player.getTotalExperience());
        map.put("[" + prefix + "WalkSpeed]", player.getWalkSpeed());
        map.put("[" + prefix + "IsFlying]", player.isFlying());
        map.put("[" + prefix + "IsHealthScale]", player.getHealthScale());
        map.put("[" + prefix + "IsTimeRelative]", player.isPlayerTimeRelative());
        map.put("[" + prefix + "IsSleepingIgnored]", player.isSleepingIgnored());
        map.put("[" + prefix + "IsSneaking]", player.isSneaking());
        map.put("[" + prefix + "IsSprinting]", player.isSprinting());
        map.put("[" + prefix + "EnderChest]", player.getEnderChest().toString());
        map.put("[" + prefix + "Inventory]", player.getInventory().toString());
        map.put("[" + prefix + "GameMode]", player.getGameMode());
        itemstack(map, player.getInventory().getItemInMainHand(), prefix + "ItemInHand");
        itemstack(map, player.getInventory().getItemInOffHand(), prefix + "ItemInHand");
        itemstack(map, player.getInventory().getBoots(), prefix + "Boots");
        itemstack(map, player.getInventory().getLeggings(), prefix + "Leggings");
        itemstack(map, player.getInventory().getChestplate(), prefix + "Chestplate");
        itemstack(map, player.getInventory().getHelmet(), prefix + "Helmet");
        map.put("[" + prefix + "SleepTicks]", player.getSleepTicks());
        map.put("[" + prefix + "Blocking]", player.isBlocking());
        map.put("[" + prefix + "CanPickupItems]", player.getCanPickupItems());
        map.put("[" + prefix + "EyeHeight]", player.getEyeHeight());
        location(map, player.getEyeLocation(), prefix + "EyeLocation");
        map.put("[" + prefix + "Killer]", killer != null ? killer.getName() : "UNKNOWN");
        map.put("[" + prefix + "LastDamage]", player.getLastDamage());
        map.put("[" + prefix + "LeashHolder]", player.isLeashed() ? player.getLeashHolder().getName() : "UNKNOWN");
        map.put("[" + prefix + "MaximumAir]", player.getMaximumAir());
        map.put("[" + prefix + "MaximumNoDamageTicks]", player.getMaximumNoDamageTicks());
        map.put("[" + prefix + "RemainingAir]", player.getRemainingAir());
        map.put("[" + prefix + "RemoveWhenFarAway]", player.getRemoveWhenFarAway());
        map.put("[" + prefix + "IsCustomNameVisible]", player.isCustomNameVisible());
        map.put("[" + prefix + "IsLeashed]", player.isLeashed());
        map.put("[" + prefix + "EntityID]", player.getEntityId());
        map.put("[" + prefix + "FallDistance]", player.getFallDistance());
        map.put("[" + prefix + "FireTicks]", player.getFireTicks());
        map.put("[" + prefix + "LastDamageCause]", player.getLastDamageCause());
        location(map, player.getLocation(), prefix);
        map.put("[" + prefix + "MaxFireTicks]", player.getMaxFireTicks());
        map.put("[" + prefix + "Server]", player.getServer().getName());
        map.put("[" + prefix + "TicksLived]", player.getTicksLived());
        map.put("[" + prefix + "Type]", player.getType().name());
        map.put("[" + prefix + "Vehicle]", vehicle != null ? vehicle.getName() : "UNKNOWN");
        map.put("[" + prefix + "Velocity]", player.getVelocity());
        map.put("[" + prefix + "World]", player.getWorld().getName());
        map.put("[" + prefix + "isDead]", player.isDead());
        map.put("[" + prefix + "isEmpty]", player.isEmpty());
        map.put("[" + prefix + "isInsideVehicle]", player.isInsideVehicle());
        map.put("[" + prefix + "isValid]", player.isValid());
        map.put("[" + prefix + "EffectivePermissions]", player.getEffectivePermissions());
        map.put("[" + prefix + "FirstPlayed]", player.getFirstPlayed());
        map.put("[" + prefix + "LastPlayed]", player.getLastLogin());
        map.put("[" + prefix + "HasPlayedBefore]", player.hasPlayedBefore());
        map.put("[" + prefix + "IsBanned]", player.isBanned());
        map.put("[" + prefix + "IsOnline]", player.isOnline());
        map.put("[" + prefix + "IsWhitelisted]", player.isWhitelisted());
        return map;
    }

    private void itemstack(Map<String, Object> map, @Nullable ItemStack itemstack, String prefix) {
        if (itemstack == null || !itemstack.hasItemMeta())
            return;
        map.put("[" + prefix + "]", itemstack.getType().name());
        map.put("[" + prefix + "]", itemstack.getAmount());
        itemstack.getEnchantments().forEach((ench, lvl) -> map.put("[" + prefix + "Enchantment" + ench.key().value() + "]", ench.key() + " - " + lvl));
        attributes(map, itemstack.getItemMeta().getAttributeModifiers(), prefix + "Attributes");
        map.put("[" + prefix + "]", String.join(", ", itemstack.getItemFlags().stream().map(ItemFlag::name).toList()));
        map.put("[" + prefix + "]", itemstack.getMaxStackSize());
        map.put("[" + prefix + "]", itemstack.getItemMeta().getCustomModelData());
        map.put("[" + prefix + "]", itemstack.getItemMeta().getEnchantmentGlintOverride());
        map.put("[" + prefix + "]", itemstack.getItemMeta().getItemModel());
        map.put("[" + prefix + "]", itemstack.getItemMeta().getRarity().name());
    }

    private void attributes(Map<String, Object> map, @Nullable Multimap<Attribute, AttributeModifier> attributes, String prefix) {
        if (attributes != null)
            attributes.forEach((attribute, attributeModifier) -> map.put("[" + prefix + "Enchantment" + attribute.key().value() + "]", attribute.getKey() + ": " + attributeModifier.getKey() + ": " + attributeModifier.getAmount() + '(' + attributeModifier.getOperation() + ')'));
    }

    private void scoreboard(Map<String, Object> map, Scoreboard scoreboard, String prefix) {
        map.put("[" + prefix + "ScoreboardEntries]", String.join(", ", scoreboard.getEntries()));
        map.put("[" + prefix + "ScoreboardObjectivesName]", String.join(", ", scoreboard.getObjectives().stream().map(Objective::getName).toList()));
        map.put("[" + prefix + "ScoreboardObjectivesDisplaySlot]", String.join(", ", scoreboard.getObjectives().stream().map(obj -> {
            DisplaySlot displaySlot = obj.getDisplaySlot();
            if (displaySlot != null) return displaySlot.name();
            return "UNKNOWN";
        }).toList()));
        map.put("[" + prefix + "ScoreboardObjectivesRenderType]", String.join(", ", scoreboard.getObjectives().stream().map(obj -> obj.getRenderType().name()).toList()));
        map.put("[" + prefix + "ScoreboardObjectivesTrackedCriteria]", String.join(", ", scoreboard.getObjectives().stream().map(obj -> obj.getTrackedCriteria().getName()).toList()));
        map.put("[" + prefix + "ScoreboardObjectivesTrackedCriteriaIsReadOnly]", String.join(", ", scoreboard.getObjectives().stream().map(obj -> String.valueOf(obj.getTrackedCriteria().isReadOnly())).toList()));
        map.put("[" + prefix + "ScoreboardTeamsName]", String.join(", ", scoreboard.getTeams().stream().map(team -> {
            if (team != null) return team.getName();
            return "";
        }).toList()));
        map.put("[" + prefix + "ScoreboardTeamsSize]", String.join(", ", scoreboard.getTeams().stream().map(team -> String.valueOf(team.getSize())).toList()));
    }

    private void location(Map<String, Object> map, Location location, String prefix) {
        map.put("[" + prefix + "X]", location.getX());
        map.put("[" + prefix + "Y]", location.getY());
        map.put("[" + prefix + "Z]", location.getZ());
        map.put("[" + prefix + "World]", location.getWorld().getName());
        map.put("[" + prefix + "Yaw]", location.getYaw());
        map.put("[" + prefix + "Pitch]", location.getPitch());
        map.put("[" + prefix + "Block]", location.getBlock().getBlockData().getMaterial().name());
        map.put("[" + prefix + "BlockX]", location.getBlockX());
        map.put("[" + prefix + "BlockY]", location.getBlockY());
        map.put("[" + prefix + "BlockZ]", location.getBlockZ());
        map.put("[" + prefix + "Entities]", String.join(", ", Arrays.stream(location.getChunk().getEntities()).map(Entity::getName).toList()));
        map.put("[" + prefix + "TileEntities]", String.join(", ", Arrays.stream(location.getChunk().getTileEntities()).map(bs -> bs.getType().name()).toList()));
        map.put("[" + prefix + "PlayerNamesSeeingChunk]", String.join(", ", location.getChunk().getPlayersSeeingChunk().stream().map(Player::getName).toList()));
        map.put("[" + prefix + "ChunkKey]", location.getChunk().getChunkKey());
        map.put("[" + prefix + "ChunkInhabitedTime]", String.valueOf(location.getChunk().getInhabitedTime()));
        map.put("[" + prefix + "ChunkLoadLevel]", location.getChunk().getLoadLevel().name());
        map.put("[" + prefix + "ChunkX]", location.getChunk().getX());
        map.put("[" + prefix + "ChunkZ]", location.getChunk().getZ());
        map.put("[" + prefix + "StructuresBoundingBoxCenterX]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getCenter().getX())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxCenterY]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getCenter().getY())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxCenterZ]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getCenter().getZ())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxCenterBlockX]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getCenter().getX())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxCenterBlockY]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getCenter().getY())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxCenterBlockZ]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getCenter().getZ())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMaxX]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMax().getX())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMaxY]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMax().getY())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMaxZ]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMax().getZ())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMaxBlockX]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMax().getX())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMaxBlockY]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMax().getY())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMaxBlockZ]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMax().getZ())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMinX]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMin().getX())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMinY]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMin().getY())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMinZ]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMin().getZ())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMinBlockX]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMin().getX())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMinBlockY]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMin().getY())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxMinBlockZ]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getMin().getZ())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxHeight]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getHeight())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxWidthX]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getWidthX())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxWidthZ]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getWidthZ())).toList()));
        map.put("[" + prefix + "StructuresBoundingBoxVolume]", String.join(", ", location.getChunk().getStructures().stream().map(gs -> String.valueOf(gs.getBoundingBox().getVolume())).toList()));
    }
}
