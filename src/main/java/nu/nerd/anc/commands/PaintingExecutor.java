package nu.nerd.anc.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import nu.nerd.anc.ArtsNCrafts;

// ----------------------------------------------------------------------------
/**
 * Executor for /painting.
 */
public class PaintingExecutor implements TabExecutor {
    // ------------------------------------------------------------------------
    /**
     * Key of Player metadata used to record most recently selected painting.
     */
    public static final String PAINTING_META_KEY = "ArtsNCrafts.painting";

    // ------------------------------------------------------------------------
    /**
     * All painting names in lower case.
     */
    static final List<String> PAINTING_NAMES = Stream.of(Art.values())
        .map(a -> a.name().toLowerCase())
        .collect(Collectors.toList());

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(CommandSender, Command,
     *      String, String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be in-game to use this command.");
            return true;
        }

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            // List paintings after help.
            Bukkit.getScheduler().runTask(ArtsNCrafts.PLUGIN, () -> {
                StringBuilder message = new StringBuilder();
                message.append(ChatColor.GOLD);
                message.append("Available paintings: ");
                String sep = "";
                for (int i = 0; i < Art.values().length; ++i) {
                    Art art = Art.values()[i];
                    message.append(sep);
                    message.append(ChatColor.YELLOW);
                    message.append(art.name().toLowerCase());
                    message.append(ChatColor.GRAY);
                    message.append(" (");
                    message.append(art.getBlockWidth());
                    message.append('x');
                    message.append(art.getBlockHeight());
                    message.append(")");
                    sep = ", ";
                }
                sender.sendMessage(message.toString());
            });

            // Show help:
            return false;
        }

        if (args.length == 1) {
            Player player = (Player) sender;
            Art art = Art.getByName(args[0]);
            if (art != null) {
                player.setMetadata(PAINTING_META_KEY, new FixedMetadataValue(ArtsNCrafts.PLUGIN, art));
                sender.sendMessage(ChatColor.GOLD + "The next painting you place will be: " +
                                   ChatColor.YELLOW + art.name().toLowerCase());
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown painting: " + args[0]);
            }
            return true;
        }
        return false;
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(CommandSender,
     *      Command, String, String[])
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 0) {
            completions.addAll(PAINTING_NAMES);
        } else if (args.length == 1) {
            completions.addAll(PAINTING_NAMES.stream()
                .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList()));
        }
        return completions;
    }
}
