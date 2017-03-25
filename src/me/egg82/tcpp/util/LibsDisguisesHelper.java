package me.egg82.tcpp.util;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.egg82.tcpp.util.interfaces.IDisguiseHelper;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;

public class LibsDisguisesHelper implements IDisguiseHelper {
	//vars
	
	//constructor
	public LibsDisguisesHelper() {
		
	}
	
	//public
	public void disguiseAsPlayer(Player player, Player disguise) {
		DisguiseAPI.disguiseToAll(player, new PlayerDisguise(disguise).setViewSelfDisguise(false));
	}
	public void disguiseAsEntity(Player player, EntityType disguise) {
		DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.getType(disguise)).setViewSelfDisguise(false));
	}
	public void undisguise(Player player) {
		DisguiseAPI.undisguiseToAll(player);
	}
	public EntityType disguiseType(Player player) {
		return (DisguiseAPI.isDisguised(player)) ? EntityType.valueOf(DisguiseAPI.getDisguise(player).getType().toString()) : null;
	}
	public boolean isValidLibrary() {
		return true;
	}
	
	//private
	
}