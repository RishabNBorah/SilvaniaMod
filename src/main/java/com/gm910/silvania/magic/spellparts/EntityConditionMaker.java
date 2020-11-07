package com.gm910.silvania.magic.spellparts;

import java.util.List;
import java.util.Map;

import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.magic.SpellException;
import com.gm910.silvania.magic.SpellException.NullValueSpellException;
import com.gm910.silvania.magic.SpellPart;
import com.gm910.silvania.magic.Spellform.Spell;
import com.gm910.silvania.magic.data.EntityCondition;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EntityConditionMaker extends SpellPart {

	// TODO
	private EntityCondition condition = EntityCondition.ALWAYS_TRUE;

	public EntityConditionMaker() {
		super(null); // TODO

	}

	public void setCondition(EntityCondition condition) {
		this.condition = condition;
	}

	public EntityCondition getCondition() {
		return condition;
	}

	@Override
	public List<SpellDataType<?>> getParameters() {
		return Lists.newArrayList();
	}

	@Override
	public Map<String, SpellDataType<?>> returnValueMap() {
		return Maps.newHashMap(ImmutableMap.of("condition", SpellDataType.ENTITY_CONDITION));
	}

	@Override
	public Map<String, SpellDataHolder<?>> executeAction(Spell spell, List<SpellDataHolder<?>> inputs)
			throws SpellException {
		if (this.condition == null) {
			throw new NullValueSpellException(this);
		}
		return Maps.newHashMap(
				ImmutableMap.of("condition", SpellDataHolder.create(condition, SpellDataType.ENTITY_CONDITION)));
	}

	@Override
	public boolean updateSpellPartFromBlock(BlockInfo info) {

		return false;
	}

}
