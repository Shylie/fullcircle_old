package com.github.shylie.fullcircle.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

public class Chunk {
	private List<Integer> code;
	private List<Value> constants;
	private Map<String, Integer> functionAddresses;

	public Chunk() {
		code = new ArrayList<>();
		constants = new ArrayList<>();
		functionAddresses = new HashMap<>();
	}

	public void reset() {
		code.clear();
		constants.clear();
	}

	public int write(int data) {
		code.add(data);
		return code.size() - 1;
	}

	public void modify(int loc, int newdata) {
		code.set(loc, newdata);
	}

	public int size() {
		return code.size();
	}

	public int read(int loc) {
		if (loc >= code.size()) {
			return OpCode.RETURN; // failsafe
		}
		return code.get(loc);
	}

	public int last() {
		if (code.size() == 0) { return OpCode.RETURN; }
		return code.get(code.size() - 1);
	}

	public int writeConstant(Value constant) {
		for (int i = 0; i < constants.size(); i++) {
			if (constants.get(i).equals(constant)) { return i; }
		}
		constants.add(constant);
		return constants.size() - 1;
	}

	public Value readConstant(int loc) {
		return constants.get(loc);
	}

	public boolean addFunction(String name) {
		if (functionAddresses.containsKey(name)) {
			return false;
		}
		else {
			functionAddresses.put(name, code.size());
			return true;
		}
	}

	public Optional<Integer> getFunctionAddress(String functionName) {
		return Optional.ofNullable(functionAddresses.get(functionName));
	}

	public void dissasemble(StringBuilder log, String name) {
		writeLog(log, "== %s ==", name);

		for (int offset = 0; offset < code.size();) {
			offset = dissasembleInstruction(log, offset);
		}

		writeLog(log, "== %s ==", name.replaceAll(".", "="));
	}

