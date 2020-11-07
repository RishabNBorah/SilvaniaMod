package com.gm910.silvania.world.gods;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gm910.silvania.SilvaniaMod;
import com.gm910.silvania.api.util.NonNullMap;
import com.gm910.silvania.api.util.ServerPos;
import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GodData extends WorldSavedData {

	public static final String NAME = SilvaniaMod.MODID + "_gods";


	private MinecraftServer server;


	public GodData(String name) {
		super(name);

	}

	public GodData() {
		this(NAME);
	}

	public MinecraftServer getServer() {
		return server;
	}


	@Override
	public void read(CompoundNBT nbt) {

	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT nbt = new CompoundNBT();

		return nbt;
	}

	public static GodData get(MinecraftServer server) {
		DimensionSavedDataManager dimdat = server.getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			GodData dat = new GodData();
			dat.server = server;
			MinecraftForge.EVENT_BUS.register(dat);
			return dat;
		}, NAME);
	}

}
