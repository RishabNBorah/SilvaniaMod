package com.gm910.silvania.init;

import com.gm910.silvania.SilvaniaMod;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlockInit {
	private BlockInit() {
	}

	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS,
			SilvaniaMod.MODID);

	public static final RegistryObject<Block> SPELL_STARTER = null;
	/*
		public static final RegistryObject<Block> MIND_JELLY = (new BlockRegistryObject("mind_jelly",
				() -> new MindJellyBlock())).makeItem(() -> new Item.Properties().group(ItemGroup.DECORATIONS))
						.createRegistryObject();
	*/
}
