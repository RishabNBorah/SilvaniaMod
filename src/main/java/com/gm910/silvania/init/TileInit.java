package com.gm910.silvania.init;

import com.gm910.silvania.SilvaniaMod;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class TileInit {
	private TileInit() {
	}

	public static final DeferredRegister<TileEntityType<?>> TILE_TYPES = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, SilvaniaMod.MODID);
/*
	public static final RegistryObject<TileEntityType<Landvaettr>> LANDVAETTR = TILE_TYPES.register("landvaettr",
			() -> TileEntityType.Builder.create(Landvaettr::new, BlockInit.LANDVAETTR.get()).build(null));

	public static final RegistryObject<TileEntityType<Stormvaettr>> STORMVAETTR = TILE_TYPES.register("stormvaettr",
			() -> TileEntityType.Builder.create(Stormvaettr::new, BlockInit.STORMVAETTR.get()).build(null));

	public static final RegistryObject<TileEntityType<Brennisteinvaettr>> BRENNISTEINVAETTR = TILE_TYPES.register(
			"brennisteinvaettr",
			() -> TileEntityType.Builder.create(Brennisteinvaettr::new, BlockInit.BRENNISTEINVAETTR.get()).build(null));

	public static final RegistryObject<TileEntityType<Endisteinvaettr>> ENDISTEINVAETTR = TILE_TYPES.register(
			"endisteinvaettr",
			() -> TileEntityType.Builder.create(Endisteinvaettr::new, BlockInit.ENDISTEINVAETTR.get()).build(null));

	public static final RegistryObject<TileEntityType<WorldControllerTileEntity>> WORLD_CONTROLLER = TILE_TYPES
			.register("world_controller", () -> TileEntityType.Builder
					.create(WorldControllerTileEntity::new, BlockInit.WORLD_CONTROLLER.get()).build(null));
*/
	public static void registerTESRs() {
/*
		ClientRegistry.bindTileEntityRenderer(TileInit.WORLD_CONTROLLER.get(), WorldControllerTESR::new);*/
	}

}
