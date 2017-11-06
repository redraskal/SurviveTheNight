package me.redraskal.survivethenight.game;

import lombok.Getter;
import lombok.Setter;
import me.redraskal.survivethenight.SurviveTheNight;
import me.redraskal.survivethenight.utils.NMSUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Generator {

    @Getter private final SurviveTheNight surviveTheNight;
    @Getter private final Arena arena;
    @Getter private final Block block;
    @Getter @Setter private boolean running = false;
    @Getter @Setter private int fuelPercentage = 0;

    @Getter private final ArmorStand armorStand;
    @Getter private final ArmorStand armorStand2;

    public Generator(SurviveTheNight surviveTheNight, Arena arena, Block block) {
        this.surviveTheNight = surviveTheNight;
        this.arena = arena;
        this.block = block;

        this.armorStand = block.getWorld().spawn(block.getRelative(BlockFace.UP).getLocation().clone(), ArmorStand.class);
        this.armorStand2 = block.getWorld().spawn(armorStand.getLocation().clone().subtract(0D, 0.3D, 0D), ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);

        armorStand2.setVisible(false);
        armorStand2.setBasePlate(false);
        armorStand2.setGravity(false);
        armorStand2.setSmall(true);

        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&l✖ &cGenerator &c&l✖"));
        armorStand.setCustomNameVisible(true);

        armorStand2.setCustomName(ChatColor.translateAlternateColorCodes('&', "&8▍▍▍▍▍▍▍▍▍▍▍▍▍▍"));
        armorStand2.setCustomNameVisible(true);
    }

    public void fill(Player player) {
        if(running) return;
        if(player.getItemInHand() != null && this.getSurviveTheNight().getCustomItemManager()
                .isSameItem(player.getItemInHand(), this.getSurviveTheNight()
                        .getCustomItemManager().getFuelCanItemStack())) {
            if(player.getItemInHand().getAmount() == 1) {
                player.getInventory().setItemInHand(new ItemStack(Material.AIR));
            } else {
                ItemStack itemStack = player.getItemInHand();
                itemStack.setAmount(itemStack.getAmount()-1);
                player.getInventory().setItemInHand(itemStack);
            }

            fuelPercentage+=25;
            if(fuelPercentage >= 100) {
                running = true;
                block.getWorld().strikeLightningEffect(block.getLocation());
                armorStand.remove();
                armorStand2.setCustomName(ChatColor.translateAlternateColorCodes('&', "&a&l✓ &aGenerator &a&l✓"));
                armorStand2.setCustomNameVisible(true);

                int generatorsFilled = this.getArena().getGameRunnable().getGeneratorsFilled();

                if(generatorsFilled < (this.getArena().getGeneratorsNeeded()+1)) {
                    this.getArena().getScoreboardMap().values().forEach(scoreboard -> {
                        if(generatorsFilled == this.getArena().getGeneratorsNeeded()) {
                            scoreboard.add("&aOpen", 7);
                        }
                        scoreboard.add("&f" + (this.getArena().getGeneratorsNeeded()-generatorsFilled) + " Generators until", 5);
                        scoreboard.update();
                    });
                }

                if(generatorsFilled == this.getArena().getGeneratorsNeeded()) {
                    //TODO: Open Exit Gates
                }

                try {
                    NMSUtils.clearTitle(player);
                    NMSUtils.sendTitleSubtitle(player, "&a+ 25%", "&2▍▍▍▍▍▍▍▍▍▍▍▍▍▍", 0, 20, 10);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } else {
                if(fuelPercentage == 25) {
                    armorStand2.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c▍▍▍&8▍▍▍▍▍▍▍▍▍▍▍"));
                    armorStand2.setCustomNameVisible(true);
                }
                if(fuelPercentage == 50) {
                    armorStand2.setCustomName(ChatColor.translateAlternateColorCodes('&', "&e▍▍▍▍▍▍▍&8▍▍▍▍▍▍▍"));
                    armorStand2.setCustomNameVisible(true);
                }
                if(fuelPercentage == 75) {
                    armorStand2.setCustomName(ChatColor.translateAlternateColorCodes('&', "&2▍▍▍▍▍▍▍▍▍▍&8▍▍▍▍"));
                    armorStand2.setCustomNameVisible(true);
                }

                try {
                    NMSUtils.clearTitle(player);
                    NMSUtils.sendTitleSubtitle(player, "&a+ 25%", armorStand2.getCustomName(), 0, 20, 10);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                try {
                    NMSUtils.playSoundEffect(block.getLocation(), "mob.guardian.flop", 0.8f, 0.39f);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}