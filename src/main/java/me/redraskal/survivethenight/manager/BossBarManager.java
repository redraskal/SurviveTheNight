package me.redraskal.survivethenight.manager;

import lombok.Getter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.listener.BossBarListener;
import me.redraskal.survivethenight.utils.NMSUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class BossBarManager {

    @Getter private final SurviveTheNight surviveTheNight;
    @Getter private final BossBarListener bossBarListener;
    private Map<Player, Object> enderDragons = new HashMap<>();

    private final Class<?> c_worldServer = Class.forName(NMSUtils.fetchNMSClass("WorldServer"));
    private final Class<?> c_entity = Class.forName(NMSUtils.fetchNMSClass("Entity"));
    private final Class<?> c_entityLiving = Class.forName(NMSUtils.fetchNMSClass("EntityLiving"));
    private final Class<?> c_entityEnderDragon = Class.forName(NMSUtils.fetchNMSClass("EntityEnderDragon"));
    private final Class<?> c_packetPlayOutSpawnEntityLiving = Class.forName(NMSUtils.fetchNMSClass("PacketPlayOutSpawnEntityLiving"));
    private final Class<?> c_packetPlayOutEntityTeleport = Class.forName(NMSUtils.fetchNMSClass("PacketPlayOutEntityTeleport"));
    private final Class<?> c_packetPlayOutEntityMetadata = Class.forName(NMSUtils.fetchNMSClass("PacketPlayOutEntityMetadata"));
    private final Class<?> c_packetPlayOutEntityDestroy = Class.forName(NMSUtils.fetchNMSClass("PacketPlayOutEntityDestroy"));
    private final Class<?> c_dataWatcher = Class.forName(NMSUtils.fetchNMSClass("DataWatcher"));

    private final Class<?> c_craftWorld = Class.forName(NMSUtils.fetchBukkitClass("CraftWorld"));

    private final Method c_getHandle = c_craftWorld.getDeclaredMethod("getHandle");
    private final Method c_setLocation = c_entity.getDeclaredMethod("setLocation", double.class, double.class, double.class,
            float.class, float.class);
    private final Method c_a = c_dataWatcher.getDeclaredMethod("a", int.class, Object.class);
    private final Method c_getId = c_entity.getDeclaredMethod("getId");

    private final Field f_l = c_packetPlayOutSpawnEntityLiving.getDeclaredField("l");

    public BossBarManager(SurviveTheNight surviveTheNight) throws Exception {
        this.surviveTheNight = surviveTheNight;
        this.bossBarListener = new BossBarListener(this);
        this.getSurviveTheNight().getServer().getPluginManager().registerEvents(
                this.getBossBarListener(), this.getSurviveTheNight());
        new BukkitRunnable() {
            public void run() {
                enderDragons.keySet().forEach(player -> {
                    try {
                        teleportBar(player);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }.runTaskTimer(this.getSurviveTheNight(), 0, 20L);
    }

    /**
     * Sends a fake BossBar to the specified player.
     * @param player
     * @param text
     * @param percentage
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    public void sendBossBar(Player player, String text, double percentage) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException, InstantiationException,
            NoSuchFieldException, ClassNotFoundException {
        if(enderDragons.containsKey(player)) {
            this.updateBossBar(player, text, percentage);
            return;
        }

        text = ChatColor.translateAlternateColorCodes('&', text);

        Location location = player.getLocation().clone();
        Object craftWorld = c_craftWorld.cast(player.getLocation().getWorld());
        Object worldServer = c_getHandle.invoke(craftWorld);

        Object entityEnderDragon = c_entityEnderDragon.getConstructor(c_worldServer.getSuperclass())
                .newInstance(worldServer);
        c_setLocation.invoke(entityEnderDragon, location.getX(), location.getY() - 100, location.getZ(), 0, 0);

        Object packetPlayOutSpawnEntityLiving = c_packetPlayOutSpawnEntityLiving.getConstructor(c_entityLiving)
                .newInstance(entityEnderDragon);
        Object dataWatcher = c_dataWatcher.getConstructor(c_entity).newInstance(new Object[]{null});
        c_a.invoke(dataWatcher, 0, (byte) 0x20);
        c_a.invoke(dataWatcher, 6, (float) (percentage * 200) / 100);
        c_a.invoke(dataWatcher, 10, text);
        c_a.invoke(dataWatcher, 2, text);
        c_a.invoke(dataWatcher, 11, (byte) 1);
        c_a.invoke(dataWatcher, 3, (byte) 1);

        f_l.setAccessible(true);
        f_l.set(packetPlayOutSpawnEntityLiving, dataWatcher);

        enderDragons.put(player, entityEnderDragon);
        NMSUtils.sendPacket(player, packetPlayOutSpawnEntityLiving);
    }

    /**
     * Sends a fake BossBar to the specified player.
     * @param player
     * @param text
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    public void sendBossBar(Player player, String text) throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException, InstantiationException, NoSuchFieldException, ClassNotFoundException {
        this.sendBossBar(player, text, 100D);
    }

    /**
     * Updates the specified player's BossBar.
     * @param player
     * @param text
     * @param percentage
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    public void updateBossBar(Player player, String text, double percentage) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException,
            NoSuchFieldException, ClassNotFoundException {
        if(!enderDragons.containsKey(player)) return;

        if(text != null) text = ChatColor.translateAlternateColorCodes('&', text);

        int entityID = (int) c_getId.invoke(enderDragons.get(player));
        Object dataWatcher = c_dataWatcher.getConstructor(c_entity).newInstance(new Object[]{null});
        c_a.invoke(dataWatcher, 0, (byte) 0x20);
        if(percentage != -1) c_a.invoke(dataWatcher, 6, (float) (percentage * 200) / 100);
        if(text != null) {
            c_a.invoke(dataWatcher, 10, text);
            c_a.invoke(dataWatcher, 2, text);
        }
        c_a.invoke(dataWatcher, 11, (byte) 1);
        c_a.invoke(dataWatcher, 3, (byte) 1);

        Object packetPlayOutEntityMetadata = c_packetPlayOutEntityMetadata.getConstructor(
                int.class, c_dataWatcher, boolean.class)
                .newInstance(entityID, dataWatcher, true);
        NMSUtils.sendPacket(player, packetPlayOutEntityMetadata);
    }

    /**
     * Updates the specified player's BossBar.
     * @param player
     * @param percentage
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public void updateBossBar(Player player, double percentage) throws NoSuchMethodException,
            IllegalAccessException, InstantiationException, ClassNotFoundException,
            InvocationTargetException, NoSuchFieldException {
        this.updateBossBar(player, null, percentage);
    }

    /**
     * Updates the specified player's BossBar.
     * @param player
     * @param text
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public void updateBossBar(Player player, String text) throws NoSuchMethodException,
            IllegalAccessException, InstantiationException, ClassNotFoundException,
            InvocationTargetException, NoSuchFieldException {
        this.updateBossBar(player, text, -1);
    }

    /**
     * Removes the BossBar a player has (if any).
     * @param player
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    public void removeBar(Player player) throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException, InstantiationException, NoSuchFieldException, ClassNotFoundException {
        if(!enderDragons.containsKey(player)) return;

        int entityID = (int) c_getId.invoke(enderDragons.get(player));
        Object packetPlayOutEntityDestroy = c_packetPlayOutEntityDestroy.getConstructor(int[].class)
                .newInstance(new int[]{entityID});

        enderDragons.remove(player);
        NMSUtils.sendPacket(player, packetPlayOutEntityDestroy);
    }

    /**
     * Teleports the BossBar a player has to a viewable location (if any).
     * @param player
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    private void teleportBar(Player player) throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException, InstantiationException, NoSuchFieldException, ClassNotFoundException {
        if(!enderDragons.containsKey(player)) return;

        int entityID = (int) c_getId.invoke(enderDragons.get(player));
        Location location = player.getLocation().clone();
        Object packetPlayOutEntityTeleport = c_packetPlayOutEntityTeleport.getConstructor(
                int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class)
                .newInstance(entityID, (int) (location.getX() * 32),
                        (int) ((location.getY() - 100) * 32), (int) (location.getZ() * 32),
                        (byte) ((int) location.getYaw() * 256 / 360),
                        (byte) ((int) location.getPitch() * 256 / 360), false);

        NMSUtils.sendPacket(player, packetPlayOutEntityTeleport);
    }
}