package com.gm910.silvania.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class GMCaps<T, K> implements ICapabilitySerializable<CompoundNBT> {
/*
	@CapabilityInject(Formshift.class)
	public static Capability<Formshift> FORM = null;
*/
	private Capability<T> capability;

	private K owner;

	private T instance;

	public GMCaps(Capability<T> capability, K owner) {
		this.capability = capability;
		this.owner = owner;
		this.instance = capability.getDefaultInstance();
		if (instance instanceof IModCapability) {
			((IModCapability<K>) instance).$setOwner(owner);
		}
	}

	public K getOwner() {
		return owner;
	}

	public T getInstance() {

		return instance;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return this.capability.orEmpty(cap, LazyOptional.of(() -> instance));
	}

	@Override
	public CompoundNBT serializeNBT() {
		return (CompoundNBT) this.capability.getStorage().writeNBT(capability, instance, null);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.capability.getStorage().readNBT(capability, instance, null, nbt);
	}

	public static void preInit() {
/*
		CapabilityManager.INSTANCE.register(Formshift.class, new FormStorage(), () -> {
			return new Formshift();
		});*/
	}

	@SubscribeEvent
	public static void attachEn(AttachCapabilitiesEvent<Entity> event) {
		
	}

	@SubscribeEvent
	public static void attachCh(AttachCapabilitiesEvent<World> event) {
		

	}

}
