package nu.nerd.anc.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import nu.nerd.anc.ArtsNCrafts;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * String, String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("You must be a player to use this command."));
            return true;
        }

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            // List paintings after help.
            Bukkit.getScheduler().runTask(ArtsNCrafts.PLUGIN, () -> {
                final TextComponent.Builder builder = Component.text();
//                StringBuilder message = new StringBuilder();
                builder.append(Component.text("Available paintings: ", NamedTextColor.GOLD));

                Component sep = Component.text("");

                for (int i = 0; i < Art.values().length; ++i) {
                    Art art = Art.values()[i];
                    builder.append(sep);
                    builder.append(Component.text(art.name().toLowerCase(), NamedTextColor.YELLOW));
                    builder.append(Component.text(" (" + art.getBlockHeight() + "x" + art.getBlockHeight() + ")", NamedTextColor.GRAY));
                    sep = Component.text(", ");
                }
                sender.sendMessage(builder);
            });

            // Show help:
            return false;
        }

        if (args.length == 1) {
            Player player = (Player) sender;
            Art art = Art.getByName(args[0]);
            if (art != null) {
                player.setMetadata(PAINTING_META_KEY, new FixedMetadataValue(ArtsNCrafts.PLUGIN, art));
                sender.sendMessage(Component.text()
                        .append(Component.text("The next painting you place will be: ", NamedTextColor.GOLD))
                        .append(Component.text(art.name().toLowerCase(), NamedTextColor.YELLOW)));
            } else {
                sender.sendMessage(Component.text("Unknown painting: " + args[0], NamedTextColor.RED));
            }
            return true;
        }
        return false;
    }

    // ------------------------------------------------------------------------

    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(CommandSender,
     * Command, String, String[])
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
