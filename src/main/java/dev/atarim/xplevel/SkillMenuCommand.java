package dev.atarim.xplevel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;


public class SkillMenuCommand implements CommandExecutor {

    private static int[] bases = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
    private static HashMap<Integer, String> map = new HashMap<Integer, String>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getUniqueId().toString();
            Inventory skillMenu = Bukkit.createInventory(player, 27, ChatColor.DARK_GREEN + "Skills");

            // new ItemMeta for every skill
            ItemStack health = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
            ItemStack strength = new ItemStack (Material.DIAMOND_SWORD);
            ItemStack speed = new ItemStack (Material.LEATHER_BOOTS);
            ItemStack endurance = new ItemStack (Material.BREAD);
            ItemStack intelligence = new ItemStack(Material.ENCHANTED_BOOK);

            ItemStack filler = new ItemStack(Material.AIR);

            // new Meta for every skill
            ItemMeta healthMeta = health.getItemMeta();
            ItemMeta strengthMeta = strength.getItemMeta();
            ItemMeta speedMeta = speed.getItemMeta();
            ItemMeta enduranceMeta = endurance.getItemMeta();
            ItemMeta intelligenceMeta = intelligence.getItemMeta();

            // health skill appearance
            healthMeta.setDisplayName(ChatColor.GOLD + "Health " + romanSkillLevel("healthLevel", playerName));
            ArrayList<String> healthLore = new ArrayList<>();
            healthLore.add(ChatColor.DARK_BLUE + "Increases your maximum health.");
            healthMeta.setLore(healthLore);
            health.setItemMeta(healthMeta);

            // strength skill appearance
            strengthMeta.setDisplayName(ChatColor.GOLD + "Strength " + romanSkillLevel("strengthLevel", playerName));
            ArrayList<String> strengthLore = new ArrayList<>();
            strengthLore.add(ChatColor.DARK_BLUE + "Increases your base attack damage.");
            strengthMeta.setLore(strengthLore);
            strength.setItemMeta(strengthMeta);

            // speed skill appearance
            speedMeta.setDisplayName(ChatColor.GOLD + "Speed " + romanSkillLevel("speedLevel", playerName));
            ArrayList<String> speedLore = new ArrayList<>();
            speedLore.add(ChatColor.DARK_BLUE + "Increases your movement and attack speed.");
            speedMeta.setLore(speedLore);
            speed.setItemMeta(speedMeta);

            // endurance skill appearance
            enduranceMeta.setDisplayName(ChatColor.GOLD + "Endurance " + romanSkillLevel("enduranceLevel", playerName));
            ArrayList<String> enduranceLore = new ArrayList<>();
            // TODO what does endurance do? :c
            enduranceLore.add(ChatColor.DARK_BLUE + "Increases your endurance.");
            enduranceMeta.setLore(enduranceLore);
            endurance.setItemMeta(enduranceMeta);

            // intelligence skill appearance
            intelligenceMeta.setDisplayName(ChatColor.GOLD + "Intelligence " + romanSkillLevel("intelligenceLevel", playerName));
            ArrayList<String> intelligenceLore = new ArrayList<>();
            intelligenceLore.add(ChatColor.DARK_BLUE + "Increases your XP gain.");
            intelligenceMeta.setLore(intelligenceLore);
            intelligence.setItemMeta(intelligenceMeta);

            // Construct GUI
            ItemStack[] menu = new ItemStack[27];
            for (int i = 0; i < menu.length; i++) {
                 switch (i) {
                     case 11:
                         menu [i] = health;
                         break;
                     case 12:
                         menu [i] = strength;
                         break;
                     case 13:
                         menu [i] = speed;
                         break;
                     case 14:
                         menu [i] = endurance;
                         break;
                     case 15:
                         menu [i] = intelligence;
                         break;
                     default:
                         menu [i] = filler;
                         break;
                 }
            }
            skillMenu.setContents(menu);
            player.openInventory(skillMenu);
        }

        return true;
    }

    private String romanSkillLevel(String skill, String playerName) {
        int healthLevel = new LevelSQL().getValueSQL(skill, playerName);
        return intToRoman(healthLevel + 1);
    }

    private static void setup()
        {
            map.put(0, "");
            map.put(1, "I");
            map.put(4, "IV");
            map.put(5, "V");
            map.put(9, "IX");
            map.put(10, "X");
            map.put(40, "XL");
            map.put(50, "L");
            map.put(90, "XC");
            map.put(100, "C");
            map.put(400, "CD");
            map.put(500, "D");
            map.put(900, "CM");
            map.put(1000, "M");
        }

    public String intToRoman(int num) {
        setup();
        String result = "";

        for (int i : bases) {
            while (num >= i)
            {
                result += map.get(i);
                num -= i;
            }
        }
        return result;
     }
}
