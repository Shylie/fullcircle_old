package com.github.shylie.fullcircle.lang;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.shylie.fullcircle.FCPacketHandler;
import com.github.shylie.fullcircle.net.MessageAdditiveMotion;
import com.github.shylie.fullcircle.proxy.CommonProxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion.Mode;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class VM {
	private static class Value3 {
		public Value3(Value x, Value y, Value z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Value x;
		public Value y;
		public Value z;
	}

	private static final int STACK_MAX = 1024;
	
	private final boolean compiled;

	private StringBuilder log;

	private int ip;
	private Deque<Integer> callStack;
	private int stackTop;
	private Value[] stack;
	private Map<String, Value> registers;
	private Map<String, Value3> registers_3;
	private Chunk chunk;
	private PlayerEntity caster;

	private int delay;

	public final DimensionType dimension;

	public VM(String[] source, Map<String, String> strings, int cx, int cy, Direction startDirection, PlayerInteractEvent event, DimensionType dimension) {
		log = new StringBuilder();

		chunk = new Chunk();
		ip = 0;
		callStack = new ArrayDeque<>();
		stack = new Value[STACK_MAX];
		registers = new HashMap<>();
		registers_3 = new HashMap<>();
		this.dimension = dimension;
		caster = event.getPlayer();

		compiled = Compiler.COMPILER.Compile(source, strings, cx, cy, startDirection, event, chunk, log);

		delay = 0;
	}

	public InterpretResult run(WorldTickEvent event) {
		if (!compiled) {
			writeLog("Compilation error");
			return InterpretResult.COMPILE_ERROR;
		}

		// maybe limit amount of instructions run to prevent infinite loops?

		writeLog("");
		for (int i = 0; i < stackTop; i++) {
			writeLog("stack[%d] = %s", i, stack[i]);
		}
		chunk.dissasembleInstruction(log, ip);

		int instruction = readInt();
		switch (instruction) {
			case OpCode.RETURN:
				return InterpretResult.OK;

			case OpCode.CONSTANT:
				push(chunk.readConstant(readInt()));
				return InterpretResult.CONTINUE;

			case OpCode.NEGATE:
			{
				Value a = pop();
				if (a == null) {
					writeLog("Null on stack at OP_NEGATE at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(a.neg());
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.NEGATE_3:
			{
				Value z = pop();
				Value y = pop();
				Value x = pop();
				if (x == null || y == null || z == null) {
					writeLog("Null on stack at OP_NEGATE_3 at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(x.neg());
					push(y.neg());
					push(z.neg());
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.ADD:
			{
				Value b = pop();
				Value a = pop();
				if (a == null) {
					writeLog("Null on stack at OP_ADD at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(a.add(b));
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.ADD_3:
			{
				Value z2 = pop();
				Value y2 = pop();
				Value x2 = pop();
				Value z1 = pop();
				Value y1 = pop();
				Value x1 = pop();
				if (x1 == null || y1 == null || z1 == null) {
					writeLog("Null on stack at OP_ADD_3 at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(x1.add(x2));
					push(y1.add(y2));
					push(z1.add(z2));
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.SUBTRACT:
			{
				Value b = pop();
				Value a = pop();
				if (a == null) {
					writeLog("Null on stack at OP_SUBTRACT at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(a.sub(b));
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.SUBTRACT_3:
			{
				Value z2 = pop();
				Value y2 = pop();
				Value x2 = pop();
				Value z1 = pop();
				Value y1 = pop();
				Value x1 = pop();
				if (x1 == null || y1 == null || z1 == null) {
					writeLog("Null on stack at OP_SUBTRACT_3 at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(x1.sub(x2));
					push(y1.sub(y2));
					push(z1.sub(z2));
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.MULTIPLY:
			{
				Value b = pop();
				Value a = pop();
				if (a == null) {
					writeLog("Null on stack at OP_MULTIPLY at instruction address %04d", ip);
				}
				else {
					push(a.mul(b));
				}
				return InterpretResult.CONTINUE;
			}

			case OpCode.MULTIPLY_3:
			{
				Value z2 = pop();
				Value y2 = pop();
				Value x2 = pop();
				Value z1 = pop();
				Value y1 = pop();
				Value x1 = pop();
				if (x1 == null || y1 == null || z1 == null) {
					writeLog("Null on stack at OP_MULTIPLY_3 at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(x1.mul(x2));
					push(y1.mul(y2));
					push(z1.mul(z2));
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.DIVIDE:
			{
				Value b = pop();
				Value a = pop();
				if (a == null) {
					writeLog("Null on stack at OP_DIVIDE at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(a.div(b));
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.DIVIDE_3:
			{
				Value z2 = pop();
				Value y2 = pop();
				Value x2 = pop();
				Value z1 = pop();
				Value y1 = pop();
				Value x1 = pop();
				if (x1 == null || y1 == null || z1 == null) {
					writeLog("Null on stack at OP_DIVIDE_3 at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(x1.div(x2));
					push(y1.div(y2));
					push(z1.div(z2));
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.DUPLICATE:
			{
				Value v = pop();
				if (v == null) {
					writeLog("Null on stack at OP_DUPLICATE at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					push(v);
					push(v.dup());
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.DUPLICATE_3:
			{
				Value z = pop();
				Value y = pop();
				Value x = pop();
				if (x == null || y == null || z == null) { return InterpretResult.RUNTIME_ERROR; }
				push(x);
				push(y);
				push(z);
				push(x.dup());
				push(y.dup());
				push(z.dup());
				return InterpretResult.CONTINUE;
			}

			case OpCode.POW:
			{
				NumberValue b = checkType(pop(), NumberValue.class);
				if (b == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue a = checkType(pop(), NumberValue.class);
				if (a == null) { return InterpretResult.RUNTIME_ERROR; }

				push(new DoubleValue(Math.pow(a.asDouble(), b.asDouble())));
				return InterpretResult.CONTINUE;
			}

			case OpCode.SIN:
			{
				NumberValue v = checkType(pop(), NumberValue.class);
				if (v == null) { return InterpretResult.RUNTIME_ERROR; }

				push(new DoubleValue(Math.sin(v.asDouble())));
				return InterpretResult.CONTINUE;
			}

			case OpCode.COS:
			{
				NumberValue v = checkType(pop(), NumberValue.class);
				if (v == null) { return InterpretResult.RUNTIME_ERROR; }

				push(new DoubleValue(Math.cos(v.asDouble())));
				return InterpretResult.CONTINUE;
			}

			case OpCode.TAN:
			{
				NumberValue v = checkType(pop(), NumberValue.class);
				if (v == null) { return InterpretResult.RUNTIME_ERROR; }

				push(new DoubleValue(Math.tan(v.asDouble())));
				return InterpretResult.CONTINUE;
			}

			case OpCode.MOD:
			{
				NumberValue b = checkType(pop(), NumberValue.class);
				if (b == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue a = checkType(pop(), NumberValue.class);
				if (a == null) { return InterpretResult.RUNTIME_ERROR; }

				push(new DoubleValue(a.asDouble() % b.asDouble()));
				return InterpretResult.CONTINUE;
			}

			case OpCode.TRUNCATE:
			{
				Value value = pop();
				if (value == null) {
					writeLog("Null on stack at OP_TRUNCATE at instruction address %04d", ip);
					return InterpretResult.RUNTIME_ERROR;
				}

				if (value instanceof LongValue) {
					push(value);
				}
				else if (value instanceof DoubleValue) {
					long result = (long)Math.floor(((DoubleValue)value).value);
					if (result < 0) {
						// account for floor rounding towards negative infinity, whereas truncating rounds towarads zero
						push(new LongValue(result + 1));
					}
					else {
						push(new LongValue(result));
					}
				}
				else {
					writeLog("Expected value to be of type LongValue or DoubleValue, but got '%s'", value != null ? value.getClass().getSimpleName() : null);
					return InterpretResult.RUNTIME_ERROR;
				}

				return InterpretResult.CONTINUE;
			}

			case OpCode.LOAD:
			{
				StringValue loc = checkType(pop(), StringValue.class);
				if (loc == null) { return InterpretResult.RUNTIME_ERROR; }
				push(registers.get(loc.value));
				return InterpretResult.CONTINUE;
			}

			case OpCode.LOAD_3:
			{
				StringValue loc = checkType(pop(), StringValue.class);
				if (loc == null) { return InterpretResult.RUNTIME_ERROR; }
				Value3 vec = registers_3.get(loc.value);
				push(vec.x);
				push(vec.y);
				push(vec.z);
				return InterpretResult.CONTINUE;
			}

			case OpCode.STORE:
			{
				StringValue loc = checkType(pop(), StringValue.class);
				if (loc == null) { return InterpretResult.RUNTIME_ERROR; }
				Value toStore = pop();
				registers.put(loc.value, toStore);
				return InterpretResult.CONTINUE;
			}

			case OpCode.STORE_3:
			{
				StringValue loc = checkType(pop(), StringValue.class);
				if (loc == null) { return InterpretResult.RUNTIME_ERROR; }
				Value toStoreZ = pop();
				Value toStoreY = pop();
				Value toStoreX = pop();
				registers_3.put(loc.value, new Value3(toStoreX, toStoreY, toStoreZ));
				return InterpretResult.CONTINUE;
			}

			case OpCode.JUMP_IF_NEGATIVE:
			{
				int jmpto = readInt();
				if (checkType(peek(0), NumberValue.class) != null && checkType(peek(0), NumberValue.class).asLong() < 0) {
					ip = jmpto;
				}
				return InterpretResult.CONTINUE;
			}

			case OpCode.JUMP_IF_POSITIVE:
			{
				int jmpto = readInt();
				if (checkType(peek(0), NumberValue.class) != null && checkType(peek(0), NumberValue.class).asLong() > 0) {
					ip = jmpto;
				}
				return InterpretResult.CONTINUE;
			}

			case OpCode.JUMP:
			{
				ip = readInt();
				return InterpretResult.CONTINUE;
			}

			case OpCode.JUMP_TO_CALLEE:
			{
				if (callStack.size() == 0) {
					writeLog("Call stack underflow");
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					ip = callStack.pop();
					return InterpretResult.CONTINUE;
				}
			}

			case OpCode.CALL:
			{
				StringValue functionName = checkType(pop(), StringValue.class);
				if (functionName == null) { return InterpretResult.RUNTIME_ERROR; }

				if (callStack.size() >= 4096) {
					writeLog("Call stack overflow");
					return InterpretResult.RUNTIME_ERROR;
				}
				else {
					callStack.push(ip);
					Optional<Integer> functionAddress = chunk.getFunctionAddress(functionName.value);
					if (functionAddress.isPresent()) {
						ip = functionAddress.get();
						return InterpretResult.CONTINUE;
					}
					else {
						writeLog("Undefined function '%s'", functionName.value);
						return InterpretResult.RUNTIME_ERROR;
					}
				}
			}

			case OpCode.POP:
			{
				pop();
				return InterpretResult.CONTINUE;
			}

			case OpCode.COMPARE:
			{
				Value b = pop();
				Value a = pop();

				if (a == null || b == null) {
					writeLog("Null on stack at OP_COMPARE at instruction address %04d", ip);
				}

				push(a.cmp(b));
				return InterpretResult.CONTINUE;
			}

			case OpCode.RAYCAST_BLOCKPOS:
			{
				NumberValue lz = checkType(pop(), NumberValue.class);
				if (lz == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue ly = checkType(pop(), NumberValue.class);
				if (ly == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue lx = checkType(pop(), NumberValue.class);
				if (lx == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue sz = checkType(pop(), NumberValue.class);
				if (sz == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue sy = checkType(pop(), NumberValue.class);
				if (sy == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue sx = checkType(pop(), NumberValue.class);
				if (sx == null) { return InterpretResult.RUNTIME_ERROR; }

				BlockRayTraceResult result = event.world.rayTraceBlocks(
					new RayTraceContext(
						new Vector3d(sx.asDouble(), sy.asDouble(), sz.asDouble()),
						new Vector3d(sx.asDouble() + lx.asDouble() * 512.0, sy.asDouble() + ly.asDouble() * 512.0, sz.asDouble() + lz.asDouble() * 512.0),
						BlockMode.OUTLINE,
						FluidMode.NONE,
						null
					)
				);
				if (result.getType() == RayTraceResult.Type.MISS) {
					push(new LongValue(1));
				}
				else {
					push(new DoubleValue(result.getPos().getX()));
					push(new DoubleValue(result.getPos().getY()));
					push(new DoubleValue(result.getPos().getZ()));
					push(new LongValue(0));
				}
				return InterpretResult.CONTINUE;
			}

			case OpCode.RAYCAST_BLOCKSIDE:
			{
				NumberValue lz = checkType(pop(), NumberValue.class);
				if (lz == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue ly = checkType(pop(), NumberValue.class);
				if (ly == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue lx = checkType(pop(), NumberValue.class);
				if (lx == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue sz = checkType(pop(), NumberValue.class);
				if (sz == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue sy = checkType(pop(), NumberValue.class);
				if (sy == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue sx = checkType(pop(), NumberValue.class);
				if (sx == null) { return InterpretResult.RUNTIME_ERROR; }

				BlockRayTraceResult result = event.world.rayTraceBlocks(
					new RayTraceContext(
						new Vector3d(sx.asDouble(), sy.asDouble(), sz.asDouble()),
						new Vector3d(sx.asDouble() + lx.asDouble() * 512.0, sy.asDouble() + ly.asDouble() * 512.0, sz.asDouble() + lz.asDouble() * 512.0),
						BlockMode.OUTLINE,
						FluidMode.NONE,
						null
					)
				);
				if (result.getType() == RayTraceResult.Type.MISS) {
					push(new LongValue(1));
				}
				else {
					push(new DoubleValue(result.getFace().getXOffset()));
					push(new DoubleValue(result.getFace().getYOffset()));
					push(new DoubleValue(result.getFace().getZOffset()));
					push(new LongValue(0));
				}
				return InterpretResult.CONTINUE;
			}

			case OpCode.ENTITY_POS:
			{
				EntityValue ev = checkType(pop(), EntityValue.class);
				if (ev == null) { return InterpretResult.RUNTIME_ERROR; }
				Entity entity = event.world.getEntityByID(ev.entityID);
				if (entity == null) { return InterpretResult.RUNTIME_ERROR; }
				push(new DoubleValue(entity.getPosX()));
				push(new DoubleValue(entity.getPosY()));
				push(new DoubleValue(entity.getPosZ()));
				return InterpretResult.CONTINUE;
			}

			case OpCode.ENTITY_EYE_POS:
			{
				EntityValue ev = checkType(pop(), EntityValue.class);
				if (ev == null) { return InterpretResult.RUNTIME_ERROR; }
				Entity entity = event.world.getEntityByID(ev.entityID);
				if (entity == null) { return InterpretResult.RUNTIME_ERROR; }
				push(new DoubleValue(entity.getPosX()));
				push(new DoubleValue(entity.getPosYEye()));
				push(new DoubleValue(entity.getPosZ()));
				return InterpretResult.CONTINUE;
			}

			case OpCode.ENTITY_LOOK:
			{
				EntityValue ev = checkType(pop(), EntityValue.class);
				if (ev == null) { return InterpretResult.RUNTIME_ERROR; }
				Entity entity = event.world.getEntityByID(ev.entityID);
				if (entity == null) { return InterpretResult.RUNTIME_ERROR; }
				push(new DoubleValue(entity.getLookVec().x));
				push(new DoubleValue(entity.getLookVec().y));
				push(new DoubleValue(entity.getLookVec().z));
				return InterpretResult.CONTINUE;
			}

			case OpCode.ENTITY_LOOKED_AT:
			{
				EntityValue ev = checkType(pop(), EntityValue.class);
				if (ev == null) { return InterpretResult.RUNTIME_ERROR; }

				Entity entity = event.world.getEntityByID(ev.entityID);
				if (entity == null) { return InterpretResult.RUNTIME_ERROR; }

				Vector3d start = new Vector3d(entity.getPosX(), entity.getPosYEye(), entity.getPosZ());
				Vector3d end = start.add(entity.getLookVec().normalize().scale(512.0));

				BlockRayTraceResult result = event.world.rayTraceBlocks(
					new RayTraceContext(
						start,
						end,
						BlockMode.VISUAL,
						FluidMode.NONE,
						null
					)
				);

				Entity entityLooked = null;
				Entity entityFound = null;

				List<Entity> entitiesInBoundingBox = event.world.getEntitiesWithinAABBExcludingEntity(entity, entity.getBoundingBox().grow(
						entity.getLookVec().x * 512.0,
						entity.getLookVec().y * 512.0,
						entity.getLookVec().z * 512.0).
					grow(1.0)
				);

				double minDistance = 512.0;

				for (Entity e : entitiesInBoundingBox) {
					if (e.canBeCollidedWith()) {
						float collisionBorderSize = entity.getCollisionBorderSize();
						AxisAlignedBB hitbox = e.getBoundingBox().grow(collisionBorderSize);
						Optional<Vector3d> interceptPosition = hitbox.rayTrace(start, end);

						if (interceptPosition.isPresent()) {
							double distanceToEntity = start.distanceTo(interceptPosition.get());

							if (distanceToEntity < minDistance || distanceToEntity == 0.0) {
								entityLooked = e;
								minDistance = distanceToEntity;
							}
						}

						if (entityLooked != null && (minDistance < 512.0 || result == null)) {
							entityFound = entityLooked;
						}
					}
				}

				if (entityFound != null) {
					push(new EntityValue(entityFound.getEntityId()));
					push(new LongValue(0));
				}
				else {
					push(new LongValue(1));
				}
				return InterpretResult.CONTINUE;
			}

			case OpCode.ENTITY_NBT:
			{
				EntityValue ev = checkType(pop(), EntityValue.class);
				if (ev == null) { return InterpretResult.RUNTIME_ERROR; }
				Entity e = event.world.getEntityByID(ev.entityID);
				if (e == null) { return InterpretResult.RUNTIME_ERROR; }

				CompoundNBT nbt = new CompoundNBT();
				e.writeUnlessRemoved(nbt);
				push(new NBTValue(nbt));
				return InterpretResult.CONTINUE;
			}

			case OpCode.ADD_MOTION:
			{
				NumberValue z = checkType(pop(), NumberValue.class);
				if (z == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue y = checkType(pop(), NumberValue.class);
				if (y == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue x = checkType(pop(), NumberValue.class);
				if (x == null) { return InterpretResult.RUNTIME_ERROR; }
				EntityValue ev = checkType(pop(), EntityValue.class);
				if (ev == null) { return InterpretResult.RUNTIME_ERROR; }

				Entity entity = event.world.getEntityByID(ev.entityID);

				MessageAdditiveMotion motion = new MessageAdditiveMotion(entity.getEntityId(), x.asDouble(), y.asDouble(), z.asDouble());
				if (entity instanceof ServerPlayerEntity) {
					FCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)entity), motion);
				}
				else {
					entity.addVelocity(x.asDouble(), y.asDouble(), z.asDouble());
					FCPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> event.world.getDimensionKey()), motion);
				}

				return InterpretResult.CONTINUE;
			}

			case OpCode.SPAWN_ENTITY:
			{
				StringValue pathValue = checkType(pop(), StringValue.class);
				if (pathValue == null) { return InterpretResult.RUNTIME_ERROR; }
				StringValue namespaceValue = checkType(pop(), StringValue.class);
				if (namespaceValue == null) { return InterpretResult.RUNTIME_ERROR; }

				NumberValue z = checkType(pop(), NumberValue.class);
				if (z == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue y = checkType(pop(), NumberValue.class);
				if (y == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue x = checkType(pop(), NumberValue.class);
				if (x == null) { return InterpretResult.RUNTIME_ERROR; }

				final ResourceLocation resourceLocation = new ResourceLocation(namespaceValue.value, pathValue.value);
				if (!ForgeRegistries.ENTITIES.containsKey(resourceLocation)) {
					return InterpretResult.RUNTIME_ERROR;
				}
				Entity e = ForgeRegistries.ENTITIES.getValue(resourceLocation).create(event.world);
				e.setPosition(x.asDouble(), y.asDouble(), z.asDouble());
				event.world.addEntity(e);
				push(new EntityValue(e.getEntityId()));
				return InterpretResult.CONTINUE;
			}

			case OpCode.CREATE_EXPLOSION:
			{
				NumberValue radius = checkType(pop(), NumberValue.class);
				if (radius == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue z = checkType(pop(), NumberValue.class);
				if (z == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue y = checkType(pop(), NumberValue.class);
				if (y == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue x = checkType(pop(), NumberValue.class);
				if (x == null) { return InterpretResult.RUNTIME_ERROR; }

				event.world.createExplosion(null, x.asDouble(), y.asDouble(), z.asDouble(), (float)radius.asDouble(), Mode.NONE);
				return InterpretResult.CONTINUE;
			}

			case OpCode.PAUSE:
			{
				NumberValue pauseTime = checkType(pop(), NumberValue.class);
				if (pauseTime == null) { return InterpretResult.RUNTIME_ERROR; }

				delay += pauseTime.asLong();
				return InterpretResult.CONTINUE;
			}

			case OpCode.MODIFY_ENTITY_NBT:
			{
				NBTValue nbtv = checkType(pop(), NBTValue.class);
				if (nbtv == null) { return InterpretResult.RUNTIME_ERROR; }
				EntityValue ev = checkType(pop(), EntityValue.class);
				if (ev == null) { return InterpretResult.RUNTIME_ERROR; }

				Entity e = event.world.getEntityByID(ev.entityID);
				if (e == null) {
					return InterpretResult.RUNTIME_ERROR;
				}

				e.read(nbtv.value);

				return InterpretResult.CONTINUE;
			}

			case OpCode.GET_BLOCK:
			{
				NumberValue z = checkType(pop(), NumberValue.class);
				if (z == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue y = checkType(pop(), NumberValue.class);
				if (y == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue x = checkType(pop(), NumberValue.class);
				if (x == null) { return InterpretResult.RUNTIME_ERROR; }

				final BlockPos pos = new BlockPos(x.asDouble(), y.asDouble(), z.asDouble());
				push(new BlockStateValue(event.world.getBlockState(pos)));
				return InterpretResult.CONTINUE;
			}

			case OpCode.SET_BLOCK:
			{
				BlockStateValue stateValue = checkType(pop(), BlockStateValue.class);
				if (stateValue == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue z = checkType(pop(), NumberValue.class);
				if (z == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue y = checkType(pop(), NumberValue.class);
				if (y == null) { return InterpretResult.RUNTIME_ERROR; }
				NumberValue x = checkType(pop(), NumberValue.class);
				if (x == null) { return InterpretResult.RUNTIME_ERROR; }

				final BlockPos pos = new BlockPos(x.asDouble(), y.asDouble(), z.asDouble());
				final BlockState state = event.world.getBlockState(pos);

				if (!event.world.isBlockModifiable(caster, pos)) {
					push(new LongValue(1));
					return InterpretResult.CONTINUE;
				}

				if (!caster.isCreative()) {
					BreakEvent breakEvent = new BlockEvent.BreakEvent(event.world, pos, state, caster);
					if (MinecraftForge.EVENT_BUS.post(breakEvent)) {
						push(new LongValue(1));
						return InterpretResult.CONTINUE;
					}
				}

				final BlockState newState = Block.getValidBlockForPosition(stateValue.getState(), event.world, pos);
				event.world.setBlockState(pos, newState, 1 | 2);
				push(new LongValue(0));
				return InterpretResult.CONTINUE;
			}

			case OpCode.NEW_NBT:
			{
				push(new NBTValue(new CompoundNBT()));
				return InterpretResult.CONTINUE;
			}

			case OpCode.NBT_GET:
			{
				StringValue ty = checkType(pop(), StringValue.class);
				if (ty == null) { return InterpretResult.RUNTIME_ERROR; }
				StringValue path = checkType(pop(), StringValue.class);
				if (path == null) { return InterpretResult.RUNTIME_ERROR; }
				NBTValue nbtv = checkType(pop(), NBTValue.class);
				if (nbtv == null) { return InterpretResult.RUNTIME_ERROR; }

				String[] splitPath = path.value.split("\\.");

				CompoundNBT nbt = nbtv.value;
				for (int i = 0; i < splitPath.length - 1; i++) {
					if (!nbt.contains(splitPath[i])) {
						return InterpretResult.RUNTIME_ERROR;
					}

					final INBT inbt = nbt.get(splitPath[i]);

					if (!inbt.getType().equals(CompoundNBT.TYPE)) {
						return InterpretResult.RUNTIME_ERROR;
					}
					else {
						nbt = (CompoundNBT)inbt;
					}
				}

				final INBT inbt = nbt.get(splitPath[splitPath.length - 1]);

				switch (ty.value) {
					case "long":
						if (inbt.getType().equals(ByteNBT.TYPE)) {
							push(new LongValue(((ByteNBT)inbt).getLong()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(ShortNBT.TYPE)) {
							push(new LongValue(((ShortNBT)inbt).getLong()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(IntNBT.TYPE)) {
							push(new LongValue(((IntNBT)inbt).getLong()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(LongNBT.TYPE)) {
							push(new LongValue(((LongNBT)inbt).getLong()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(FloatNBT.TYPE)) {
							push(new LongValue(((FloatNBT)inbt).getLong()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(DoubleNBT.TYPE)) {
							push(new LongValue(((DoubleNBT)inbt).getLong()));
							return InterpretResult.CONTINUE;
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}

					case "double":
						if (inbt.getType().equals(ByteNBT.TYPE)) {
							push(new DoubleValue(((ByteNBT)inbt).getDouble()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(ShortNBT.TYPE)) {
							push(new DoubleValue(((ShortNBT)inbt).getDouble()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(IntNBT.TYPE)) {
							push(new DoubleValue(((IntNBT)inbt).getDouble()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(LongNBT.TYPE)) {
							push(new DoubleValue(((LongNBT)inbt).getDouble()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(FloatNBT.TYPE)) {
							push(new DoubleValue(((FloatNBT)inbt).getDouble()));
							return InterpretResult.CONTINUE;
						}
						else if (inbt.getType().equals(DoubleNBT.TYPE)) {
							push(new DoubleValue(((DoubleNBT)inbt).getDouble()));
							return InterpretResult.CONTINUE;
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}

					case "string":
						if (inbt.getType().equals(StringNBT.TYPE)) {
							push(new StringValue(((StringNBT)inbt).getString()));
							return InterpretResult.CONTINUE;
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}

					case "nbt":
						if (inbt.getType().equals(CompoundNBT.TYPE)) {
							push(new NBTValue((CompoundNBT)inbt));
							return InterpretResult.CONTINUE;
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}

					default:
						return InterpretResult.RUNTIME_ERROR;
				}
			}

			case OpCode.NBT_SET:
			{
				Value putv = pop();
				StringValue putvtype = checkType(pop(), StringValue.class);
				StringValue path = checkType(pop(), StringValue.class);
				NBTValue nbtv = checkType(peek(0), NBTValue.class);

				if (putv instanceof BlockStateValue) {
					writeLog("Cannot store block states in NBT");
					return InterpretResult.RUNTIME_ERROR;
				}

				String puttype = ((StringValue)putvtype).value;

				String[] splitPath = ((StringValue)path).value.split("\\.");

				CompoundNBT nbt = ((NBTValue)nbtv).value;

				for (int i = 0; i < splitPath.length - 1; i++) {
					if (!nbt.contains(splitPath[i], 10)) {
						return InterpretResult.RUNTIME_ERROR;
					}

					nbt = nbt.getCompound(splitPath[i]);
				}

				switch (puttype.toLowerCase()) {
					case "byte":
						if (putv instanceof LongValue) {
							nbt.putByte(splitPath[splitPath.length - 1], (byte)((LongValue)putv).value);
						}
						else if (putv instanceof DoubleValue) {
							nbt.putByte(splitPath[splitPath.length - 1], (byte)((DoubleValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "short":
						if (putv instanceof LongValue) {
							nbt.putShort(splitPath[splitPath.length - 1], (short)((LongValue)putv).value);
						}
						else if (putv instanceof DoubleValue) {
							nbt.putShort(splitPath[splitPath.length - 1], (short)((DoubleValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "int":
						if (putv instanceof LongValue) {
							nbt.putInt(splitPath[splitPath.length - 1], (int)((LongValue)putv).value);
						}
						else if (putv instanceof DoubleValue) {
							nbt.putInt(splitPath[splitPath.length - 1], (int)((DoubleValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "long":
						if (putv instanceof LongValue) {
							nbt.putLong(splitPath[splitPath.length - 1], (long)((LongValue)putv).value);
						}
						else if (putv instanceof DoubleValue) {
							nbt.putLong(splitPath[splitPath.length - 1], (long)((DoubleValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "float":
						if (putv instanceof LongValue) {
							nbt.putFloat(splitPath[splitPath.length - 1], (float)((LongValue)putv).value);
						}
						else if (putv instanceof DoubleValue) {
							nbt.putFloat(splitPath[splitPath.length - 1], (float)((DoubleValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "double":
						if (putv instanceof LongValue) {
							nbt.putDouble(splitPath[splitPath.length - 1], (double)((LongValue)putv).value);
						}
						else if (putv instanceof DoubleValue) {
							nbt.putDouble(splitPath[splitPath.length - 1], (double)((DoubleValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "string":
						if (putv instanceof StringValue) {
							nbt.putString(splitPath[splitPath.length - 1], ((StringValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "entity":
						if (putv instanceof EntityValue) {
							nbt.putUniqueId(splitPath[splitPath.length - 1], event.world.getEntityByID(((EntityValue)putv).entityID).getUniqueID());
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					case "nbt":
						if (putv instanceof NBTValue) {
							nbt.put(splitPath[splitPath.length - 1], ((NBTValue)putv).value);
						}
						else {
							return InterpretResult.RUNTIME_ERROR;
						}
						break;

					default:
						return InterpretResult.RUNTIME_ERROR;
				}

				return InterpretResult.CONTINUE;
			}

			case OpCode.DEFAULT_BLOCK_STATE:
			{
				StringValue pathValue = checkType(pop(), StringValue.class);
				if (pathValue == null) { return InterpretResult.RUNTIME_ERROR; }
				StringValue namespaceValue = checkType(pop(), StringValue.class);
				if (namespaceValue == null) { return InterpretResult.RUNTIME_ERROR; }

				final ResourceLocation rl = new ResourceLocation(namespaceValue.value, pathValue.value);
				if (ForgeRegistries.BLOCKS.containsKey(rl)) {
					push(new BlockStateValue(ForgeRegistries.BLOCKS.getValue(rl).getDefaultState()));
					return InterpretResult.CONTINUE;
				}
				else {
					return InterpretResult.RUNTIME_ERROR;
				}
			}

			case OpCode.BLOCK_STATE_GET:
			{
				StringValue propertyNameValue = checkType(pop(), StringValue.class);
				if (propertyNameValue == null) { return InterpretResult.RUNTIME_ERROR; }
				BlockStateValue bsv = checkType(peek(0), BlockStateValue.class);
				if (bsv == null) { return InterpretResult.RUNTIME_ERROR; }

				Optional<Long> longProp = bsv.getPropertyAsLong(propertyNameValue.value);
				if (longProp.isPresent()) {
					push(new LongValue(longProp.get()));
					return InterpretResult.CONTINUE;
				}

				Optional<String> stringProp = bsv.getPropertyAsString(propertyNameValue.value);
				if (stringProp.isPresent()) {
					push(new StringValue(stringProp.get()));
					push(new LongValue(0));
				}
				else {
					push(new LongValue(1));
				}
				
				return InterpretResult.CONTINUE;
			}

			case OpCode.BLOCK_STATE_SET:
			{
				Value newValueV = pop();
				StringValue propertyNameValue = checkType(pop(), StringValue.class);
				if (propertyNameValue == null) { return InterpretResult.RUNTIME_ERROR; }
				BlockStateValue bsv = checkType(peek(0), BlockStateValue.class);
				if (bsv == null) { return InterpretResult.RUNTIME_ERROR; }

				if ((!(newValueV instanceof StringValue) && !(newValueV instanceof NumberValue))) {
					writeLog("Expected StringValue or NumberValue, got '%s' instead", newValueV != null ? newValueV.getClass().getSimpleName() : "null");
					return InterpretResult.RUNTIME_ERROR;
				}

				// test for number and convert to string, otherwise get stringvalue's value
				// .toString is avoided so that doubles are rounded
				String newValue = Optional.ofNullable(checkType(newValueV, LongValue.class, false)).map(l -> Long.toString(l.value)).orElseGet(() -> ((StringValue)newValueV).value);

				push(new LongValue(bsv.modifyProperty(propertyNameValue.value, newValue) ? 0L : 1L));
				return InterpretResult.CONTINUE;
			}

			default:
				writeLog("Unknown OpCode '%04d' at instruction address %04d", instruction, ip);
				return InterpretResult.RUNTIME_ERROR;
		}
	}

	public PlayerEntity getCaster() {
		return caster;
	}

	public List<String> getLog() {
		List<String> strings = new ArrayList<>();
		for (int i = 0; i < log.length(); i += CommonProxy.STRING_CHUNK_LENGTH) {
			int end = (i + CommonProxy.STRING_CHUNK_LENGTH >= log.length() ? log.length() - 1 : i + CommonProxy.STRING_CHUNK_LENGTH);
			strings.add(log.substring(i, end));
		}
		return strings;
	}

	public boolean delay() {
		boolean ret = delay > 0;
		if (ret) { delay--; }
		return ret;
	}

	private int readInt() {
		return chunk.read(ip++);
	}

	private void resetStack() {
		stackTop = 0;
		for (int i = 0; i < STACK_MAX; i++) {
			stack[i] = null;
		}
	}

	private void push(Value value) {
		if (stackTop >= STACK_MAX) {
			writeLog("Stack overflow at instruction address %04d", ip);
		}
		else {
			stack[stackTop++] = value;
		}
	}

	private Value pop() {
		if (stackTop <= 0) {
			writeLog("Stack underflow at instruction address %04d", ip);
			return null;
		}
		else {
			stackTop--;
			Value ret = stack[stackTop];
			stack[stackTop] = null;
			return ret;
		}
	}

	private Value peek(int offset) {
		if (stackTop - offset - 1 < 0 || stackTop - offset - 1 >= stack.length) {
			return null;
		}
		else {
			return stack[stackTop - offset - 1];
		}
	}

	private <T extends Value> T checkType(Value value, Class<T> clazz) {
		return checkType(value, clazz, true);
	}

	private <T extends Value> T checkType(Value value, Class<T> clazz, boolean log) {
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}
		else {
			if (log) {
				writeLog("Expected type %s, got %s instead.", clazz.getSimpleName(), value != null ? value.getClass().getSimpleName() : "null");
			}
			return null;
		}
	}

	private void writeLog(String message, Object... objects) {
		log.append(String.format(message, objects) + "\n");
	}
}