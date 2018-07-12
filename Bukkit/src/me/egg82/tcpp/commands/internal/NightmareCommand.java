package me.egg82.tcpp.commands.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.registries.NightmareRegistry;
import me.egg82.tcpp.util.MetricsHelper;
import ninja.egg82.bukkit.BasePlugin;
import ninja.egg82.bukkit.utils.BlockUtil;
import ninja.egg82.bukkit.utils.CommandUtil;
import ninja.egg82.bukkit.utils.LocationUtil;
import ninja.egg82.concurrent.FixedConcurrentDeque;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.handlers.CommandHandler;
import ninja.egg82.protocol.core.IFakeLivingEntity;
import ninja.egg82.protocol.reflection.IFakeEntityHelper;
import ninja.egg82.utils.MathUtil;
import ninja.egg82.utils.ThreadUtil;

public class NightmareCommand extends CommandHandler {
	//vars
	private IVariableRegistry<UUID> nightmareRegistry = ServiceLocator.getService(NightmareRegistry.class);
	
	private MetricsHelper metricsHelper = ServiceLocator.getService(MetricsHelper.class);
	private IFakeEntityHelper fakeEntityHelper = ServiceLocator.getService(IFakeEntityHelper.class);
	
	private String gameVersion = ServiceLocator.getService(BasePlugin.class).getGameVersion();
	
	//constructor
	public NightmareCommand() {
		super();
	}
	
