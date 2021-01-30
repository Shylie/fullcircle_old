package com.github.shylie.fullcircle.lang;

import java.util.ArrayList;
import java.util.List;

import com.github.shylie.fullcircle.FullCircle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
    private static final Logger LOGGER = LogManager.getLogger(FullCircle.MOD_ID + ".lang.Chunk");

    private List<Integer> code;
    private List<Value> constants;

    public Chunk() {
        code = new ArrayList<>();
        constants = new ArrayList<>();
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
        constants.add(constant);
        return constants.size() - 1;
    }

    public Value readConstant(int loc) {
        return constants.get(loc);
    }

    public void dissasemble(String name) {
        LOGGER.debug(String.format("== %s ==", name));

        for (int offset = 0; offset < code.size();) {
            offset = dissasembleInstruction(offset);
        }
    }

    public int dissasembleInstruction(int offset) {
        if (code.size() <= offset) {
            LOGGER.debug("Dissasembling non-existent instruction");
            return offset + 1;
        }
        int instruction = code.get(offset);
        switch (instruction) {
            case OpCode.RETURN:
                return simpleInstruction("OP_RETURN", offset);

            case OpCode.CONSTANT:
                return constantInstruction("OP_CONSTANT", offset);

            case OpCode.CONSTANT_LONG:
                return constantInstruction("OP_CONSTANT_LONG", offset);

            case OpCode.NEGATE:
                return simpleInstruction("OP_NEGATE", offset);

            case OpCode.NEGATE_3:
                return simpleInstruction("OP_NEGATE_3", offset);

            case OpCode.ADD:
                return simpleInstruction("OP_ADD", offset);

            case OpCode.ADD_3:
                return simpleInstruction("OP_ADD_3", offset);

            case OpCode.SUBTRACT:
                return simpleInstruction("OP_SUBTRACT", offset);

            case OpCode.SUBTRACT_3:
                return simpleInstruction("OP_SUBTRACT_3", offset);

            case OpCode.MULTIPLY:
                return simpleInstruction("OP_MULTIPLY", offset);

            case OpCode.MULTIPLY_3:
                return simpleInstruction("OP_MULTIPLY_3", offset);

            case OpCode.DIVIDE:
                return simpleInstruction("OP_DIVIDE", offset);

            case OpCode.DIVIDE_3:
                return simpleInstruction("OP_DIVIDE_3", offset);

            case OpCode.DUPLICATE:
                return simpleInstruction("OP_DUPLICATE", offset);

            case OpCode.DUPLICATE_3:
                return simpleInstruction("OP_DUPLICATE_3", offset);

            case OpCode.LOAD:
                return argInstruction("OP_LOAD", offset);

            case OpCode.LOAD_3:
                return argInstruction("OP_LOAD_3", offset);

            case OpCode.STORE:
                return argInstruction("OP_STORE", offset);

            case OpCode.STORE_3:
                return argInstruction("OP_STORE_3", offset);

            case OpCode.JUMP:
                return argInstruction("OP_JUMP", offset);

            case OpCode.JUMP_IF_NEGATIVE:
                return argInstruction("OP_JUMP_IF_NEGATIVE", offset);

            case OpCode.JUMP_IF_POSITIVE:
                return argInstruction("OP_JUMP_IF_POSITIVE", offset);

            case OpCode.POP:
                return simpleInstruction("OP_POP", offset);

            case OpCode.RAYCAST_BLOCKPOS:
                return simpleInstruction("OP_RAYCAST_BLOCKPOS", offset);

            case OpCode.RAYCAST_BLOCKSIDE:
                return simpleInstruction("OP_RAYCAST_BLOCKSIDE", offset);

            case OpCode.ENTITY_POS:
                return simpleInstruction("OP_ENTITY_POS", offset);

            case OpCode.ENTITY_EYE_POS:
                return simpleInstruction("OP_ENTITY_EYE_POS", offset);

            case OpCode.ENTITY_LOOK:
                return simpleInstruction("OP_ENTITY_LOOK", offset);

            case OpCode.ENTITY_LOOKED_AT:
                return simpleInstruction("OP_RAYCAST_ENTITY", offset);

            case OpCode.ENTITY_NBT:
                return simpleInstruction("OP_ENTITY_NBT", offset);

            case OpCode.ADD_MOTION:
                return simpleInstruction("OP_ADD_MOTION", offset);

            case OpCode.SPAWN_ENTITY:
                return simpleInstruction("OP_SPAWN_ENTITY", offset);

            case OpCode.CREATE_EXPLOSION:
                return simpleInstruction("OP_CREATE_EXPLOSION", offset);

            case OpCode.MOVE_BLOCK:
                return simpleInstruction("OP_MOVE_BLOCK", offset);

            case OpCode.MODIFY_ENTITY_NBT:
                return simpleInstruction("OP_MODIFY_ENTITY_NBT", offset);

            case OpCode.PAUSE:
                return simpleInstruction("OP_PAUSE", offset);

            case OpCode.NEW_NBT:
                return simpleInstruction("OP_NEW_NBT", offset);

            case OpCode.NBT_GET:
                return simpleInstruction("OP_NBT_GET", offset);

            case OpCode.NBT_SET:
                return simpleInstruction("OP_NBT_SET", offset);

            default:
                LOGGER.debug(String.format("Unknown OpCode '%04d'", instruction));
                return offset + 1;
        }
    }

    private int simpleInstruction(String name, int offset) {
        LOGGER.debug(String.format("%-20s %04d", name, offset));
        return offset + 1;
    }

    private int constantInstruction(String name, int offset) {
        int constant = code.get(offset + 1);

        LOGGER.debug(String.format("%-20s %04d '%s'", name, offset, constant < constants.size() ? constants.get(constant) : null));
        return offset + 2;
    }

    private int argInstruction(String name, int offset) {
        LOGGER.debug(String.format("%-20s %04d %04d", name, offset, code.get(offset + 1)));
        return offset + 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (!(obj instanceof Chunk)) { return false; }
        Chunk chunkObj = (Chunk)obj;
        return code.equals(chunkObj.code) && constants.equals(chunkObj.constants);
    }
}