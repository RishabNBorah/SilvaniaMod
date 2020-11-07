package com.gm910.silvania.init;

import com.gm910.silvania.SilvaniaMod;
import com.gm910.silvania.effect.EffectIncorporeal;

import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class EffectInit {
	private EffectInit() {
	}

	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS,
			SilvaniaMod.MODID);

	public static final RegistryObject<Effect> INCORPOREAL = EFFECTS.register("incorporeal",
			() -> new EffectIncorporeal());
}
