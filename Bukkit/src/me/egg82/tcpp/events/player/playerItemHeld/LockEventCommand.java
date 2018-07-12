package me.egg82.tcpp.events.player.playerItemHeld;

import java.util.UUID;

import org.bukkit.event.player.PlayerItemHeldEvent;

import me.egg82.tcpp.registries.LockRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.handlers.events.EventHandler;

public class LockEventCommand extends EventHandler<PlayerItemHeldEvent> {
	//vars
	private IVariableRegistry<UUID> lockRegistry = ServiceLocator.getService(LockRegistry.class);
	
	//constructor
	public LockEventCommand() {
		super();
	}
	
	//public

	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		if (lockRegistry.hasRegister(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
}
