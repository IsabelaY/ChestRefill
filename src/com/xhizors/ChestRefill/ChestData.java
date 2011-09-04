package com.xhizors.ChestRefill;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ChestData {
	private ConcurrentHashMap<Integer, ItemStack> inv;
	private long delay;
	private long next;
	
	public ChestData() {
		inv = new ConcurrentHashMap<Integer, ItemStack>();
	}
	
	public void addItem(int index, int type, int amount, short damage) {
		inv.put(new Integer(index), new ItemStack(type, amount, damage));
	}
	
	public void setDelay(long time) {
		this.delay = time;
	}
	
	public void setNextRefill(long time) {
		this.next = time;
	}
	
	public void nextRefill() {
		next = System.currentTimeMillis() + delay;
	}
	
	public long getNextRefill() {
		return next;
	}
	
	public boolean canRefill() {
		if (System.currentTimeMillis() >= next) {
			return true;
		}
		return false;
	}
	
	public static BlockData createBlockData(Block b) {
		return new BlockData(b.getWorld().getName(), b.getX(), b.getY(), b.getZ());
	}
	
	public long getDelay() {
		return delay;
	}
	
	public ConcurrentHashMap<Integer, ItemStack> getMap() {
		return inv;
	}
}
