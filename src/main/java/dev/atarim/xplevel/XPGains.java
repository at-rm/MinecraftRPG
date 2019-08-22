package dev.atarim.xplevel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class XPGains implements Listener {

    private XPLevel plugin;
    private LevelLogic levelLogic;

    public XPGains (XPLevel plugin, LevelLogic levelLogic) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.levelLogic = levelLogic;
    }

    @EventHandler
    public void onBreak (BlockBreakEvent e) {
        Material material = e.getBlock().getType();
        Location location = e.getBlock().getLocation().add(0.5, 0, 0.5);
        e.setCancelled(true);
        e.getBlock().setType(Material.AIR);
        switch (material) {
            case COAL_ORE:
                location.getWorld().dropItem(location, new ItemStack(Material.COAL));
                e.getBlock().setType(Material.AIR);
                break;
            case REDSTONE_ORE:
                location.getWorld().dropItem(location, new ItemStack(Material.REDSTONE, 4 + new Random().nextInt(1)));
                e.getBlock().setType(Material.AIR);
                break;
            case LAPIS_ORE:
                location.getWorld().dropItem(location, new ItemStack(Material.LAPIS_LAZULI, 4 + new Random().nextInt(4)));
                e.getBlock().setType(Material.AIR);
                break;
            case DIAMOND_ORE:
                location.getWorld().dropItem(location, new ItemStack(Material.DIAMOND));
                e.getBlock().setType(Material.AIR);
                break;
            case EMERALD_ORE:
                location.getWorld().dropItem(location, new ItemStack(Material.EMERALD));
                e.getBlock().setType(Material.AIR);
                break;
            default:
                location.getWorld().dropItem(location, new ItemStack(material));
                e.getBlock().setType(Material.AIR);
                break;
        }
        levelLogic.gainXP(2);
    }

}
