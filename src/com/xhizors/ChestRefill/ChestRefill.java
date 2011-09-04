package com.xhizors.ChestRefill;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class ChestRefill extends JavaPlugin {
	
	private PermissionHandler permissionHandler = null;
	public boolean friendlyfire = false;
	public static final Logger log = Logger.getLogger("Minecraft");
	public static String name;
	public ChestRefillReader reader;
	private PluginManager pm;
	private ChestRefillBlockListener blockListener;
	private ChestRefillPlayerListener playerListener;
	private ConcurrentHashMap<BlockData, ChestData> chestMap;
	private ConcurrentHashMap<Player, Interaction> nextInteraction;

	@Override
	public void onDisable() {
		log.info("[" + name + "] is now disabled.");
	}

	@Override
	public void onEnable() {
		pm = getServer().getPluginManager();
		PluginDescriptionFile pdf = this.getDescription();
		blockListener = new ChestRefillBlockListener(this);
		playerListener = new ChestRefillPlayerListener(this);
		nextInteraction = new ConcurrentHashMap<Player, Interaction>();
		name = pdf.getName();
		reader = new ChestRefillReader(this);
		chestMap = reader.readChestFile();
		setupPermissions();
		log.info("[" + ChestRefill.name + "] Loaded.");
		
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			log.info("Use commands ingame!");
			return false;
		}
		
		if (command.getName().equalsIgnoreCase("addchestrefill")) {
			if (hasPermission(sender, "chestrefill.admin")) {
				long time;
				try {
					if (args.length >= 1) {
						time = Long.parseLong(args[0]);
					} else {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e) {
					sender.sendMessage("Delay not found.");
					return false;
				}
				sender.sendMessage("Click on a chest to create a new refill chest");
				Player player = (Player) sender;
				nextInteraction.put(player, new AddChestRefill(time));
			}
			return true;
		}

		return false;
	}

	private void setupPermissions() {
		if (permissionHandler != null) {
			return;
		}

		Plugin permissionsPlugin = this.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (permissionsPlugin == null) {
			log.warning("[" + name
					+ "] Permissions System not found. Disabling plugin.");
			setEnabled(false);
			return;
		}

		permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	}

	public PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}
	
	public ConcurrentHashMap<BlockData, ChestData> getChestMap() {
		return chestMap;
	}
	
	public Interaction checkInteraction(Player p) {
		Interaction i = nextInteraction.get(p);
		nextInteraction.remove(p);
		return i;
	}
	
	public BlockData findRepeatKey(BlockData b) {
		BlockData key = b;
		for (BlockData k : chestMap.keySet()) {
			if (k.getWorldName().equals(b.getWorldName()) &&
					k.getX() == b.getX() &&
					k.getY() == b.getY() &&
					k.getZ() == b.getZ()) {
				key = k;
				break;
			}
		}
		return key;
	}
	
	public boolean hasPermission(CommandSender sender, String permission) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!permissionHandler.has(player, permission)) {
				sender.sendMessage("You do not have permission for this.");
				return false;
			}
		}
		return true;
	}
	
	public void saveFile() {
		reader.saveFile();
	}

}