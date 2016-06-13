package me.egg82.tcpp.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.FlowerPot;
import org.bukkit.block.Jukebox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.egg82.tcpp.commands.base.BasePluginCommand;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.enums.PluginServiceType;
import me.egg82.tcpp.ticks.PortalTickCommand;
import me.egg82.tcpp.util.BlockDataUtil;
import ninja.egg82.events.patterns.command.CommandEvent;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.enums.CustomServiceType;
import ninja.egg82.plugin.utils.interfaces.ITickHandler;
import ninja.egg82.registry.interfaces.IRegistry;

public class PortalCommand extends BasePluginCommand {
	//vars
	IRegistry reg = (IRegistry) ServiceLocator.getService(PluginServiceType.PORTAL_REGISTRY);
	ITickHandler tickHandler = (ITickHandler) ServiceLocator.getService(CustomServiceType.TICK_HANDLER);
	
	//constructor
	public PortalCommand(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	//public
	
	//private
	protected void execute() {
		if (isValid(false, PermissionsType.COMMAND_PORTAL, new int[]{1}, new int[]{0})) {
			if (args.length == 1) {
				e(Bukkit.getPlayer(args[0]));
			}
			
			dispatch(CommandEvent.COMPLETE, null);
		}
	}
	private void e(Player player) {
		ArrayList<Material[]> blocks = new ArrayList<Material[]>();
		ArrayList<ArrayList<ItemStack[]>> inv = new ArrayList<ArrayList<ItemStack[]>>();
		ArrayList<BlockState[]> data = new ArrayList<BlockState[]>();
		Location loc = player.getLocation();
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				inv.add(getBlockInventory(loc.clone().add(i - 1.0d, 0.0d, j - 1.0d)));
				data.add(getBlockState(loc.clone().add(i - 1.0d, 0.0d, j - 1.0d)));
				blocks.add(removeBlocks(loc.clone().add(i - 1.0d, 0.0d, j - 1.0d)));
			}
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("time", System.currentTimeMillis());
		map.put("loc", loc);
		map.put("blocks", blocks);
		map.put("inv", inv);
		map.put("data",  data);
		reg.setRegister(player.getName().toLowerCase(), map);
		tickHandler.addDelayedTickCommand("portal-" + player.getName().toLowerCase(), PortalTickCommand.class, 102);
		
		sender.sendMessage(player.getName() + " is now falling to The(ir) End.");
	}
	
	private ArrayList<ItemStack[]> getBlockInventory(Location l) {
		ArrayList<ItemStack[]> s = new ArrayList<ItemStack[]>();
		BlockState state = null;
		Material type = null;
		
		do {
			state = l.getBlock().getState();
			type = state.getType();
			if (state instanceof InventoryHolder) {
				InventoryHolder holder = (InventoryHolder) state;
				s.add(holder.getInventory().getContents());
			} else if (type == Material.FLOWER_POT) {
				MaterialData contents = ((FlowerPot) state).getContents();
				if (contents != null) {
					s.add(new ItemStack[]{contents.toItemStack()});
				} else {
					s.add(null);
				}
			} else if (type == Material.JUKEBOX) {
				Material playing = ((Jukebox) state).getPlaying();
				if (playing != null) {
					s.add(new ItemStack[]{new ItemStack(playing, 1)});
				} else {
					s.add(null);
				}
			} else {
				s.add(null);
			}
		} while (l.subtract(0.0d, 1.0d, 0.0d).getBlockY() >= 0);
		
		return s;
	}
	private BlockState[] getBlockState(Location l) {
		BlockState[] d = new BlockState[l.getBlockY() + 1];
		int i = 0;
		
		do {
			d[i] = l.getBlock().getState();
			i++;
		} while (l.subtract(0.0d, 1.0d, 0.0d).getBlockY() >= 0);
		
		return d;
	}
	private Material[] removeBlocks(Location l) {
		Material[] b = new Material[6];
		int i = 0;
		Block block = null;
		int endY = l.getBlockY() - 5;
		
		do {
			block = l.getBlock();
			b[i] = block.getType();
			BlockDataUtil.clearBlockInventory(block.getState());
			block.setType(Material.AIR);
			i++;
		} while (l.subtract(0.0d, 1.0d, 0.0d).getBlockY() >= endY);
		
		l.add(0.0d, 1.0d, 0.0d);
		l.getBlock().setType(Material.ENDER_PORTAL);
		
		return b;
	}
}
