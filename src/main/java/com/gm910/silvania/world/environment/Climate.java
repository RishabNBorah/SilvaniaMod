package com.gm910.silvania.world.environment;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.IChunk;

@SuppressWarnings("deprecation")
public class Climate {

	private float overallTemperature;
	private Float[][][] temperatureGrid = new Float[16][256][16];
	private IChunk chunk;
	private ClimateData data;

	public Climate(ClimateData data, IChunk chunk) {

		this.chunk = chunk;
		this.data = data;
	}

	public void create() {
		// TODO
		ChunkPos cpos = chunk.getPos();
		Map<Biome, Integer> biomeFrequencies = new HashMap<>();
		BiomeContainer biomes = chunk.getBiomes();
		for (int bio : biomes.getBiomeIds()) {
			Biome biome = Registry.BIOME.getByValue(bio);
			biomeFrequencies.put(biome, biomeFrequencies.getOrDefault(biomes, 0) + 1);
		}

		float temperature = 0;
		int count = 0;
		for (Biome biome : biomeFrequencies.keySet()) {
			count += biomeFrequencies.get(biome);
			temperature += biome.getDefaultTemperature() * biomeFrequencies.get(biome);
		}
		temperature /= count;
		this.overallTemperature = temperature;
		this.update();
	}

	public IChunk getChunk() {
		return chunk;
	}

	public ChunkPos getPos() {
		return getChunk().getPos();
	}

	public void update() {
		// TODO
		for (int x = getPos().getXStart(); x <= getPos().getXEnd(); x++) {
			for (int y = 0; y < 256; y++) {
				for (int z = getPos().getZStart(); z <= getPos().getZEnd(); z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = chunk.getWorldForge().getBlockState(pos);

				}
			}
		}
	}

	public void setTemperatureGrid(Float[][][] temperatureGrid) {
		this.temperatureGrid = temperatureGrid;
	}

	public float getOverallTemperature() {
		return overallTemperature;
	}

	public Float[][][] getTemperatureGrid() {
		return temperatureGrid;
	}

	public float getTemperature(BlockPos pos) {
		Float temp = temperatureGrid[pos.getX()][pos.getY()][pos.getZ()];
		return temp == null ? overallTemperature : temp.floatValue();
	}

	public void setTemperature(BlockPos pos, float temp) {
		temperatureGrid[pos.getX()][pos.getY()][pos.getZ()] = temp;
	}

	public void setTemperatureToDefault(BlockPos pos) {
		temperatureGrid[pos.getX()][pos.getY()][pos.getZ()] = this.overallTemperature;
	}

	public Climate(ClimateData data, IChunk chunk, CompoundNBT nbt) {
		this(data, chunk);
	}

	public CompoundNBT writeToNBT() {
		CompoundNBT nbt = new CompoundNBT();
		return nbt;
	}
}
