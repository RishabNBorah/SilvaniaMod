package com.gm910.silvania.magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.gm910.silvania.api.language.Translate;
import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.api.util.ServerPos;
import com.gm910.silvania.magic.data.SpellDataType;
import com.gm910.silvania.magic.data.SpellDataType.SpellDataHolder;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class Spellform implements INBTSerializable<CompoundNBT> {

	/**
	 * The server this spellform is on for ease of access
	 */
	private MinecraftServer server;
	/**
	 * The higher storage of all spellforms in the server
	 */
	private SpellformData data;
	/**
	 * Stores the central position of the spellform
	 */
	private ServerPos center;
	/**
	 * Stores all parts of the spellform
	 */
	private List<SpellPart> parts = new ArrayList<>();
	/**
	 * Stores which index in the parts list corresponds to what real-world blockpos
	 */
	private Map<BlockPos, Integer> partToIndexMap = new HashMap<>();

	/**
	 * Areas where spell can affect
	 */
	private Map<ServerPos, Integer> radii = new HashMap<>();

	private Set<Spariable<?>> spellVariables = new HashSet<>();

	private List<Spell> runningSpells = new ArrayList<>();

	private List<SpellScope> scopes = new ArrayList<>();

	public static final int DEFAULT_MAX_RADIUS = 10;

	public Spellform(SpellformData server) {
		this.server = server.getServer();
		this.data = server;
	}

	public ServerPos getCenter() {
		return center;
	}

	public void setCenter(ServerPos center) {
		this.center = center;
	}

	public int getCenterRadius() {
		return this.parts.size() + DEFAULT_MAX_RADIUS;
	}

	public Map<ServerPos, Integer> getRadii() {
		Map<ServerPos, Integer> radii = new HashMap<>(this.radii);
		radii.put(this.getCenter(), getCenterRadius());
		return radii;
	}

	public BlockPos getPosition(SpellPart part) {
		int index = parts.indexOf(part);
		if (index == -1)
			return null;
		for (BlockPos pos : this.partToIndexMap.keySet()) {
			if (partToIndexMap.getOrDefault(pos, -1) == index) {
				return pos;
			}
		}
		return null;
	}

	public void runSpell() {
		Spell spell = new Spell();
		runningSpells.add(spell);
		spell.spellIndex = runningSpells.indexOf(spell);
		spell.start();
	}

	public void tick() {
		for (Spell spell : new HashSet<>(runningSpells)) {
			if (spell.isEnded()) {
				runningSpells.remove(spell);
			}
			spell.tick();
		}
	}

	public List<Spariable<?>> getCurrentVariables(int index) {
		List<Spariable<?>> list = new ArrayList<>(this.spellVariables);
		List<SpellScope> scopes = getCurrentScope(index);
		for (SpellScope scope : scopes) {
			list.addAll(scope.scopeVariables);
		}
		return list;
	}

	public List<SpellScope> getCurrentScope(int index) {
		List<SpellScope> spellScopes = new ArrayList<>();
		for (SpellScope scope : this.scopes) {
			if (scope.beginning <= index && scope.end >= index) {
				spellScopes.add(scope);
			}
		}
		return spellScopes;
	}

	public int getIndex(SpellPart part) {
		return parts.indexOf(part);
	}

	/**
	 * 
	 * @return error if the spellform could find a structure to create itself from
	 */
	public Pair<Integer, String> create() {

		Pair<Integer, String> msg = this.initialize();
		if (msg != null)
			return msg;
		this.data.addSpellform(this, true);
		return null;
	}

	public Spariable<?> getVariableByName(int index, String name) {
		List<Spariable<?>> vars = new ArrayList<>(this.spellVariables);
		vars.addAll(this.getCurrentScope(index).stream().flatMap((e) -> e.scopeVariables.stream())
				.collect(Collectors.toList()));
		for (Spariable<?> so : vars) {
			if (so.getName().equals(name)) {
				return so;
			}
		}
		return null;
	}

	/**
	 * @return null if success, error message + index otherwise
	 */
	public Pair<Integer, String> initialize() {
		ServerWorld world = this.getCenter().getWorld(server);
		BlockPos nextPos = getCenter().getPos();
		SpellPart starter = SpellPart.createForBlock(new BlockInfo(world, nextPos));
		if (starter == null) {
			this.data.removeSpellform(this);
			return Pair.of(0, "error.no.program");
		}
		Direction dir = starter.getOutputSide();
		this.parts.clear();
		this.partToIndexMap.clear();
		parts.add(starter);
		partToIndexMap.put(nextPos, 0);
		Stack<SpellScope> scopeStack = new Stack<>();
		while (nextPos != null) {
			BlockPos off = nextPos.offset(dir);
			BlockInfo nextblock = new BlockInfo(world, off);
			SpellPart spart = SpellPart.createForBlock(nextblock);

			if (spart.getInputSide() == dir.getOpposite()) {
				parts.add(spart);
				int index = parts.indexOf(spart);
				spart.setSpellform(this);
				if (spart.shouldHaveScope()) {
					SpellScope scopo = new SpellScope(index);
					scopeStack.push(scopo);
					spart.setScope(scopo);
				} else if (spart.endsScope()) {
					SpellScope scope = scopeStack.isEmpty() ? null : scopeStack.pop();
					if (scope == null) {
						return Pair.of(index, Translate.translate("error.mismatched.scope"));
					}
					spart.setScope(scope);
					scope.setEnd(index);
					scopes.add(scope);
				}
				List<Spariable<?>> vars = spart.createVariables();
				List<Spariable<?>> existingVars = new ArrayList<>(spellVariables);

				SpellScope recent = scopeStack.isEmpty() ? null : scopeStack.peek();
				for (SpellScope biggers : scopeStack) {

					existingVars.addAll(biggers.scopeVariables);
				}
				for (Spariable<?> spar : vars) {
					for (Spariable<?> exist : existingVars) {
						if (spar.name.equals(exist.name)) {
							return Pair.of(index, Translate.translate("error.duplicate.variable", spar.name));
						}
					}
				}
				if (recent != null) {
					for (Spariable<?> spar : vars) {
						if (!spar.isGlobal) {
							recent.getScopeVariables().add(spar);

						} else {
							spellVariables.add(spar);
						}
					}
				} else {
					for (Spariable<?> spar : vars) {
						spellVariables.add(spar);
					}
				}
				partToIndexMap.put(off, parts.indexOf(spart));
				nextPos = off;
			} else {
				nextPos = null;
			}
		}
		return parts.size() <= 0 ? Pair.of(0, Translate.translate("error.no.program"))
				: (!scopeStack.empty() ? Pair.of(parts.size(), Translate.translate("error.mismatched.scope")) : null);
	}

	/**
	 * @return error message if the spellform no longer exists in a usable form
	 */
	public Pair<Integer, String> update() {
		for (BlockPos pos : partToIndexMap.keySet()) {
			SpellPart part = SpellPart.createForBlock(new BlockInfo(this.getWorld(), pos));
			if (part == null || !part.canSpellPartExistAtBlock(new BlockInfo(this.getWorld(), pos))) {
				return this.initialize();
			}
			part.reset();
			if (part.updateSpellPartFromBlock(new BlockInfo(this.getWorld(), pos))) {
				return this.initialize();
			}
			parts.set(partToIndexMap.get(pos), part);
		}
		return null;
	}

	public ServerWorld getWorld() {
		return this.center.getWorld(server);
	}

	public void deserializeNBT(CompoundNBT nbt) {

	}

	public SpellformData getData() {
		return data;
	}

	public MinecraftServer getServer() {
		return server;
	}

	@Override
	public CompoundNBT serializeNBT() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Spellform))
			return false;
		return ((Spellform) obj).serializeNBT().equals(this.serializeNBT());
	}

	public class Spell implements INBTSerializable<CompoundNBT> {

		private int partIndex = 0;
		private int spellIndex = 0;
		private boolean ended = false;

		private Spell() {

		}

		private Spell(CompoundNBT nbt) {
			this();
			this.deserializeNBT(nbt);
		}

		public Spellform getSpellform() {
			return Spellform.this;
		}

		public int getSpellIndex() {
			return spellIndex;
		}

		public void start() {
			// TODO
		}

		public void tick() {
			if (ended)
				return;
			if (parts.size() >= partIndex) {
				end();
				return;
			}
			SpellPart spart = parts.get(partIndex);
			List<Spariable<?>> parameters = spart.getCurrentParameters();
			Map<String, SpellDataHolder<?>> output = null;
			try {
				List<SpellDataHolder<?>> ls = parameters.stream().map((e) -> e.getData(this))
						.collect(Collectors.toList());

				output = spart.executeAction(this, ls);
			} catch (SpellException e) {
				e.setPart(spart);
				e.setPos(new ServerPos(getPosition(spart), getCenter().getD()));
				end(e);
				return;
			}

			Map<String, Spariable<?>> sparDests = spart.getOutputDestinations();
			for (String s : output.keySet()) {
				Spariable spari = sparDests.get(s);
				if (spari == null || spari.type != output.get(s).getType())
					throw new IllegalStateException("Variable null of name " + s + " in " + this);
				spari.setData(this, output.get(s).getValue());
			}
			List<Spariable<?>> vars = getCurrentScope(partIndex).stream().flatMap((e) -> e.scopeVariables.stream())
					.collect(Collectors.toList());
			if (spart.hasScope()) {
				if (!spart.shouldRunScope(this, vars)) {
					partIndex = spart.getScope().end;
				}
			} else if (spart.endsScope()) {
				if (spart.shouldRepeatScope(this, vars)) {
					partIndex = spart.getScope().beginning - 1;
				}
			}
			onEndScope(false);
			partIndex++;
		}

		public void onEndScope(boolean terminateProgram) {
			if (terminateProgram) {

				spellVariables.forEach((e) -> e.clearRAM(runningSpells.indexOf(this)));
				scopes.stream().flatMap((e) -> e.scopeVariables.stream())
						.forEach((e) -> e.clearRAM(runningSpells.indexOf(this)));
			} else {
				List<SpellScope> scopes = getCurrentScope(this.partIndex);
				SpellScope thisScope = null;
				for (SpellScope scope : scopes) {
					if (scope.end == partIndex) {
						if (thisScope != null) {
							throw new IllegalStateException("Two scope ends at " + partIndex + " for " + this);
						}
						thisScope = scope;

					}
				}
				if (thisScope == null) {
					return;
				}
				thisScope.scopeVariables.forEach((e) -> e.clearRAM(runningSpells.indexOf(this)));
			}
		}

		public void end() {
			end(null);
		}

		public void end(SpellException ex) {
			this.ended = true;
			onEndScope(true);
			if (ex != null) {
				ServerPos pos = ex.getPos();
				getWorld().addLightningBolt(
						new LightningBoltEntity(getWorld(), pos.getX(), pos.getY(), pos.getZ(), true));
				// TODO
			}
		}

		public boolean isEnded() {
			return ended;
		}

		@Override
		public String toString() {
			return "Spell with iteration " + this.partIndex + " cast by Spellform: " + this.getSpellform() + " ;";
		}

		@Override
		public CompoundNBT serializeNBT() {
			return null;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
		}

	}

	public class SpellScope implements INBTSerializable<INBT> {

		private int beginning;
		private int end;
		private List<Spariable<?>> scopeVariables = new ArrayList<>();

		public SpellScope(int beginning) {
			this.beginning = beginning;
		}

		public SpellScope(int beginning, int end) {
			this.beginning = beginning;
			this.end = end;
		}

		public SpellScope() {

		}

		public List<Spariable<?>> getScopeVariables() {
			return scopeVariables;
		}

		public void setBeginning(int beginning) {
			this.beginning = beginning;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public int getBeginning() {
			return beginning;
		}

		public int getEnd() {
			return end;
		}

		public SpellScope(INBT nbt) {
			this.deserializeNBT(nbt);
		}

		public SpellPart getHead() {
			return parts.get(beginning);
		}

		public List<SpellPart> getParts() {
			return parts.subList(beginning, end + 1);
		}

		@Override
		public INBT serializeNBT() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("Beginning", beginning);
			nbt.putInt("End", end);
			// TODO
			return nbt;
		}

		@Override
		public void deserializeNBT(INBT nbt) {
			CompoundNBT compound = (CompoundNBT) nbt;
			this.beginning = compound.getInt("Beginning");
			this.end = compound.getInt("End");
		}
	}

	public class Spariable<T> implements INBTSerializable<CompoundNBT> {
		private String name;
		private boolean isStatic;
		private boolean isFinal;
		private SpellDataType<T> type;
		private Int2ObjectMap<SpellDataHolder<T>> data = new Int2ObjectOpenHashMap<>();
		private boolean isHidden;
		private boolean isGlobal;

		public Spariable(String name, SpellDataType<T> type) {
			this.name = name;
			this.type = type;
		}

		/**
		 * If variable's value continues to stay what it is between spell exectutions
		 * 
		 * @param isStatic
		 */
		public void setStatic(boolean isStatic) {
			this.isStatic = isStatic;
			SpellDataHolder<T> generic = this.data.values().stream().findAny().orElse(null);
			this.data.put(0, generic);
		}

		/**
		 * If variable's value continues to stay what it is between spell exectutions
		 * 
		 * @param isStatic
		 */
		public boolean isStatic() {
			return isStatic;
		}

		/**
		 * If the program can not change the value of this variable while running
		 * 
		 * @return
		 */
		public boolean isFinal() {
			return isFinal;
		}

		/**
		 * If the program can not change the value of this variable while it is running
		 * 
		 * @param isFinal
		 */
		public void setFinal(boolean isFinal) {
			this.isFinal = isFinal;
		}

		public void setHidden(boolean isHidden) {
			this.isHidden = isHidden;
		}

		public void setGlobal(boolean isGlobal) {
			this.isGlobal = isGlobal;
		}

		public boolean isHidden() {
			return isHidden;
		}

		/**
		 * Whetherthis variable is forced to be global
		 * 
		 * @return
		 */
		public boolean isGlobal() {
			return isGlobal;
		}

		public String getName() {
			return name;
		}

		public SpellDataHolder<T> setDataStatic(T value) {
			if (!this.isStatic())
				throw new UnsupportedOperationException("Spell variable is not static");
			return setData(0, value);
		}

		public SpellDataHolder<T> setData(int spellIndex, T value) {
			if (this.isFinal() && this.getData(spellIndex) != null) {
				throw new UnsupportedOperationException("Cannot set final variable");
			}
			if (this.isStatic()) {
				spellIndex = 0;
			}
			return data.put(spellIndex, SpellDataHolder.create(value, null));
		}

		public SpellDataHolder<T> setData(Spell spell, T value) {
			return setData(runningSpells.indexOf(spell), value);
		}

		public SpellDataHolder<T> getData(int spellIndex) {
			if (this.isStatic()) {
				spellIndex = 0;
			}
			return data.get(spellIndex);
		}

		public SpellDataHolder<T> getDataStatic() {
			if (!this.isStatic())
				throw new UnsupportedOperationException("Non-static variable cannot be accessed in static way");
			else {
				return this.getData(0);
			}
		}

		public SpellDataHolder<T> getData(Spell spell) {
			return getData(runningSpells.indexOf(spell));
		}

		public void clearRAM(int spellIndex) {
			data.remove(spellIndex);
		}

		@Override
		public CompoundNBT serializeNBT() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			// TODO Auto-generated method stub

		}

	}
}
