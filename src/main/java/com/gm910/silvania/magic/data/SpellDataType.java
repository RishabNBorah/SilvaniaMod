package com.gm910.silvania.magic.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.gm910.silvania.api.language.Translate;
import com.gm910.silvania.api.util.BlockInfo;
import com.gm910.silvania.api.util.GMNBT;
import com.gm910.silvania.api.util.ServerPos;
import com.gm910.silvania.magic.Spellform;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class SpellDataType<T> {

	public static final SpellDataType<SpellDataList> LIST = new SpellDataType<>("list", SpellDataList.class,
			() -> new SpellDataList(), (e) -> e.serializeNBT(), (b) -> new SpellDataList((ListNBT) b));
	public static final SpellDataType<Boolean> BOOLEAN = new SpellDataType<>("boolean", boolean.class, () -> false,
			(e) -> ByteNBT.valueOf(e), (b) -> ((ByteNBT) b).getByte() == 1);
	public static final SpellDataType<Integer> INTEGER = new SpellDataType<>("integer", int.class, () -> 0,
			(e) -> IntNBT.valueOf(e), (b) -> ((IntNBT) b).getInt());
	public static final SpellDataType<Double> DECIMAL = new SpellDataType<Double>("decimal", double.class, () -> 0.0,
			(e) -> DoubleNBT.valueOf(e), (b) -> ((DoubleNBT) b).getDouble()) {
		@Override
		public boolean instanceOf(Object o) {
			if (int.class.isInstance(o)) {
				return false;
			}
			return super.instanceOf(o);
		}
	};
	public static final SpellDataType<Entity> SPECIFIC_ENTITY = new SpellDataType<>("specific_entity", Entity.class,
			() -> null, (e) -> StringNBT.valueOf(e.getUniqueID().toString()), (spellform, b) -> ServerPos
					.getEntityFromUUID(UUID.fromString(((StringNBT) b).getString()), spellform.getServer()));
	public static final SpellDataType<EntityCondition> ENTITY_CONDITION = new SpellDataType<>("entity_condition",
			EntityCondition.class, () -> EntityCondition.ALWAYS_TRUE, (e) -> e.serializeNBT(),
			(b) -> EntityCondition.fromNBT((CompoundNBT) b));
	public static final SpellDataType<ServerPos> POSITION = new SpellDataType<>("position", ServerPos.class,
			() -> new ServerPos(0, 0, 0, 0), (e) -> ServerPos.toNBT(e), (b) -> ServerPos.fromNBT((CompoundNBT) b));

	public static final SpellDataType<Vec3d> VECTOR = new SpellDataType<>("vector", Vec3d.class,
			() -> new Vec3d(0, 0, 0), (e) -> ServerPos.serializeVec3d(e, NBTDynamicOps.INSTANCE),
			(b) -> ServerPos.deserializeVec3d(GMNBT.makeDynamic(b)));
	public static final SpellDataType<ItemStack> ITEM = new SpellDataType<>("item", ItemStack.class,
			() -> ItemStack.EMPTY, (e) -> e.serializeNBT(), (b) -> ItemStack.read((CompoundNBT) b));
	public static final SpellDataType<BlockInfo> BLOCK = new SpellDataType<>("block", BlockInfo.class, () -> null,
			(e) -> e.serializeNBT(), (b) -> new BlockInfo((CompoundNBT) b));
	public static final SpellDataType<String> TEXT = new SpellDataType<>("text", String.class, () -> "",
			(e) -> StringNBT.valueOf(e), (b) -> ((StringNBT) b).getString());
	public static final SpellDataType<Direction> DIRECTION = new SpellDataType<>("direction", Direction.class,
			() -> Direction.NORTH, (e) -> IntNBT.valueOf(e.getIndex()),
			(b) -> Direction.values()[((IntNBT) b).getInt()]);

	private static final Map<String, SpellDataType<?>> TYPES = new HashMap<>();
	private Class<T> type;
	private Supplier<T> defaultVal;
	private Function<T, INBT> serializer;
	private BiFunction<Spellform, INBT, T> deserializer;
	private String name;

	private SpellDataType(String name, Class<T> clazz, Supplier<T> defaultValue, Function<T, INBT> serializer,
			Function<INBT, T> deserializer) {
		this(name, clazz, defaultValue, serializer, (world, inbt) -> deserializer.apply(inbt));
	}

	private SpellDataType(String name, Class<T> clazz, Supplier<T> defaultValue, Function<T, INBT> serializer,
			BiFunction<Spellform, INBT, T> deserializer) {
		this.type = clazz;
		this.defaultVal = defaultValue;
		this.serializer = serializer;
		this.deserializer = deserializer;
		this.name = name;
		TYPES.put(name, this);
	}

	public String getName() {
		return name;
	}

	public TranslationTextComponent getDisplayText() {
		return Translate.make("datatype." + getName());
	}

	public static <T> SpellDataType<T> fromName(String name) {
		return (SpellDataType<T>) TYPES.get(name);
	}

	public static <T> SpellDataType<T> fromObject(T obj) {
		if (obj == null)
			return null;
		return (SpellDataType<T>) TYPES.values().stream().filter((e) -> e.instanceOf(obj)).findAny().orElse(null);
	}

	public static Collection<SpellDataType<?>> getTypes() {
		return TYPES.values();
	}

	public T getDefaultValue() {
		return defaultVal.get();
	}

	public INBT serialize(T object) {
		return serializer.apply(object);
	}

	public T deserialize(Spellform spell, INBT object) {
		return deserializer.apply(spell, object);
	}

	public static <T> SpellDataHolder<T> deserializeHolder(Spellform form, CompoundNBT nbt) {
		SpellDataType<T> type = fromName(nbt.getString("Type"));
		if (type == SpellDataType.LIST) {
			return (SpellDataHolder<T>) new SpellDataList(form, (ListNBT) nbt.get("Value"));
		}
		SpellDataHolder<T> holder = new SpellDataHolder<>(form, nbt.get("Value"));
		return holder;
	}

	public Class<T> getType() {
		return type;
	}

	public boolean instanceOf(Object o) {
		return this.type.isInstance(o);
	}

	public static class SpellDataHolder<T> {
		protected T value;
		protected SpellDataType<T> type;

		public static <T> SpellDataHolder<T> create(T value) {
			return create(value, null);
		}

		public static <T> SpellDataHolder<T> create(T value, @Nullable SpellDataType<T> type) {
			if (value instanceof Collection) {
				return (SpellDataHolder<T>) new SpellDataList((Collection<?>) value);
			} else {
				if (type == null) {
					return new SpellDataHolder<T>(value);
				} else {
					return new SpellDataHolder<T>(type, value);
				}
			}
		}

		private SpellDataHolder(T value) {
			this(Objects.requireNonNull(fromObject(value)), value);
		}

		private SpellDataHolder(SpellDataType<T> type, T value) {
			this.value = value;
			this.type = type;
		}

		private SpellDataHolder(Spellform spell, INBT nbt) {
			this.type = fromName(((CompoundNBT) nbt).getString("Type"));
			this.value = type.deserialize(spell, nbt);

		}

		public static <T> SpellDataHolder<T> fromNBT(Spellform spell, INBT nbt) {
			if (nbt instanceof ListNBT) {
				return (SpellDataHolder<T>) new SpellDataList(spell, (ListNBT) nbt);
			} else {
				return new SpellDataHolder<>(spell, nbt);
			}
		}

		public SpellDataType<T> getType() {
			return type;
		}

		public T getValue() {
			return value == null ? type.getDefaultValue() : value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public INBT serializeNBT() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("Type", type.name);
			nbt.put("Value", type.serialize(value));
			return nbt;
		}

	}

	public static class SpellDataList extends SpellDataHolder<List<SpellDataHolder<?>>>
			implements List<SpellDataHolder<?>> {

		public SpellDataList() {
			super(new ArrayList<SpellDataHolder<?>>());
		}

		public SpellDataList(Spellform form, ListNBT listnbt) {
			this();
			List<SpellDataHolder<?>> list = this.getValue();
			for (INBT nbt : listnbt) {
				if (nbt instanceof ListNBT) {
					list.add(new SpellDataList((ListNBT) nbt));
				} else if (nbt instanceof CompoundNBT) {
					SpellDataHolder<?> holder = SpellDataType.deserializeHolder(form, (CompoundNBT) nbt);
					list.add(holder);
				} else {
					throw new IllegalStateException("ListNBT contains a tag that cannot be turned into data : "
							+ nbt.getType() + nbt.getString());
				}
			}
		}

		public ListNBT serializeNBT() {
			ListNBT lnbt = new ListNBT();
			for (SpellDataHolder<?> par : getValue()) {
				if (par.getType() == LIST) {
					lnbt.add(((SpellDataList) par.getValue()).serializeNBT());
				} else {
					lnbt.add(par.serializeNBT());
				}
			}
			return lnbt;
		}

		public SpellDataList(Collection<?> list) {
			this();

			this.addAll(list.stream().map((e) -> new SpellDataHolder<>(e)).collect(Collectors.toList()));
		}

		@Override
		public int size() {
			return value.size();
		}

		@Override
		public boolean isEmpty() {
			return value.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return value.contains(o);
		}

		@Override
		public Iterator<SpellDataHolder<?>> iterator() {
			return value.iterator();
		}

		@Override
		public Object[] toArray() {
			return value.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return value.toArray(a);
		}

		@Override
		public boolean add(SpellDataHolder<?> e) {
			return value.add(e);
		}

		@Override
		public boolean remove(Object o) {
			return value.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return value.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends SpellDataHolder<?>> c) {
			return value.addAll(c);
		}

		@Override
		public boolean addAll(int index, Collection<? extends SpellDataHolder<?>> c) {
			return value.addAll(index, c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return value.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return value.retainAll(c);
		}

		@Override
		public void clear() {
			value.clear();
		}

		@Override
		public SpellDataHolder<?> get(int index) {
			return value.get(index);
		}

		@Override
		public SpellDataHolder<?> set(int index, SpellDataHolder<?> element) {
			return value.set(index, element);
		}

		@Override
		public void add(int index, SpellDataHolder<?> element) {
			value.add(index, element);
		}

		@Override
		public SpellDataHolder<?> remove(int index) {
			return value.remove(index);
		}

		@Override
		public int indexOf(Object o) {
			return value.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return value.lastIndexOf(o);
		}

		@Override
		public ListIterator<SpellDataHolder<?>> listIterator() {
			return value.listIterator();
		}

		@Override
		public ListIterator<SpellDataHolder<?>> listIterator(int index) {
			return value.listIterator(index);
		}

		@Override
		public List<SpellDataHolder<?>> subList(int fromIndex, int toIndex) {
			return value.subList(fromIndex, toIndex);
		}

	}
}
