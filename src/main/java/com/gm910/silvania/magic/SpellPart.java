package com.gm910.silvania.magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.magic.Spellform.Spariable;
import com.gm910.silvania.magic.Spellform.Spell;
import com.gm910.silvania.magic.Spellform.SpellScope;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.gm910.silvania.magic.spellparts.ScopeEnder;
import com.google.common.collect.Lists;

import net.minecraft.util.Direction;

public abstract class SpellPart {

	private Direction inputSide = Direction.NORTH;
	private Direction outputSide = Direction.SOUTH;
	private List<Spariable<?>> currentParameters = new ArrayList<>();

	private Map<String, Spariable<?>> outputDestinations = new HashMap<>();
	private Spellform spellform;
	private SpellScope scope;

	private final SpellPartType<?> type;

	public SpellPart(SpellPartType<?> type) {
		this.type = type;
	}

	/**
	 * Get the SpellPartType to avoid dumb "getClass" checks and the like
	 * 
	 * @return
	 */
	public SpellPartType<?> getType() {
		return type;
	}

	/**
	 * Gets the types of the parameters of the functionality
	 * 
	 * @return
	 */
	public abstract List<SpellDataType<?>> getParameters();

	/**
	 * Map of the names of return values and their datatype.
	 * 
	 * @return
	 */
	public Map<String, SpellDataType<?>> returnValueMap() {
		return new HashMap<>();
	}

	/**
	 * Gets the parent spellform
	 * 
	 * @return
	 */
	public Spellform getSpellform() {
		return spellform;
	}

	/**
	 * Set parent spellform
	 * 
	 * @param spellform
	 */
	public void setSpellform(Spellform spellform) {
		this.spellform = spellform;
	}

	/**
	 * The destination variables that each value outputs to
	 * 
	 * @return
	 */
	public Map<String, Spariable<?>> getOutputDestinations() {
		return outputDestinations;
	}

	/**
	 * Gets variable by name, for updating for example
	 * 
	 * @param name
	 * @return
	 */
	public Spariable<?> getFromName(String name) {
		return this.getSpellform().getVariableByName(this.getSpellform().getIndex(this), name);
	}

	/**
	 * The side which the program feeds its string of command into
	 * 
	 * @return
	 */
	public Direction getInputSide() {
		return inputSide;
	}

	/**
	 * The side which the program feeds its string of commands out of
	 * 
	 * @return
	 */
	public Direction getOutputSide() {
		return outputSide;
	}

	public void setInputSide(Direction inputSide) {
		this.inputSide = inputSide;
	}

	public void setOutputSide(Direction outputSide) {
		this.outputSide = outputSide;
	}

	/**
	 * Gets the "spell variables" which give the values of the input
	 * 
	 * @return
	 */
	public List<Spellform.Spariable<?>> getCurrentParameters() {
		return currentParameters;
	}

	/**
	 * CREATES a list of variables that are created by this spelpart to be stored in
	 * "RAM"
	 * 
	 * @return
	 */
	public List<Spellform.Spariable<?>> createVariables() {
		return Lists.newArrayList();
	}

	/**
	 * Whether this part should define the beginning of a scope
	 * 
	 * @return
	 */
	public boolean shouldHaveScope() {
		return false;
	}

	/**
	 * Whether this part is the beginning or end of an actual defined scope in RAM
	 * 
	 * @return
	 */
	public final boolean hasScope() {
		return scope != null;
	}

	/**
	 * Whether this part is the end of a scope
	 * 
	 * @return
	 */
	public boolean endsScope() {
		return this instanceof ScopeEnder;
	}

	/**
	 * Whether this scope should be repeated given these variables, like in a loop
	 * 
	 * @param scopeVariables
	 * @return
	 */
	public boolean shouldRepeatScope(Spell spell, List<Spariable<?>> scopeVariables) {
		return false;
	}

	/**
	 * Whether this scope should be skipped or not given these variables, like an if
	 * statement
	 * 
	 * @param scopeVariables
	 * @return
	 */
	public boolean shouldRunScope(Spell spell, List<Spariable<?>> scopeVariables) {
		return true;
	}

	/**
	 * Returns the scope object assigned to the part by the spellform
	 * 
	 * @return
	 */
	public SpellScope getScope() {
		return this.scope;
	}

	public void setScope(SpellScope scope) {
		this.scope = scope;
	}

	/**
	 * Runs the "program" of the spell
	 * 
	 * @param inputs
	 * @return
	 * @throws SpellException which occurs if a "magic error" happens
	 */
	@Nullable
	public abstract Map<String, SpellDataHolder<?>> executeAction(Spell spell, List<SpellDataHolder<?>> inputs)
			throws SpellException;

	/**
	 * Whether this spell part can exist at the block, and if this is true the
	 * spellform will regenerate itself
	 * 
	 * @param info
	 * @return
	 */
	public boolean canSpellPartExistAtBlock(BlockInfo info) {
		return this.type.check(info);
	}

	/**
	 * return true if this spellpart's update redefines variables or scope to
	 * reinitialize something
	 */
	public abstract boolean updateSpellPartFromBlock(BlockInfo info);

	/**
	 * Expected to create new spellpart on each call based on the data of the given
	 * block
	 * 
	 * @param block
	 * @return
	 */
	public static SpellPart createForBlock(BlockInfo block) {

		for (SpellPartType<?> type : SpellPartType.TYPES.values()) {
			if (type.check(block)) {
				return type.create(block);
			}
		}
		return null;
	}

	/**
	 * Resets the spell part's entire data
	 */
	public void reset() {

		inputSide = Direction.NORTH;
		outputSide = Direction.SOUTH;

		currentParameters = new ArrayList<>();

		outputDestinations = new HashMap<>();
		spellform = null;
		scope = null;
	}
}
