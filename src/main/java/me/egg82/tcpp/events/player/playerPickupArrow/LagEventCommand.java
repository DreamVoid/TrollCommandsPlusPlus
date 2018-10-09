package me.egg82.tcpp.events.player.playerPickupArrow;

import java.util.UUID;

import me.egg82.tcpp.registries.LagItemRegistry;
import me.egg82.tcpp.registries.LagRegistry;
import org.bukkit.event.player.PlayerPickupArrowEvent;

import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.handlers.events.EventHandler;
import ninja.egg82.utils.MathUtil;

public class LagEventCommand extends EventHandler<PlayerPickupArrowEvent> {
    //vars
    private IVariableRegistry<UUID> lagRegistry = ServiceLocator.getService(LagRegistry.class);
    private IVariableRegistry<UUID> lagItemRegistry = ServiceLocator.getService(LagItemRegistry.class);

    //constructor
    public LagEventCommand() {
        super();
    }

    //public

    //private
    protected void onExecute(long elapsedMilliseconds) {
        if (event.isCancelled()) {
            return;
        }

        if (!lagRegistry.hasRegister(event.getPlayer().getUniqueId())) {
            return;
        }

        UUID uuid = event.getItem().getUniqueId();

        Long pickupTime = lagItemRegistry.getRegister(uuid, Long.class);

        if (pickupTime == null) {
            event.setCancelled(true);
            lagItemRegistry.setRegister(uuid, System.currentTimeMillis() + MathUtil.fairRoundedRandom(1500, 2500));
        } else if (System.currentTimeMillis() < pickupTime) {
            event.setCancelled(true);
        } else {
            lagItemRegistry.removeRegister(uuid);
        }
    }
}