package com.gm910.silvania.magic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.gm910.silvania.SilvaniaMod;
import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.magic.spellparts.RegularStarterPart;
import com.gm910.silvania.magic.spellparts.ScopeEnder;

import net.minecraft.util.ResourceLocation;

public class SpellPartType<T extends SpellPart> {

	public static final Map<ResourceLocation, SpellPartType<?>> TYPES = new HashMap<>();

	/**
	 * TODO
	 */
	public static final SpellPartType<RegularStarterPart> REGULAR_STARTER = new SpellPartType<>("regular_starter_part",
			(info) -> null, (info) -> false);
	/**
	 * TODO
	 */
	public static final SpellPartType<ScopeEnder> SCOPE_ENDER = new SpellPartType<>("scope_ender", (info) -> null,
			(info) -> false);

	private Function<BlockInfo, T> supplier;
	private Predicate<BlockInfo> checker;
	private ResourceLocation id;

	public SpellPartType(String id, Function<BlockInfo, T> supplier, Predicate<BlockInfo> checker) {
		this(new ResourceLocation(SilvaniaMod.MODID, id), supplier, checker);
	}

	public SpellPartType(ResourceLocation id, Function<BlockInfo, T> supplier, Predicate<BlockInfo> checker) {
		this.supplier = supplier;
		this.checker = checker;
		this.id = id;
		TYPES.put(id, this);
	}

	public boolean check(BlockInfo info) {
		return checker.test(info);
	}

	public T create(BlockInfo info) {
		return supplier.apply(info);
	}

	public ResourceLocation getId() {
		return id;
	}
}