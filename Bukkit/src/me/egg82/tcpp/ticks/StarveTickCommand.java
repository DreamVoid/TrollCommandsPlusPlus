package me.egg82.tcpp.ticks;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.egg82.tcpp.registries.StarveRegistry;
import ninja.egg82.bukkit.handlers.TickHandler;
import ninja.egg82.bukkit.utils.CommandUtil;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;

public class StarveTickCommand extends TickHandler {
	//vars
	private IVariableRegistry<UUID> starveRegistry = ServiceLocator.getService(StarveRegistry.class);
	
	//constructor
	public StarveTickCommand() {
		super(0L, 15L);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		for (UUID key : starveRegistry.getKeys()) {
			e(CommandUtil.getPlayerByUuid(key));
		}
	}
	private void e(Player player) {
		if (player == null) {
			return;
		}
		
		player.setFoodLevel(player.getFoodLevel() - 1);
	}
}
