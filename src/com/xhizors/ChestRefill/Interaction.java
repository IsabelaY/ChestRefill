package com.xhizors.ChestRefill;

public abstract class Interaction {
	private String name;
	
	public Interaction(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
