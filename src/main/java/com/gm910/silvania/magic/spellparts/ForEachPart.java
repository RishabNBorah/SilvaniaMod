package com.gm910.silvania.magic.spellparts;

import java.util.List;
import java.util.Map;

import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.magic.SpellException;
import com.gm910.silvania.magic.SpellPart;
import com.gm910.silvania.magic.Spellform.Spariable;
import com.gm910.silvania.magic.Spellform.Spell;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ForEachPart extends SpellPart {

	private SpellDataType<?> objectType;
	private String objectName;

	public ForEachPart() {
		super(null); // TODO
	}

	@Override
	public boolean shouldHaveScope() {
		return true;
	}

	@Override
	public boolean shouldRepeatScope(Spell spell, List<Spariable<?>> scopeVariables) {
		Spariable<Integer> index = (Spariable<Integer>) scopeVariables.stream()
				.filter((e) -> e.getName().equals("_index" + this.getSpellform().getIndex(this))).findAny().get();
		Spariable<List<?>> list = (Spariable<List<?>>) scopeVariables.stream()
				.filter((e) -> e.getName().equals("_list" + this.getSpellform().getIndex(this))).findAny().get();
		return index.getData(spell).getValue() < list.getData(spell).getValue().size();
	}

	@Override
	public boolean shouldRunScope(Spell spell, List<Spariable<?>> scopeVariables) {
		Spariable<List<?>> list = (Spariable<List<?>>) scopeVariables.stream()
				.filter((e) -> e.getName().equals("_list" + this.getSpellform().getIndex(this))).findAny().get();
		return !list.getData(spell).getValue().isEmpty();
	}

	@Override
	public Map<String, SpellDataType<?>> returnValueMap() {
		return super.returnValueMap();
	}

	@Override
	public List<Spariable<?>> createVariables() {
		return Lists.newArrayList(
				getSpellform().new Spariable<>("_list" + this.getSpellform().getIndex(this), SpellDataType.LIST),
				getSpellform().new Spariable<>("_index" + this.getSpellform().getIndex(this), SpellDataType.INTEGER),
				getSpellform().new Spariable<>(objectName, objectType));
	}

	@Override
	public List<SpellDataType<?>> getParameters() {
		return Lists.newArrayList(SpellDataType.LIST);
	}

	@Override
	public Map<String, SpellDataHolder<?>> executeAction(Spell spell, List<SpellDataHolder<?>> inputs)
			throws SpellException {
		List<Spariable<?>> vars = getScope().getScopeVariables();
		Spariable current = vars.stream().filter((e) -> e.getName().equals(objectName)).findAny().get();
		Spariable<Integer> index = (Spariable<Integer>) vars.stream()
				.filter((e) -> e.getName().equals("_index" + this.getSpellform().getIndex(this))).findAny().get();
		Spariable<List<?>> list = (Spariable<List<?>>) vars.stream()
				.filter((e) -> e.getName().equals("_list" + this.getSpellform().getIndex(this))).findAny().get();
		if (index.getData(spell) == null) {
			index.setData(spell, 0);
			list.setData(spell, (List) inputs.get(0).getValue());
		}
		current.setData(spell, list.getData(spell).getValue().get(index.getData(spell).getValue()));
		index.setData(spell, index.getData(spell).getValue() + 1);
		return Maps.newHashMap();
	}

	@Override
	public boolean updateSpellPartFromBlock(BlockInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

}
