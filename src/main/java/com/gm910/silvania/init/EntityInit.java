package com.gm910.silvania.init;

import com.gm910.silvania.SilvaniaMod;
import com.gm910.silvania.api.sitting.SitEntity;

import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class EntityInit {
	private EntityInit() {
	}

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES,
			SilvaniaMod.MODID);

	static {
		SitEntity.introduceSitEntity(SilvaniaMod.MODID, ENTITY_TYPES);

	}

	public static void registerSittableEntities() {
		/*SitEntity.registerSittableBlockStatesFromPredicate(
				((ThroneBlock) BlockInit.THRONE.get()).getStatesForHalf(DoubleBlockHalf.LOWER),
				ImmutableSet.of((e) -> e instanceof LivingEntity));*/
	}
/*
	public static final RegistryObject<EntityType<LivingBlockEntity>> LIVING_BLOCK = ENTITY_TYPES
			.register("living_block", () -> {
				return EntityType.Builder.<LivingBlockEntity>create(LivingBlockEntity::new, EntityClassification.MISC)
						.size(1.0f, 1.0f).build(new ResourceLocation(SilvaniaMod.MODID, "living_block").toString());
			});

	public static final RegistryObject<EntityType<CitizenEntity>> CITIZEN = ENTITY_TYPES.register("citizen", () -> {
		return EntityType.Builder.<CitizenEntity>create(CitizenEntity::new, EntityClassification.CREATURE)
				.size(1.0f, 1.0f).build(new ResourceLocation(SilvaniaMod.MODID, "citizen").toString());
	});

	public static final EntityType<Deity> DEITY_DUMMY = EntityType.Builder
			.<Deity>create(Deity::new, EntityClassification.AMBIENT).size(1.0f, 1.0f)
			.build(new ResourceLocation(SilvaniaMod.MODID, "deity_dummy").toString());
*/
}
