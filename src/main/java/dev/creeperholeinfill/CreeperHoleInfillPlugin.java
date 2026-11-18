package dev.creeperholeinfill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/** Restores creeper-damaged terrain gradually, using Folia's region scheduler. */
public final class CreeperHoleInfillPlugin extends JavaPlugin implements Listener {
    private long delayMillis;
    private double delayFalloff;
    private long minimumDelayMillis;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadSettings();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void reloadSettings() {
        delayMillis = Math.max(0L, getConfig().getLong("delay", 2000L));
        delayFalloff = Math.max(0.0, getConfig().getDouble("delay-falloff", 0.175));
        minimumDelayMillis = Math.max(50L, getConfig().getLong("minimum-delay", 50L));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (event.getEntityType() != EntityType.CREEPER || event.blockList().isEmpty()) {
            return;
        }

        // Capture before the server applies the explosion. Only touch blocks owned by this region;
        // leave other regions' blocks in the vanilla list. Replacing blocks directly avoids drops
        // while preserving the explosion's entity damage and sound.
        List<Block> managedBlocks = new ArrayList<>();
        List<BlockState> states = new ArrayList<>(event.blockList().size());
        for (Block block : event.blockList()) {
            Location location = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
            if (!Bukkit.isOwnedByCurrentRegion(location)) {
                continue;
            }
            managedBlocks.add(block);
            states.add(block.getState());
        }
        for (Block block : managedBlocks) {
            block.setType(Material.AIR, false);
        }
        event.blockList().removeAll(managedBlocks);

        scheduleRebuild(states);
    }

    private void scheduleRebuild(List<BlockState> states) {
        if (states.isEmpty()) {
            return;
        }

        Vector center = new Vector();
        int highestY = Integer.MIN_VALUE;
        for (BlockState state : states) {
            center.add(state.getLocation().toVector());
            highestY = Math.max(highestY, state.getY());
        }
        center.multiply(1.0 / states.size()).setY(highestY + 1.0);

        states.sort(Comparator.comparingDouble(state ->
                state.getLocation().toVector().distanceSquared(center)));

        long dueTicks = millisecondsToTicks(delayMillis);
        for (int index = states.size() - 1; index >= 0; index--) {
            BlockState state = states.get(index);
            long scheduledDelay = dueTicks;

            Bukkit.getRegionScheduler().runDelayed(this, state.getLocation(), task -> restore(state), scheduledDelay);

            long nextDelayMillis = Math.max(minimumDelayMillis,
                    (long) (delayMillis * Math.exp(-(states.size() - index) * delayFalloff)));
            dueTicks += millisecondsToTicks(nextDelayMillis);
        }
    }

    private void restore(BlockState state) {
        Block block = state.getBlock();
        // Leave player or plugin edits alone if the block changed while the crater was open.
        if (block.getType() != Material.AIR) {
            return;
        }

        state.update(true, false);
        block.getWorld().playSound(
                block.getLocation(),
                block.getBlockSoundGroup().getPlaceSound(),
                SoundCategory.BLOCKS,
                1.0f,
                0.8f
        );
    }

    private static long millisecondsToTicks(long milliseconds) {
        return Math.max(1L, (milliseconds + 49L) / 50L);
    }
}
