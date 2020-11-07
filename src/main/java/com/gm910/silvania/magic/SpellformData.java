package com.gm910.silvania.magic;

import java.util.HashMap;
import java.util.Map;

import com.gm910.silvania.SilvaniaMod;
import com.gm910.silvania.api.util.ServerPos;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class SpellformData extends WorldSavedData {

	public static final String NAME = SilvaniaMod.MODID + "_spells";

	public final Map<ServerPos, Spellform> SPELLFORMS = new HashMap<>();

	private MinecraftServer server;

	// public static final ResourceLocation USIFIA_RL = new
	// ResourceLocation(Elkloria.MODID, "usifia");

	public SpellformData(String name) {
		super(name);

	}

	public SpellformData() {
		this(NAME);
	}

	public MinecraftServer getServer() {
		return server;
	}

	public Spellform addSpellform(Spellform spell, boolean replace) {
		Spellform form = this.SPELLFORMS.get(spell.getCenter());
		if (form == null) {
			this.SPELLFORMS.put(spell.getCenter(), spell);
			return null;
		} else {
			if (replace) {
				return this.SPELLFORMS.put(spell.getCenter(), spell);
			} else {
				return spell;
			}
		}
	}

	public Spellform removeSpellform(ServerPos pos) {
		return SPELLFORMS.remove(pos);
	}

	public Spellform removeSpellform(Spellform spell) {
		return SPELLFORMS.remove(spell.getCenter());
	}

	public boolean existsInWorld(Spellform spell) {
		return SPELLFORMS.containsValue(spell);
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.SPELLFORMS.clear();
		ListNBT ls = nbt.getList("Spellforms", NBT.TAG_COMPOUND);
		for (INBT fn : ls) {
			CompoundNBT tag = (CompoundNBT) fn;
			Spellform spell = new Spellform(this);
			spell.deserializeNBT(tag);
			this.SPELLFORMS.put(spell.getCenter(), spell);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT nbt = new CompoundNBT();
		ListNBT dimlist = new ListNBT();
		for (ServerPos pos : SPELLFORMS.keySet()) {
			dimlist.add(SPELLFORMS.get(pos).serializeNBT());
		}

		nbt.put("Spellforms", dimlist);

		return nbt;
	}

	public static SpellformData get(MinecraftServer server) {
		DimensionSavedDataManager dimdat = server.getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			SpellformData dat = new SpellformData();
			dat.server = server;
			return dat;
		}, NAME);
	}

}
