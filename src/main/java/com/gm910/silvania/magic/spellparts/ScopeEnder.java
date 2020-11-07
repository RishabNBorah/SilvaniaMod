package com.gm910.silvania.magic.spellparts;

import java.util.List;
import java.util.Map;

import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.magic.SpellException;
import com.gm910.silvania.magic.SpellPart;
import com.gm910.silvania.magic.SpellPartType;
import com.gm910.silvania.magic.Spellform.Spell;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ScopeEnder extends SpellPart {

	public ScopeEnder() {
		super(SpellPartType.SCOPE_ENDER);
	}

	@Override
	public List<SpellDataType<?>> getParameters() {
		return Lists.newArrayList();
	}

	@Override
	public Map<String, SpellDataHolder<?>> executeAction(Spell spell, List<SpellDataHolder<?>> inputs)
			throws SpellException {
		return Maps.newHashMap();
	}

	@Override
	public boolean updateSpellPartFromBlock(BlockInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

}
