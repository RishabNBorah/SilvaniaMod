package com.gm910.silvania.magic;

import java.util.List;
import java.util.Map;

import com.gm910.silvania.magic.Spellform.Spariable;
import com.gm910.silvania.magic.Spellform.Spell;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.google.common.collect.Lists;

import net.minecraft.util.Direction;

public abstract class StarterPart extends SpellPart {

	public StarterPart(SpellPartType<?> type) {
		super(type);
	}

	@Override
	public Direction getInputSide() {
		throw new IllegalStateException("Starter parts have no input side");
	}

	@Override
	public void setInputSide(Direction dir) {
		throw new IllegalStateException("Starter parts have no input side");
	}

	@Override
	public List<SpellDataType<?>> getParameters() {
		return Lists.newArrayList();
	}

	@Override
	public Map<String, SpellDataHolder<?>> executeAction(Spell spell, List<SpellDataHolder<?>> inputs) {
		return executeAction();
	}

	public abstract Map<String, SpellDataHolder<?>> executeAction();

	@Override
	public abstract Map<String, SpellDataType<?>> returnValueMap();

	@Override
	public final boolean shouldHaveScope() {
		return false;
	}

	@Override
	public final boolean shouldRepeatScope(Spell spell, List<Spariable<?>> scopeVariables) {
		return false;
	}

	@Override
	public final boolean shouldRunScope(Spell spell, List<Spariable<?>> scopeVariables) {
		return false;
	}

}
