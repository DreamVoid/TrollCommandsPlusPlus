package me.egg82.tcpp.events.player.playerMove;

import java.util.UUID;

import org.bukkit.event.player.PlayerMoveEvent;

import me.egg82.tcpp.services.registries.FreezeRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;

public class FreezeEventCommand extends EventCommand<PlayerMoveEvent> {
	//vars
	private IVariableRegistry<UUID> freezeRegistry = ServiceLocator.getService(FreezeRegistry.class);
	
	//constructor
	public FreezeEventCommand() {
		super();
	}
	
	//public

	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		if (freezeRegistry.hasRegister(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
}