	public int dissasembleInstruction(StringBuilder log, int offset) {
		if (code.size() <= offset) {
			return offset + 1;
		}
		int instruction = code.get(offset);
		switch (instruction) {
			case OpCode.RETURN:
				return simpleInstruction(log, "OP_RETURN", offset);

			case OpCode.CONSTANT:
				return constantInstruction(log, "OP_CONSTANT", offset);

			case OpCode.CONSTANT_LONG:
				return constantInstruction(log, "OP_CONSTANT_LONG", offset);

			case OpCode.NEGATE:
				return simpleInstruction(log, "OP_NEGATE", offset);

			case OpCode.NEGATE_3:
				return simpleInstruction(log, "OP_NEGATE_3", offset);

			case OpCode.ADD:
				return simpleInstruction(log, "OP_ADD", offset);

			case OpCode.ADD_3:
				return simpleInstruction(log, "OP_ADD_3", offset);

			case OpCode.SUBTRACT:
				return simpleInstruction(log, "OP_SUBTRACT", offset);

			case OpCode.SUBTRACT_3:
				return simpleInstruction(log, "OP_SUBTRACT_3", offset);

			case OpCode.MULTIPLY:
				return simpleInstruction(log, "OP_MULTIPLY", offset);

			case OpCode.MULTIPLY_3:
				return simpleInstruction(log, "OP_MULTIPLY_3", offset);

			case OpCode.DIVIDE:
				return simpleInstruction(log, "OP_DIVIDE", offset);

			case OpCode.DIVIDE_3:
				return simpleInstruction(log, "OP_DIVIDE_3", offset);

			case OpCode.DUPLICATE:
				return simpleInstruction(log, "OP_DUPLICATE", offset);

			case OpCode.DUPLICATE_3:
				return simpleInstruction(log, "OP_DUPLICATE_3", offset);

			case OpCode.POW:
				return simpleInstruction(log, "OP_POW", offset);

			case OpCode.SIN:
				return simpleInstruction(log, "OP_SIN", offset);

			case OpCode.COS:
				return simpleInstruction(log, "OP_COS", offset);

			case OpCode.TAN:
				return simpleInstruction(log, "OP_TAN", offset);

			case OpCode.MOD:
				return simpleInstruction(log, "OP_MOD", offset);

			case OpCode.TRUNCATE:
				return simpleInstruction(log, "OP_TRUNCATE", offset);

			case OpCode.LOAD:
				return simpleInstruction(log, "OP_LOAD", offset);

			case OpCode.LOAD_3:
				return simpleInstruction(log, "OP_LOAD_3", offset);

			case OpCode.CALL:
				return simpleInstruction(log, "OP_CALL", offset);

			case OpCode.STORE:
				return simpleInstruction(log, "OP_STORE", offset);

			case OpCode.STORE_3:
				return simpleInstruction(log, "OP_STORE_3", offset);

			case OpCode.DEFINE:
				return simpleInstruction(log, "OP_DEFINE", offset);

			case OpCode.JUMP_TO_CALLEE:
				return simpleInstruction(log, "OP_JUMP_TO_CALLEE", offset);

			case OpCode.JUMP:
				return argInstruction(log, "OP_JUMP", offset);

			case OpCode.JUMP_IF_NEGATIVE:
				return argInstruction(log, "OP_JUMP_IF_NEGATIVE", offset);

			case OpCode.JUMP_IF_POSITIVE:
				return argInstruction(log, "OP_JUMP_IF_POSITIVE", offset);

			case OpCode.COMPARE:
				return simpleInstruction(log, "OP_COMPARE", offset);

			case OpCode.POP:
				return simpleInstruction(log, "OP_POP", offset);

			case OpCode.RAYCAST_BLOCKPOS:
				return simpleInstruction(log, "OP_RAYCAST_BLOCKPOS", offset);

			case OpCode.RAYCAST_BLOCKSIDE:
				return simpleInstruction(log, "OP_RAYCAST_BLOCKSIDE", offset);

			case OpCode.ENTITY_POS:
				return simpleInstruction(log, "OP_ENTITY_POS", offset);

			case OpCode.ENTITY_EYE_POS:
				return simpleInstruction(log, "OP_ENTITY_EYE_POS", offset);

			case OpCode.ENTITY_LOOK:
				return simpleInstruction(log, "OP_ENTITY_LOOK", offset);

			case OpCode.ENTITY_LOOKED_AT:
				return simpleInstruction(log, "OP_RAYCAST_ENTITY", offset);

			case OpCode.ENTITY_NBT:
				return simpleInstruction(log, "OP_ENTITY_NBT", offset);

			case OpCode.ADD_MOTION:
				return simpleInstruction(log, "OP_ADD_MOTION", offset);

			case OpCode.SPAWN_ENTITY:
				return simpleInstruction(log, "OP_SPAWN_ENTITY", offset);

			case OpCode.CREATE_EXPLOSION:
				return simpleInstruction(log, "OP_CREATE_EXPLOSION", offset);

			case OpCode.MODIFY_ENTITY_NBT:
				return simpleInstruction(log, "OP_MODIFY_ENTITY_NBT", offset);

			case OpCode.DEFAULT_BLOCK_STATE:
				return simpleInstruction(log, "OP_DEFAULT_BLOCK_STATE", offset);

			case OpCode.GET_BLOCK:
				return simpleInstruction(log, "OP_GET_BLOCK", offset);

			case OpCode.SET_BLOCK:
				return simpleInstruction(log, "OP_SET_BLOCK", offset);

			case OpCode.PAUSE:
				return simpleInstruction(log, "OP_PAUSE", offset);

			case OpCode.NEW_NBT:
				return simpleInstruction(log, "OP_NEW_NBT", offset);

			case OpCode.NBT_GET:
				return simpleInstruction(log, "OP_NBT_GET", offset);

			case OpCode.NBT_SET:
				return simpleInstruction(log, "OP_NBT_SET", offset);

			case OpCode.BLOCK_STATE_GET:
				return simpleInstruction(log, "OP_BLOCK_STATE_GET", offset);

			case OpCode.BLOCK_STATE_SET:
				return simpleInstruction(log, "OP_BLOCK_STATE_SET", offset);

			default:
				writeLog(log, "Unknown opcode '%04d'", instruction);
				return offset + 1;
		}
	}

	private int simpleInstruction(StringBuilder log, String name, int offset) {
		writeLog(offset, log, "%-20s %04d", name, offset);
		return offset + 1;
	}

	private int constantInstruction(StringBuilder log, String name, int offset) {
		int constant = code.get(offset + 1);

		writeLog(offset, log, "%-20s %04d '%s'", name, offset, constant < constants.size() ? constants.get(constant) : null);
		return offset + 2;
	}

	private int argInstruction(StringBuilder log, String name, int offset) {
		writeLog(offset, log, "%-20s %04d %04d", name, offset, code.get(offset + 1));
		return offset + 2;
	}

	private void writeLog(int offset, StringBuilder log, String message, Object... objects) {
		for (Entry<String, Integer> entry : functionAddresses.entrySet()) {
			if (offset == entry.getValue()) {
				message += " | function: " + entry.getKey();
			}
		}

		writeLog(log, message, objects);
	}

	private void writeLog(StringBuilder log, String message, Object... objects) {
		log.append(String.format(message, objects) + "\n");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (!(obj instanceof Chunk)) { return false; }
		Chunk chunkObj = (Chunk)obj;
		return code.equals(chunkObj.code) && constants.equals(chunkObj.constants);
	}
}