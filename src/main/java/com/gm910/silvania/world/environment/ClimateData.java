package com.gm910.silvania.world.environment;

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

public class ClimateData extends WorldSavedData {

	public static final String NAME = SilvaniaMod.MODID + "_climate";

	private Map<DimensionType, Map<ChunkPos, Climate>> CLIMATES = new NonNullMap<>(Maps::newHashMap);

	private MinecraftServer server;

	private Map<IChunk, Set<Runnable>> deferredWork = new NonNullMap<>(HashSet::new);

	public ClimateData(String name) {
		super(name);

	}

	public ClimateData() {
		this(NAME);
	}

	public MinecraftServer getServer() {
		return server;
	}

	public void createClimate(IChunk chunk) {

		Climate data = new Climate(this, chunk);

		data.create();

		this.CLIMATES.get(chunk.getWorldForge().getDimension().getType()).put(data.getChunk().getPos(), data);
	}

	@SubscribeEvent
	public void chunkload(ChunkEvent.Load event) {

		Set<Runnable> runs = deferredWork.get(event.getChunk());
		for (Runnable run : runs) {
			run.run();
		}
	}

	public synchronized Climate getFromPos(ServerPos pos) {
		IChunk chunk = pos.getWorld(server).getChunk(pos);
		try {
			wait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return this.CLIMATES.get(pos.getWorld(server).dimension.getType()).get(new ChunkPos(pos));
	}

	@SubscribeEvent
	public void chunkload(ChunkDataEvent.Load event) {

		CompoundNBT nbt = event.getData();
		if (!nbt.contains("Climate")) {

			this.deferredWork.get(event.getChunk()).add(() -> this.createClimate(event.getChunk()));
		}
		CompoundNBT tag = nbt.getCompound("Climate");
		ChunkPos cpos = new ChunkPos(tag.getLong("Pos"));
		Climate data = new Climate(this, event.getChunk(), tag.getCompound("Data"));
		this.CLIMATES.get(event.getWorld().getDimension().getType()).putIfAbsent(cpos, data);
		notify();
	}

	@SubscribeEvent
	public void chunksave(ChunkDataEvent.Save event) {
		CompoundNBT nbt = event.getData();
		CompoundNBT tag = new CompoundNBT();
		nbt.put("Climate", tag);
		DimensionType type = event.getWorld().getDimension().getType();
		IChunk chunk = event.getChunk();
		ChunkPos cpos = chunk.getPos();
		if (this.CLIMATES.get(type).get(cpos) != null) {
			tag.putLong("Pos", cpos.asLong());
			tag.put("Data", this.CLIMATES.get(type).get(cpos).writeToNBT());
		}
	}

	@Override
	public void read(CompoundNBT nbt) {

	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT nbt = new CompoundNBT();

		return nbt;
	}

	public static ClimateData get(MinecraftServer server) {
		DimensionSavedDataManager dimdat = server.getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			ClimateData dat = new ClimateData();
			dat.server = server;
			MinecraftForge.EVENT_BUS.register(dat);
			return dat;
		}, NAME);
	}

}
