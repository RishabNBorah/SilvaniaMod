package com.gm910.silvania.magic.spellparts;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.api.util.ServerPos;
import com.gm910.silvania.magic.SpellException;
import com.gm910.silvania.magic.SpellPart;
import com.gm910.silvania.magic.Spellform.Spell;
import com.gm910.silvania.magic.data.EntityCondition;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class EntitySelectorPart extends SpellPart {

	public EntitySelectorPart() {
		super(null); // TODO

	}

	@Override
	public List<SpellDataType<?>> getParameters() {
		return Lists.newArrayList(SpellDataType.ENTITY_CONDITION);
	}

	@Override
	public Map<String, SpellDataType<?>> returnValueMap() {
		return Maps.newHashMap(ImmutableMap.of("entities", SpellDataType.LIST));
	}

	@Override
	public Map<String, SpellDataHolder<?>> executeAction(Spell spell, List<SpellDataHolder<?>> inputs)
			throws SpellException {
		EntityCondition condition = (EntityCondition) inputs.get(0).getValue();
		List<Entity> list = ((Collection<ServerWorld>) this.getSpellform().getServer().getWorlds()).stream()
				.flatMap((world) -> {
					Map<ServerPos, Integer> radii = this.getSpellform().getRadii();
					return world.getEntities().filter((e) -> {
						if (radii.isEmpty())
							return false;
						for (ServerPos pos : radii.keySet()) {
							if (e.dimension == pos.getDimension()
									&& e.getDistanceSq(new Vec3d(pos)) < Math.pow(radii.getOrDefault(pos, 0), 2)) {
								return true;
							}
						}
						return false;
					});
				}).filter((e) -> {
					return condition.test(e);
				}).collect(Collectors.toList());

		return Maps.newHashMap(ImmutableMap.of("entities", new SpellDataList(list)));
	}

	@Override
	public boolean updateSpellPartFromBlock(BlockInfo info) {

		return false;
	}

}
