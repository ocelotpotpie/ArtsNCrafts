package nu.nerd.anc;

import java.util.List;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Painting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import nu.nerd.anc.commands.PaintingExecutor;

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
     *      Load the config and player settings. Start a task to update action
     *      bars.
     */
    @Override
    public void onEnable() {
        PLUGIN = this;

        // saveDefaultConfig();
        // CONFIG.reload(false);

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("painting").setExecutor(new PaintingExecutor());
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
     * prevent interactions with them in non-nether worlds, except if the player
     * is right-clicking glowstone.
     *
     * Minecraft also makes the anchor explode when you right click with
     * glowstone in your hand on a fully charged anchor.
     *
     * This code can go away when Spigot implements BlockExplodeEvent for
     * respawn anchors.
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
    /**
     * Set the content of the next placed painting per the /painting command.
     */
    @EventHandler(ignoreCancelled = true)
    public void onHangingPlaceEvent(HangingPlaceEvent event) {
        if (event.getEntity() instanceof Painting) {
            List<MetadataValue> metaList = event.getPlayer().getMetadata(PaintingExecutor.PAINTING_META_KEY);
            if (metaList.size() == 1) {
                MetadataValue meta = metaList.get(0);
                if (meta.getOwningPlugin() == this && meta.value() instanceof Art) {
                    Painting painting = (Painting) event.getEntity();
                    painting.setArt((Art) meta.value());
                }

                // After placing a painting, clear the metadata so the next
                // painting is random again.
                event.getPlayer().removeMetadata(PaintingExecutor.PAINTING_META_KEY, this);
            }
        }
    }
} // class ArtsNCrafts
