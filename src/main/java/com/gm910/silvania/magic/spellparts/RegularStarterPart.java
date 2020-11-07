package com.gm910.silvania.magic.spellparts;

import java.util.Map;

import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.magic.SpellPartType;
import com.gm910.silvania.magic.StarterPart;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.google.common.collect.Maps;

public class RegularStarterPart extends StarterPart {

	public RegularStarterPart() {
		super(SpellPartType.REGULAR_STARTER);
	}

	@Override
	public Map<String, SpellDataHolder<?>> executeAction() {
		return Maps.newHashMap();
	}

	@Override
	public Map<String, SpellDataType<?>> returnValueMap() {
		return Maps.newHashMap();
	}

	@Override
	public boolean updateSpellPartFromBlock(BlockInfo info) {

		return false;
	}

}
