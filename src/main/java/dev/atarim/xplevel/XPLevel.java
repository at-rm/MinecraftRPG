package dev.atarim.xplevel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public final class XPLevel extends JavaPlugin implements Listener {

    private HashMap<UUID, LevelLogic> levelLogicHashMap;

    private Connection connection;
    public String host, database, username, password;
    public int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        mysqlSetup();

        this.levelLogicHashMap = new HashMap<>();
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void join (PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

       if (!levelLogicHashMap.containsKey(player.getUniqueId())) {
            levelLogicHashMap.put(player.getUniqueId(), new LevelLogic(player));
        }
       levelLogicHashMap.put(player.getUniqueId(), levelLogicHashMap.get(player.getUniqueId()));
       player.sendMessage(ChatColor.GOLD + "Hey " + ChatColor.AQUA + player.getName() +
                ChatColor.GOLD +"! Your current level is " + ChatColor.RED +
                + levelLogicHashMap.get(player.getUniqueId()).getLevel() + ChatColor.GOLD + ".");
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
        levelLogicHashMap.get(e.getPlayer().getUniqueId()).gainXP(2);
    }

    public void mysqlSetup(){
        host = "localhost";
        port = 3306;
        database = "test";
        username = "root";
        password = "password";

        try{

            synchronized (this){
                if(getConnection() != null && !getConnection().isClosed()){
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                setConnection( DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                        + this.port + "/" + this.database, this.username, this.password));

                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
