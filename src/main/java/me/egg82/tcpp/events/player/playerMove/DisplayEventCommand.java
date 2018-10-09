package me.egg82.tcpp.events.player.playerMove;

import java.util.UUID;

import me.egg82.tcpp.registries.DisplayLocationRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.handlers.events.EventHandler;

public class DisplayEventCommand extends EventHandler<PlayerMoveEvent> {
    //vars
    private IVariableRegistry<UUID> displayLocationRegistry = ServiceLocator.getService(DisplayLocationRegistry.class);

    //constructor
    public DisplayEventCommand() {
        super();
    }

    //public

    //private
    protected void onExecute(long elapsedMilliseconds) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (displayLocationRegistry.hasRegister(uuid)) {
            Location loc = displayLocationRegistry.getRegister(uuid, Location.class);
            if (!event.getTo().getWorld().equals(loc.getWorld())) {
                event.setCancelled(true);
                player.teleport(loc);
            } else {
                if (event.getTo().distanceSquared(loc) >= 4) {
                    event.setCancelled(true);
                    player.teleport(loc);
                }
            }
        }
    }
}
