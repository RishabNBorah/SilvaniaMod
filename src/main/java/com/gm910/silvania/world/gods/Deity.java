package com.gm910.silvania.world.gods;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public class Deity {

	private Entity avatar;
	private GodData data;
	private String name;

	public Deity(GodData data, String name) {
		this.data = data;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Deity(GodData data, CompoundNBT nbt) {
		this(data, nbt.getString("Name"));
	}

	public CompoundNBT serialize() {
		CompoundNBT nbt = new CompoundNBT();

		return nbt;
	}

	public Entity getAvatar() {
		return avatar;
	}

	public GodData getData() {
		return data;
	}

	public void setAvatar(Entity avatar) {
		this.avatar = avatar;
	}
}
