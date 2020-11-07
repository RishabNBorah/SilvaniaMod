package com.gm910.silvania.effect;

import com.gm910.silvania.SilvaniaMod;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

public class EffectIncorporeal extends Effect {

	public EffectIncorporeal() {
		super(EffectType.NEUTRAL, 0x55FFFF);
		this.setRegistryName(SilvaniaMod.MODID, "incorporeal");
	}

	@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
		EffectInstance instancia = entityLivingBaseIn.getActivePotionEffect(this);
		if (instancia.getDuration() <= 1) {
			entityLivingBaseIn.setInvisible(false);
		} else {
			entityLivingBaseIn.setInvisible(true);
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

}
