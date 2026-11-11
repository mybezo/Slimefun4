package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ASlimefunDataContainer;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes.UniversalBlock;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.ErrorReport;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.ticker.TickLocation;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ParticleUtil;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * The {@link TickerTask} is responsible for ticking every {@link BlockTicker},
 * synchronous or not.
 *
 * @author TheBusyBiscuit
 *
 * @see BlockTicker
 *
 */
@Getter
public class TickerTask implements Runnable {

    /**
     * This Map holds all currently actively ticking locations.
     * The value of this map (Set entries) MUST be thread-safe and mutable.
     */
    private final Map<ChunkPosition, Set<TickLocation>> tickingLocations = new ConcurrentHashMap<>();

    /**
     * This Map tracks how many bugs have occurred in a given Location .
     * If too many bugs happen, we delete that Location.
     */
    private final Map<BlockPosition, Integer> bugs = new ConcurrentHashMap<>();

    private int count = 0;

    /**
     * -- GETTER --
     *  This returns the delay between ticks
     *
     * @return The tick delay
     */
    @Setter
    private int tickRate;

    private boolean halted = false;
    private boolean running = false;

    @Setter
    private volatile boolean paused = false;

    private final Deque<WaitingEntry> waiting = new ConcurrentLinkedDeque<>();

    private final ExecutorService tickExecutor =
            Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "Slimefun - Ticker Thread"));

    /**
     * @see #stepTo(WaitingEntry, boolean)
     */
    private final ExecutorService operateExecutor =
            Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "Slimefun - Ticker Operation Thread"));

    /**
     * Debounce
     * Whether a step task is already running on {@link #operateExecutor}. See {@link #stepTo}.
     */
    private final AtomicBoolean stepping = new AtomicBoolean(false);

    private final int PAGE_SIZE = 10;

    @Setter
    private volatile boolean tickFreeze = false;

    // false -> not paused, true -> paused
    private volatile Predicate<WaitingEntry> tickFreezePredicate = entry -> false;

    public void setTickFreezePredicate(Predicate<WaitingEntry> predicate) {
        this.tickFreezePredicate = predicate != null ? predicate : entry -> false;
    }

    @Data
    public static class WaitingEntry {
        private static final AtomicLong ID = new AtomicLong(0);
        private final Location location;
        private final SlimefunItem item;
        private final ASlimefunDataContainer data;
        private final boolean sync;
        private final long id;

        public WaitingEntry(Location location, SlimefunItem item, ASlimefunDataContainer data, boolean sync) {
            this.location = location;
            this.item = item;
            this.data = data;
            this.sync = sync;
            this.id = ID.getAndIncrement();
        }
    }

    /**
     * This method starts the {@link TickerTask} on an asynchronous schedule.
     *
     * @param plugin
     *            The instance of our {@link Slimefun}
     */
    public void start(@Nonnull Slimefun plugin) {
        this.tickRate = Slimefun.getCfg().getInt("URID.custom-ticker-delay");

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, this, 100L, 1);
    }

    /**
     * This method resets this {@link TickerTask} to run again.
     */
    private void reset() {
        running = false;
    }

    @Override
    public void run() {
        if (paused || stepping.get()) { // Do not run while stepping
            return;
        }

        if (tickFreeze && !waiting.isEmpty()) {
            return;
        }

        count += 1;
        if (count < tickRate) {
            return;
        }
        count = 0;

        // If this method is actually still running... DON'T
        if (running) {
            return;
        }

        running = true;

        try {
            flushWaitingEntries(); // Entries not executed during the last tick freeze
            Slimefun.getProfiler().start();
            Set<BlockTicker> tickers = new HashSet<>();

            // Run our ticker code
            if (!halted) {
                Set<Map.Entry<ChunkPosition, Set<TickLocation>>> loc;

                synchronized (tickingLocations) {
                    loc = new HashSet<>(tickingLocations.entrySet());
                }

                for (Map.Entry<ChunkPosition, Set<TickLocation>> entry : loc) {
                    // Add all machines to the waiting queue, tickFreeze is checked here
                    tickChunk(entry.getKey(), tickers, new HashSet<>(entry.getValue()));
                }
            }

            // The iteration phase is over, machines of this round actually start running now
            if (!tickFreeze) {
                flushWaitingEntries();
            }

            // Start a new tick cycle for every BlockTicker
            for (BlockTicker ticker : tickers) {
                ticker.startNewTick();
            }

            reset();
            Slimefun.getProfiler().stop();

            if (tickFreeze) {
                showWaitingList();
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "An Exception was caught while ticking the Block Tickers Task for Slimefun v"
                                    + Slimefun.getVersion());
            reset();

            int discarded = clearWaitingEntries();
            if (discarded > 0) {
                Slimefun.logger()
                        .log(
                                Level.SEVERE,
                                "" + discarded + " machines failed to tick and were discarded from the waiting queue");
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void tickChunk(ChunkPosition chunk, Set<BlockTicker> tickers, Set<TickLocation> locations) {
        // Only continue if the Chunk is actually loaded
        if (!chunk.isLoaded()) return;

        try {
            for (TickLocation l : locations) {
                if (l.isUniversal()) {
                    tickUniversalLocation(l.getUuid(), l.getLocation(), tickers);
                } else {
                    tickLocation(tickers, l.getLocation());
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException x) {
            Slimefun.logger()
                    .log(Level.SEVERE, x, () -> "An Exception has occurred while trying to resolve Chunk: " + chunk);
        }
    }

    private void tickLocation(@Nonnull Set<BlockTicker> tickers, @Nonnull Location l) {
        var blockData = StorageCacheUtils.getBlock(l);
        if (blockData == null || !blockData.isDataLoaded() || blockData.isPendingRemove()) {
            return;
        }

        SlimefunItem item = SlimefunItem.getById(blockData.getSfId());
        if (item != null && item.getBlockTicker() != null && !item.isDisabledIn(l.getWorld())) {
            enqueueTick(l, item, blockData, tickers);
        }
    }

    @ParametersAreNonnullByDefault
    private void tickUniversalLocation(UUID uuid, Location l, @Nonnull Set<BlockTicker> tickers) {
        var data = StorageCacheUtils.getUniversalBlock(uuid);
        var item = SlimefunItem.getById(data.getSfId());
        if (item != null && item.getBlockTicker() != null && !item.isDisabledIn(l.getWorld())) {
            enqueueTick(l, item, data, tickers);
        }
    }

    /**
     * Puts the entry into waiting
     * The actual execution happens in {@link #flushWaitingEntries()}.
     */
    @ParametersAreNonnullByDefault
    private void enqueueTick(Location l, SlimefunItem item, ASlimefunDataContainer data, Set<BlockTicker> tickers) {
        try {
            boolean sync = item.getBlockTicker().isSynchronized();

            item.getBlockTicker().update();
            waiting.add(new WaitingEntry(l, item, data, sync));

            tickers.add(item.getBlockTicker());
        } catch (Exception | LinkageError x) {
            reportErrors(l, item, x);
        }
    }

    private void timedTickBlock(boolean checkTickFreeze) {
        if (checkTickFreeze && tickFreeze) return;

        WaitingEntry entry = waiting.poll();
        if (entry == null) return;

        if (checkTickFreeze && tickFreezePredicate.test(entry)) {
            waiting.addFirst(entry);
            tickFreeze = true;
            return;
        }

        timedTickBlock(entry);
    }

    /**
     * Executes all pending waiting tick tasks
     */
    private void flushWaitingEntries() {
        while (!waiting.isEmpty() && !tickFreeze) {
            timedTickBlock(true);
        }
    }

    /**
     * {@link ClickEvent#callback} runs on the main thread, step over/run to operations may block the main thread
     * Use {@code operateExecutor} to avoid blocking the main thread
     */
    private void stepTo(WaitingEntry target, boolean inclusive) {
        // Debounce: do not allow two step tasks at the same time
        if (!stepping.compareAndSet(false, true)) {
            return;
        }

        try {
            operateExecutor.execute(() -> {
                try {
                    while (true) {
                        WaitingEntry head = waiting.peek();

                        // Stop if the queue is empty or the head has passed the target (advanced by another thread)
                        if (head == null || (inclusive ? head.id > target.id : head.id >= target.id)) {
                            break;
                        }

                        timedTickBlock(false);
                    }
                } finally {
                    stepping.set(false);
                    showWaitingList();
                }
            });
        } catch (RejectedExecutionException x) {
            stepping.set(false);
            Slimefun.logger().log(Level.SEVERE, x, () -> "Operate Executor is unavailable, cannot step");
        }
    }

    private int clearWaitingEntries() {
        int discarded = waiting.size();
        waiting.clear();
        return discarded;
    }

    /**
     * This method is called regardless of tickFreeze
     */
    private void timedTickBlock(WaitingEntry entry) {
        boolean profiling = Slimefun.getProfiler().isProfiling();
        timedTickBlock(entry, 10, TimeUnit.SECONDS, profiling); // default timeout
    }

    @ParametersAreNonnullByDefault
    private void timedTickBlock(WaitingEntry entry, long timeout, TimeUnit timeUnit, boolean profiling) {
        if (entry.data.isPendingRemove()) return; // The machine may have been removed while waiting

        Location l = entry.location;
        SlimefunItem item = entry.item;

        if (entry.isSync()) {
            Slimefun.getProfiler().scheduleEntries(1);
            Slimefun.runSync(() -> {
                long timestamp = profiling ? System.nanoTime() : 0;
                try {
                    // Bukkit's own Watchdog detects timeouts, no need to handle it here
                    tickBlock(entry);
                } catch (Exception | LinkageError x) {
                    reportErrors(l, item, x);
                } finally {
                    if (profiling) {
                        Slimefun.getProfiler().closeEntry(l, item, timestamp);
                    }
                }
            });
        } else {
            long timestamp = profiling ? Slimefun.getProfiler().newEntry() : 0;

            try {
                CompletableFuture.runAsync(() -> tickBlock(entry), tickExecutor).get(timeout, timeUnit);
            } catch (TimeoutException e) {
                reportTimeout(l, item, timeout, timeUnit, e);
            } catch (Exception | LinkageError x) {
                reportErrors(l, item, x);
            } finally {
                if (profiling) {
                    Slimefun.getProfiler().closeEntry(l, item, timestamp);
                }
            }
        }
    }

    @Internal
    @ParametersAreNonnullByDefault
    private void tickBlock(WaitingEntry entry) {
        Location l = entry.location;
        SlimefunItem item = entry.item;
        ASlimefunDataContainer data = entry.data;
        if (item.getBlockTicker().isUniversal()) {
            if (data instanceof SlimefunUniversalData universalData) {
                item.getBlockTicker().tick(l.getBlock(), item, universalData);
            } else {
                throw new IllegalStateException("BlockTicker is universal but item is non-universal!");
            }
        } else {
            if (data instanceof SlimefunBlockData blockData) {
                item.getBlockTicker().tick(l.getBlock(), item, blockData);
            } else {
                throw new IllegalStateException("BlockTicker is non-universal but item is universal!");
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void timedTickBlock(WaitingEntry entry, long timeout, TimeUnit timeUnit) {
        if (entry.data.isPendingRemove()) return; // The machine may have been removed while waiting

        Location l = entry.location;
        SlimefunItem item = entry.item;

        try {
            if (entry.isSync()) {
                // Bukkit's own Watchdog detects timeouts, no need to handle it here
                tickBlock(entry);
            } else {
                CompletableFuture.runAsync(
                                () -> {
                                    tickBlock(entry);
                                },
                                tickExecutor)
                        .get(timeout, timeUnit);
            }
        } catch (TimeoutException e) {
            reportTimeout(l, item, timeout, timeUnit, e);
        } catch (Exception | LinkageError x) {
            reportErrors(l, item, x);
        }
    }

    public static String getTimeUnitName(TimeUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> " nanoseconds";
            case MICROSECONDS -> " microseconds";
            case MILLISECONDS -> " milliseconds";
            case SECONDS -> " seconds";
            case MINUTES -> " minutes";
            case HOURS -> " hours";
            case DAYS -> " days";
        };
    }

    private void reportTimeout(Location l, SlimefunItem item, long timeout, TimeUnit timeUnit, TimeoutException e) {
        Slimefun.logger().log(Level.SEVERE, "World: {0} X: {1} Y: {2} Z: {3} ({4})", new Object[] {
            l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), item.getId()
        });
        Slimefun.logger()
                .log(
                        Level.SEVERE,
                        "The machine at this block ran for more than " + timeout + getTimeUnitName(timeUnit)
                                + " in the last tick, this may slow down Slimefun ticks severely!");
        reportErrors(l, item, e);
    }

    @ParametersAreNonnullByDefault
    private void reportErrors(Location l, SlimefunItem item, Throwable x) {
        BlockPosition position = new BlockPosition(l);
        int errors = bugs.getOrDefault(position, 0) + 1;

        if (errors == 1) {
            // Generate a new Error-Report
            new ErrorReport<>(x, l, item);
            bugs.put(position, errors);
        } else if (errors == 4) {
            Slimefun.logger().log(Level.SEVERE, "X: {0} Y: {1} Z: {2} ({3})", new Object[] {
                l.getBlockX(), l.getBlockY(), l.getBlockZ(), item.getId()
            });
            Slimefun.logger()
                    .log(
                            Level.SEVERE,
                            "Multiple errors occurred within the last 4 ticks, the machine at this block has been disabled.");
            Slimefun.logger()
                    .log(Level.SEVERE, "Please check the /plugins/Slimefun/error-reports/ folder for details.");
            Slimefun.logger()
                    .log(
                            Level.SEVERE,
                            "If you are asking someone for help, send them the error report file instead of a screenshot of this console");
            Slimefun.logger().log(Level.SEVERE, " ");
            bugs.remove(position);

            disableTicker(l);
        } else {
            bugs.put(position, errors);
        }
    }

    public void halt() {
        halted = true;
    }

    /**
     * BINARY COMPATIBILITY
     *
     * Use #getTickLocations instead
     *
     * @return A {@link Map} representation of all ticking {@link Location Locations}
     */
    @Nonnull
    public Map<ChunkPosition, Set<Location>> getLocations() {
        return tickingLocations.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(TickLocation::getLocation)
                                .collect(Collectors.toUnmodifiableSet())));
    }

    /**
     * This method returns a <strong>read-only</strong> {@link Map}
     * representation of every {@link ChunkPosition} and its corresponding
     * {@link Set} of ticking {@link Location Locations}.
     *
     * This does include any {@link Location} from an unloaded {@link Chunk} too!
     *
     * @return A {@link Map} representation of all ticking {@link TickLocation Locations}
     */
    @Nonnull
    public Map<ChunkPosition, Set<TickLocation>> getTickLocations() {
        return Collections.unmodifiableMap(tickingLocations);
    }

    /**
     * This method returns a <strong>read-only</strong> {@link Set}
     * of all ticking {@link Location Locations} in a given {@link Chunk}.
     * The {@link Chunk} does not have to be loaded.
     * If no {@link Location} is present, the returned {@link Set} will be empty.
     *
     * @param chunk
     *            The {@link Chunk}
     *
     * @return A {@link Set} of all ticking {@link Location Locations}
     */
    @Nonnull
    public Set<Location> getLocations(@Nonnull Chunk chunk) {
        Validate.notNull(chunk, "The Chunk cannot be null!");

        Set<TickLocation> locations = tickingLocations.getOrDefault(new ChunkPosition(chunk), Collections.emptySet());
        return locations.stream().map(TickLocation::getLocation).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns a <strong>read-only</strong> {@link Map}
     * representing, for each {@link ChunkPosition}, the {@link Location locations} of items with the {@link UniversalBlock} attribute
     * that are ticked.
     *
     * The contained {@link Location Locations} can be in loaded or unloaded {@link Chunk Chunks}
     *
     * @param chunk
     *            {@link Chunk}
     *
     * @return a read-only {@link Map} containing all ticked machine {@link TickLocation locations}
     */
    @Nonnull
    public Set<TickLocation> getTickLocations(@Nonnull Chunk chunk) {
        Validate.notNull(chunk, "The Chunk cannot be null!");

        return tickingLocations.getOrDefault(new ChunkPosition(chunk), Collections.emptySet());
    }

    /**
     * This enables the ticker at the given {@link Location} and adds it to our "queue".
     *
     * @param l
     *            The {@link Location} to activate
     */
    public void enableTicker(@Nonnull Location l) {
        enableTicker(l, null);
    }

    public void enableTicker(@Nonnull Location l, @Nullable UUID uuid) {
        Validate.notNull(l, "Location cannot be null!");

        synchronized (tickingLocations) {
            ChunkPosition chunk = new ChunkPosition(l.getWorld(), l.getBlockX() >> 4, l.getBlockZ() >> 4);
            final var tickPosition = uuid == null
                    ? new TickLocation(new BlockPosition(l))
                    : new TickLocation(new BlockPosition(l), uuid);

            /*
              Note that all the values in #tickingLocations must be thread-safe.
              Thus, the choice is between the CHM KeySet or a synchronized set.
              The CHM KeySet was chosen since it at least permits multiple concurrent
              reads without blocking.
            */
            Set<TickLocation> newValue = ConcurrentHashMap.newKeySet();
            Set<TickLocation> oldValue = tickingLocations.putIfAbsent(chunk, newValue);

            /**
             * This is faster than doing computeIfAbsent(...)
             * on a ConcurrentHashMap because it won't block the Thread for too long
             */
            if (oldValue != null) {
                oldValue.add(tickPosition);
            } else {
                newValue.add(tickPosition);
            }
        }
    }

    /**
     * This method disables the ticker at the given {@link Location} and removes it from our internal
     * "queue".
     *
     * @param l
     *            The {@link Location} to remove
     */
    public void disableTicker(@Nonnull Location l) {
        Validate.notNull(l, "Location cannot be null!");

        synchronized (tickingLocations) {
            ChunkPosition chunk = new ChunkPosition(l.getWorld(), l.getBlockX() >> 4, l.getBlockZ() >> 4);
            Set<TickLocation> locations = tickingLocations.get(chunk);

            if (locations != null) {
                locations.removeIf(tk -> l.equals(tk.getLocation()));

                if (locations.isEmpty()) {
                    tickingLocations.remove(chunk);
                }
            }
        }
    }

    /**
     * This method disables the ticker at the given {@link UUID} and removes it from our internal
     * "queue".
     *
     * DO NOT USE THIS until you cannot disable by location,
     * or enjoy extremely slow.
     *
     * @param uuid
     *            The {@link UUID} to remove
     */
    public void disableTicker(@Nonnull UUID uuid) {
        Validate.notNull(uuid, "Universal Data ID cannot be null!");

        synchronized (tickingLocations) {
            // Also clean up emptied sets
            var iterator = tickingLocations.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ChunkPosition, Set<TickLocation>> entry = iterator.next();
                entry.getValue().removeIf(tk -> uuid.equals(tk.getUuid()));

                if (entry.getValue().isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    public void showWaitingList() {
        showWaitingList(1);
    }

    public void showWaitingList(int page) {
        var builder = Component.text()
                .color(TextColor.color(0xFFD700))
                .append(Component.text("===== Ticker Waiting List ====="))
                .appendNewline();

        int j = 0;
        for (var entry :
                waiting.stream().skip((page - 1) * PAGE_SIZE).limit(PAGE_SIZE).toList()) {
            int number = (page - 1) * PAGE_SIZE + j + 1;
            String head = number + ". " + entry.item.getItemName() + " ";
            builder.color(TextColor.color(0x00B7B7))
                    .append(Component.text(head)
                            .hoverEvent(Component.text("Click to step over")
                                    .clickEvent(ClickEvent.callback(p2 -> stepTo(entry, true)))))
                    .append(Component.text(" ".repeat(Math.max(0, 12 - head.length()))))
                    .append(Component.text("[Run to] ")
                            .hoverEvent(Component.text("Click to run until here and stop"))
                            .clickEvent(ClickEvent.callback(p2 -> stepTo(entry, false))))
                    .append(Component.text("[Step over] ")
                            .hoverEvent(Component.text("Click to step over"))
                            .clickEvent(ClickEvent.callback(p2 -> stepTo(entry, true))))
                    .append(Component.text("[Highlight] ")
                            .hoverEvent(Component.text("Click to highlight the block"))
                            .clickEvent(ClickEvent.callback(p2 -> {
                                if (p2 instanceof Player p) {
                                    if (p.getLocation().getWorld()
                                            == entry.getLocation().getWorld()) {
                                        ParticleUtil.highlightBlock(p, entry.getLocation(), 3);
                                    } else {
                                        Slimefun.getLocalization().sendMessage(p, "messages.wrong-world");
                                    }
                                }
                            })))
                    .appendNewline();
            j++;
        }
        int totalPage = (waiting.size() - 1) / PAGE_SIZE + 1;
        builder.color(TextColor.color(0xFFD700))
                .append(Component.text()
                        .append(Component.text("=== Previous page < ")
                                .hoverEvent(Component.text("Click to go to the previous page (" + (page - 1) + ")"))
                                .clickEvent(ClickEvent.callback(p2 -> {
                                    if (page - 1 < 1) {
                                        return;
                                    }
                                    showWaitingList(page - 1);
                                })))
                        .append(Component.text(page + " / " + totalPage)
                                .append(Component.text(" > Next page ===")
                                        .hoverEvent(Component.text("Click to go to the next page (" + (page + 1) + ")"))
                                        .clickEvent(ClickEvent.callback(p2 -> {
                                            if (page + 1 > totalPage) {
                                                return;
                                            }
                                            showWaitingList(page + 1);
                                        })))));

        Component text = builder.build();
        Bukkit.getServer().getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            player.sendMessage(text);
        });
    }
}