	//public
	public List<String> tabComplete() {
		if (args.length == 1) {
			ArrayList<String> retVal = new ArrayList<String>();
			
			if (args[0].isEmpty()) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					retVal.add(player.getName());
				}
			} else {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
						retVal.add(player.getName());
					}
				}
			}
			
			return retVal;
		}
		
		return null;
	}
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (!sender.hasPermission(PermissionsType.COMMAND_NIGHTMARE)) {
			sender.sendMessage(ChatColor.RED + "You do not have permissions to run this command!");
			return;
		}
		if (!CommandUtil.isArrayOfAllowedLength(args, 1)) {
			sender.sendMessage(ChatColor.RED + "Incorrect command usage!");
			String name = getClass().getSimpleName();
			name = name.substring(0, name.length() - 7).toLowerCase();
			Bukkit.getServer().dispatchCommand((CommandSender) sender.getHandle(), "troll help " + name);
			return;
		}
		if (!fakeEntityHelper.isValidLibrary()) {
			sender.sendMessage(ChatColor.RED + "This command has been disabled because there is no recognized backing library available. Please install one and restart the server to enable this command.");
			return;
		}
		if (gameVersion.equals("1.8") || gameVersion.equals("1.8.1") || gameVersion.equals("1.8.3") || gameVersion.equals("1.8.8")) {
			sender.sendMessage(ChatColor.RED + "This command has been disabled because this version of Minecraft doesn't support it.");
			return;
		}
		
		List<Player> players = CommandUtil.getPlayers(CommandUtil.parseAtSymbol(args[0], CommandUtil.isPlayer((CommandSender) sender.getHandle()) ? ((Player) sender.getHandle()).getLocation() : null));
		if (players.size() > 0) {
			for (Player player : players) {
				UUID uuid = player.getUniqueId();
				
				if (!nightmareRegistry.hasRegister(uuid)) {
					if (player.hasPermission(PermissionsType.IMMUNE)) {
						continue;
					}
					
					e(uuid, player);
				} else {
					eUndo(uuid, player);
				}
			}
		} else {
			Player player = CommandUtil.getPlayerByName(args[0]);
			
			if (player == null) {
				OfflinePlayer offlinePlayer = CommandUtil.getOfflinePlayerByName(args[0]);
				if (offlinePlayer != null) {
					UUID uuid = offlinePlayer.getUniqueId();
					if (nightmareRegistry.hasRegister(uuid)) {
						eUndo(uuid, offlinePlayer);
						return;
					}
				}
				
				sender.sendMessage(ChatColor.RED + "Player could not be found.");
				return;
			}
			
			UUID uuid = player.getUniqueId();
			
			if (!nightmareRegistry.hasRegister(uuid)) {
				if (player.hasPermission(PermissionsType.IMMUNE)) {
					sender.sendMessage(ChatColor.RED + "Player is immune.");
					return;
				}
				
				e(uuid, player);
			} else {
				eUndo(uuid, player);
			}
		}
	}
	private void e(UUID uuid, Player player) {
		Location[] zombieLocs = LocationUtil.getCircleAround(player.getLocation(), 3, MathUtil.fairRoundedRandom(6, 9));
		Location[] zombie2Locs = LocationUtil.getCircleAround(player.getLocation(), 5, MathUtil.fairRoundedRandom(8, 12));
		
		IConcurrentDeque<IFakeLivingEntity> entities = new FixedConcurrentDeque<IFakeLivingEntity>(zombieLocs.length + zombie2Locs.length);
		
		ThreadUtil.submit(new Runnable() {
			public void run() {
				for (int i = 0; i < zombieLocs.length; i++) {
					IFakeLivingEntity e = fakeEntityHelper.createEntity(zombieLocs[i], EntityType.ZOMBIE);
					e.addPlayer(player);
					Vector v = player.getLocation().toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.23);
					if (LocationUtil.isFinite(v)) {
						e.moveTo(BlockUtil.getHighestSolidBlock(e.getLocation().add(v)).add(0.0d, 1.0d, 0.0d));
					}
					e.lookTo(player.getEyeLocation());
					entities.add(e);
				}
				for (int i = 0; i < zombie2Locs.length; i++) {
					IFakeLivingEntity e = fakeEntityHelper.createEntity(zombie2Locs[i], EntityType.ZOMBIE);
					e.addPlayer(player);
					Vector v = player.getLocation().toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.23);
					if (LocationUtil.isFinite(v)) {
						e.moveTo(BlockUtil.getHighestSolidBlock(e.getLocation().add(v)).add(0.0d, 1.0d, 0.0d));
					}
					e.lookTo(player.getEyeLocation());
					entities.add(e);
				}
			}
		});
		
		nightmareRegistry.setRegister(uuid, entities);
		
		metricsHelper.commandWasRun(this);
		
		sender.sendMessage(player.getName() + " is now living in a nightmare!");
	}
	
	protected void onUndo() {
		Player player = CommandUtil.getPlayerByName(args[0]);
		if (player != null) {
			UUID uuid = player.getUniqueId();
			if (nightmareRegistry.hasRegister(uuid)) {
				eUndo(uuid, player);
			}
		} else {
			OfflinePlayer offlinePlayer = CommandUtil.getOfflinePlayerByName(args[0]);
			UUID uuid = offlinePlayer.getUniqueId();
			if (nightmareRegistry.hasRegister(uuid)) {
				eUndo(uuid, offlinePlayer);
			}
		}
	}
	@SuppressWarnings("unchecked")
	private void eUndo(UUID uuid, Player player) {
		IConcurrentDeque<IFakeLivingEntity> entities = nightmareRegistry.getRegister(uuid, IConcurrentDeque.class);
		
		ThreadUtil.submit(new Runnable() {
			public void run() {
				for (IFakeLivingEntity e : entities) {
					e.destroy();
				}
			}
		});
		
		nightmareRegistry.removeRegister(uuid);
		
		sender.sendMessage(player.getName() + " is no longer living in a nightmare.");
	}
	@SuppressWarnings("unchecked")
	private void eUndo(UUID uuid, OfflinePlayer player) {
		IConcurrentDeque<IFakeLivingEntity> entities = nightmareRegistry.getRegister(uuid, IConcurrentDeque.class);
		
		ThreadUtil.submit(new Runnable() {
			public void run() {
				for (IFakeLivingEntity e : entities) {
					e.destroy();
				}
			}
		});
		
		nightmareRegistry.removeRegister(uuid);
		
		sender.sendMessage(player.getName() + " is no longer living in a nightmare.");
	}
}
