package com.gm910.silvania.magic.spellparts;

import java.util.List;
import java.util.Map;

import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.magic.SpellException;
import com.gm910.silvania.magic.SpellPart;
import com.gm910.silvania.magic.Spellform.Spell;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;

public class EntityVectorSelector extends SpellPart {

	public EntityVectorSelector() {
		super(null); // TODO
	}

	@Override
	public List<SpellDataType<?>> getParameters() {
		return Lists.newArrayList(SpellDataType.SPECIFIC_ENTITY);
	}

	@Override
	public Map<String, SpellDataType<?>> returnValueMap() {
		return Maps.newHashMap(ImmutableMap.of("vector", SpellDataType.VECTOR));
	}

	@Override
	public Map<String, SpellDataHolder<?>> executeAction(Spell spell, List<SpellDataHolder<?>> inputs)
			throws SpellException {

		return Maps.newHashMap(ImmutableMap.of("vector",
				SpellDataHolder.create(((Entity) inputs.get(0).getValue()).getPositionVector())));
	}

	@Override
	public boolean updateSpellPartFromBlock(BlockInfo info) {
		return false;
	}

}
