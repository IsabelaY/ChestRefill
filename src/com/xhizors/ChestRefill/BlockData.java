package com.xhizors.ChestRefill;

import org.bukkit.block.Block;

public class BlockData {
	
	private int x, y, z;
	private String world;
	
	public BlockData(String worldName, int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		world = worldName;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public String getWorldName() {
		return world;
	}
	
	public static BlockData getBlockData(Block b) {
		return new BlockData(b.getWorld().getName(), b.getX(), b.getY(), b.getZ());
	}
	
}