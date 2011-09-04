package com.xhizors.ChestRefill;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class ChestRefillBlockListener extends BlockListener {
	
	private ChestRefill instance;
	
	public ChestRefillBlockListener(ChestRefill instance) {
		this.instance = instance;
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player p = event.getPlayer();
		BlockData key = instance.findRepeatKey(new BlockData(block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
		if (instance.getChestMap().containsKey(key)) {
			if (instance.hasPermission(p, "chestrefill.admin")) {
				instance.getChestMap().remove(key);
				instance.saveFile();
				p.sendMessage("Chest refill removed.");
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getItemInHand().getTypeId() == 54) {
			Block chest = getRelativeChest(event.getBlockPlaced());
			if (chest != null) {
				BlockData data = instance.findRepeatKey(ChestData.createBlockData(chest));
				if (instance.getChestMap().containsKey(data)) {
					event.setCancelled(true);
					event.getPlayer().sendMessage("You can't place a chest near a refill chest.");
				}
			}
		}
	}
	
	public Block getRelativeChest(Block b) {
		if (b.getRelative(BlockFace.EAST).getTypeId() == 54) return b.getRelative(BlockFace.EAST);
		if (b.getRelative(BlockFace.WEST).getTypeId() == 54) return b.getRelative(BlockFace.WEST);
		if (b.getRelative(BlockFace.NORTH).getTypeId() == 54) return b.getRelative(BlockFace.NORTH);
		if (b.getRelative(BlockFace.SOUTH).getTypeId() == 54) return b.getRelative(BlockFace.SOUTH);
		return null;
	}

}