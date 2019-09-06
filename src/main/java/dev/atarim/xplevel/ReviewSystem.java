package dev.atarim.xplevel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewSystem implements CommandExecutor {

    private XPLevel plugin = XPLevel.getPlugin(XPLevel.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 1) {
                // TODO open own reviews
            } else if (args.length == 1) {
                // TODO check someone's reviews in GUI with papers
            } else if (args.length == 3 && (args [2].equalsIgnoreCase("good") || args[2].equalsIgnoreCase("bad"))) {
                // TODO write a review for someone
                // /review <playerName> <good/bad> <review text (no more than 200 characters)>
            } else {
                player.sendMessage(ChatColor.RED + "You seem lost, did you mean:");
                player.sendMessage("/review Check the feedback players left for you");
                player.sendMessage("/review <player> Check the feedback players have left for the player");
                player.sendMessage("/review <player> <good/bad> Leave a review for a player");
            }
        }

        return true;
    }

    private void addReview(Player fore, Player from, String goodBad, String review) {
        String fromUuid = from.getUniqueId().toString();
        String forUuid = fore.getUniqueId().toString();
        int gb = goodBad.equalsIgnoreCase("good") ? 1 : 0;

        String command = "INSERT INTO player_review (forUuid, fromUuid, goodOrBad, review) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement add = plugin.getConnection().prepareStatement(command);
            add.setString(1, forUuid);
            add.setString(2, fromUuid);
            add.setInt(3, gb);
            add.setString(4, review);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean playerExists(Player fore) {

        return true;
    }

}