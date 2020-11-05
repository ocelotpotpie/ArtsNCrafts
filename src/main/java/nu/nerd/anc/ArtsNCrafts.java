package nu.nerd.anc;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

// ----------------------------------------------------------------------------
/**
 * Main plugin class.
 */
public class ArtsNCrafts extends JavaPlugin implements Listener {
    /**
     * This plugin instance.
     */
    public static ArtsNCrafts PLUGIN;

    /**
     * The configuration as a singleton.
     */
    // public static Configuration CONFIG = new Configuration();

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     * 
     *      Load the config and player settings. Start a task to update action bars.
     */
    @Override
    public void onEnable() {
        PLUGIN = this;

        // saveDefaultConfig();
        // CONFIG.reload(false);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * Prevent respawn anchors from exploding in non-nether dimensions.
     * 
     * This doesn't actually work because Spigot doesn't support it yet.
     */
    @EventHandler(ignoreCancelled = true)
    protected void onBlockExplode(BlockExplodeEvent event) {
        getLogger().info("Blocked respawn anchor explosion.");
        Block block = event.getBlock();
        if (block.getType() == Material.RESPAWN_ANCHOR
                && block.getWorld().getEnvironment() != Environment.NETHER) {
            event.setCancelled(true);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Since BlockExplodeEvent doesn't appear to be raised for respawn anchors,
     * prevent interactions with them in non-nether worlds, except if the player is
     * right-clicking glowstone.
     * 
     * Minecraft also makes the anchor explode when you right click with glowstone
     * in your hand on a fully charged anchor.
     * 
     * This code can go away when Spigot implements BlockExplodeEvent for respawn
     * anchors.
     */
    @EventHandler(ignoreCancelled = true)
    protected void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block.getType() != Material.RESPAWN_ANCHOR
                || block.getWorld().getEnvironment() == Environment.NETHER
                || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        RespawnAnchor anchor = (RespawnAnchor) block.getBlockData();
        if (item == null || item.getType() != Material.GLOWSTONE
                || anchor.getCharges() == anchor.getMaximumCharges()) {
            event.setCancelled(true);
        }
    }

    // ------------------------------------------------------------------------
} // class ArtsNCrafts
