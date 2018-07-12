package me.egg82.tcpp.events.player.playerDropItem;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.egg82.tcpp.registries.RandomDropRegistry;
import ninja.egg82.filters.EnumFilter;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.handlers.events.EventHandler;
import ninja.egg82.utils.MathUtil;

public class RandomDropEventCommand extends EventHandler<PlayerDropItemEvent> {
	//vars
	private IVariableRegistry<UUID> randomDropRegistry = ServiceLocator.getService(RandomDropRegistry.class);
	
	private Material[] materials = null;
	
	//constructor
	public RandomDropEventCommand() {
		super();
		
		EnumFilter<Material> materialFilterHelper = new EnumFilter<Material>(Material.class);
		materials = materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
			materialFilterHelper.filter(
				materialFilterHelper.getAllTypes(),
			"_block", false),
			"barrier", false),
			"air", false),
			"stationary_", false),
			"piston_", false),
			"mob_spawner", false),
			"torch_on", false),
			"command_", false),
			"sponge", false),
			"bedrock", false);
	}
	
	//public

	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		if (randomDropRegistry.hasRegister(event.getPlayer().getUniqueId())) {
			event.getItemDrop().setItemStack(new ItemStack(materials[MathUtil.fairRoundedRandom(0, materials.length - 1)], event.getItemDrop().getItemStack().getAmount()));
		}
	}
}
