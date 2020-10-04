package com.gm910.silvania.init;

import com.gm910.silvania.SilvaniaMod;

import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ItemInit {
	private ItemInit() {
	}

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
			SilvaniaMod.MODID);
/*
	public static final RegistryObject<Item> MIND_MIRROR = ITEMS.register("mind_mirror", () -> new MindMirrorItem());

	public static final RegistryObject<Item> MEMORY_BOOK = ITEMS.register("memory_book", () -> new MemoryBook());
*/
	public static void registerISTERs() {

	}

}
