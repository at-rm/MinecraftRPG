package dev.atarim.xplevel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class XPLevel extends JavaPlugin {

    private Connection connection;
    public String host, database, username, password, tablePlayers, tableReviews;
    public int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();

        mysqlSetup();
        this.getServer().getPluginManager().registerEvents(new LevelSQL(), this);
        getCommand("skills").setExecutor(new SkillMenuCommand());
        getServer().getPluginManager().registerEvents(new SkillAction(), this);
        getCommand("review").setExecutor(new ReviewSystem());
    }

    /**
     * Load configuration for database from config.yml file.
     */
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        host = this.getConfig().getString("host");
        port = this.getConfig().getInt("port");
        database = this.getConfig().getString("database");
        username = this.getConfig().getString("username");
        password = this.getConfig().getString("password");
        tablePlayers = this.getConfig().getString("tablePlayers");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void mysqlSetup(){

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
