package dev.atarim.xplevel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class XPLevel extends JavaPlugin {

    // private HashMap<UUID, LevelLogic> levelLogicHashMap;

    private Connection connection;
    public String host, database, username, password, table;
    public int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        mysqlSetup();

        // this.levelLogicHashMap = new HashMap<>();
        this.getServer().getPluginManager().registerEvents(new LevelSQL(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
/*
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
*/

    public void mysqlSetup(){
        host = "localhost";
        port = 3306;
        database = "test";
        username = "root";
        password = "password";
        table = "players";

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
