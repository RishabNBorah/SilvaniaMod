package com.gm910.silvania.magic.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class EntityCondition implements Predicate<Entity> {

	public static final EntityCondition ALWAYS_TRUE = new EntityCondition() {
		@Override
		public boolean test(Entity t) {
			return true;
		}

		@Override
		public CompoundNBT serializeNBT() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putBoolean("AlwaysTrue", true);
			return nbt;
		}
	};

	private Integer numericLimit;
	private Set<DimensionType> dimensionsIn = new HashSet<>();
	private Predicate<Entity> filter = Predicates.alwaysTrue();

	@Nullable
	private Double minDistance;
	private Double maxDistance;

	@Nullable
	private Double xPos;
	private Double yPos;
	private Double zPos;

	@Nullable
	private Double dX;
	private Double dY;
	private Double dZ;

	@Nullable
	private AxisAlignedBB aabb;
	private BiConsumer<Vec3d, List<? extends Entity>> sorter;
	@Nullable
	private String username;
	@Nullable
	private UUID uuid;
	@Nullable
	private EntityType<?> type;

	private EntityCondition() {

	}

	@Override
	public boolean test(Entity t) {
		boolean passes = true;

		return passes;
	}

	public static EntityCondition fromNBT(CompoundNBT nbt) {
		if (nbt.getBoolean("AlwaysTrue"))
			return ALWAYS_TRUE;
		else {
			EntityCondition con = new EntityCondition();
			con.deserializeNBT(nbt);
			return con;
		}
	}

	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();

		return nbt;
	}

	private void deserializeNBT(CompoundNBT nbt) {

	}

}
