package dev.atarim.xplevel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SkillAction implements Listener {

    @EventHandler
    public void clickEvent (InventoryClickEvent click) {
        Player player = (Player) click.getWhoClicked();
        if (click.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Skills | "
                + ChatColor.DARK_BLUE + new LevelSQL().getValueSQL("skillPoints", player.getUniqueId().toString())
                + ChatColor.BLACK + " Points Remaining" )) {
            switch (click.getCurrentItem().getType()) {
                case ENCHANTED_GOLDEN_APPLE:
                    player.closeInventory();
                    new LevelSQL().skillUp("health", player);
                    break;
                case DIAMOND_SWORD:
                    player.closeInventory();
                    new LevelSQL().skillUp("strength", player);
                    break;
                case LEATHER_BOOTS:
                    player.closeInventory();
                    new LevelSQL().skillUp("speed", player);
                    break;
                case BREAD:
                    player.closeInventory();
                    // TODO acrobatics :(
                    new LevelSQL().skillUp("endurance", player);
                    break;
                case ENCHANTED_BOOK:
                    player.closeInventory();
                    new LevelSQL().skillUp("intelligence", player);
                    break;
                default:
                    player.sendMessage("Uh-oh something went wrong.");
                    break;
            }
            click.setCancelled(true);
        }
    }
}
