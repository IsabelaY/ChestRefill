package com.xhizors.ChestRefill;

public class AddChestRefill extends Interaction {
	private long delay;
	
	public AddChestRefill(long delay) {
		super("addchestrefill");
		this.delay = delay;
	}
	
	public long getDelay() {
		return delay;
	}
	
}
