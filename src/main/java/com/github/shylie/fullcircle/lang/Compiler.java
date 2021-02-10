package com.github.shylie.fullcircle.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class Compiler {
    public static final Compiler COMPILER = new Compiler();

    private static enum CompileResult {
        OK,
        CONTINUE,
        ERROR;
    }

    private static class IfInfo {
        public IfInfo(int x, int y) {
            this.x = x;
            this.y = y;

            up = -1;
            down = -1;
            left = -1;
            right = -1;
        }

        public int x;
        public int y;

        public int up;
        public int down;
        public int left;
        public int right;
    }

    // name this better?
    private static final int NUM_PREV = 7;

    private char[] prev = new char[NUM_PREV];
    private int x;
    private int y;
    private Direction direction;
    private int op;
    private String[] source;
    private Map<String, String> strings;
    private int wcx;
    private int wcy;
    private Chunk chunk;
    private PlayerInteractEvent event;
    private List<IfInfo> infos = new ArrayList<>();

    private Compiler() {
    }

    public boolean Compile(String[] source, Map<String, String> strings, int wcx, int wcy, Direction startDirection, PlayerInteractEvent event, Chunk out, StringBuilder log) {
        this.source = source;
        this.strings = strings;
        this.wcx = wcx;
        this.wcy = wcy;

        this.event = event;
        x = source.length / 2;
        y = source[0].length() / 2;
        direction = startDirection;
        op = -1;
        chunk = out;
        for (int i = 0; i < NUM_PREV; i++) {
            prev[i] = ' ';
        }

        infos.clear();

        CompileResult result = CompileResult.CONTINUE;
        for (int count = 0; result == CompileResult.CONTINUE && count < 4096; count++) {
            result = compileLine();
        }

        if (chunk.last() != OpCode.RETURN) {
            chunk.write(OpCode.RETURN);
        }

        chunk.dissasemble(log, "main");

        return result == CompileResult.OK;
    }

    private CompileResult compileIf() {
        int sx = x;
        int sy = y;
        Direction sd = direction;

        switch (direction) {
            case RIGHT:
                infos.get(infos.size() - 1).right = chunk.size();
                break;

            case LEFT:
                infos.get(infos.size() - 1).left = chunk.size();
                break;

            case UP:
                infos.get(infos.size() - 1).up = chunk.size();
                break;

            case DOWN:
                infos.get(infos.size() - 1).down = chunk.size();
                break;
        }

        chunk.write(OpCode.JUMP_IF_NEGATIVE);
        int negpatch = chunk.write(-1);
        chunk.write(OpCode.JUMP_IF_POSITIVE);
        int pospatch = chunk.write(-1);

        chunk.write(OpCode.POP);
        {
            CompileResult result = CompileResult.CONTINUE;
            for (int count = 0; result == CompileResult.CONTINUE && count < 4096; count++) {
                result = compileLine();
            }
            if (result == CompileResult.ERROR) { return CompileResult.ERROR; }
        }

        chunk.write(OpCode.RETURN);

        chunk.modify(negpatch, chunk.size());

        x = sx;
        y = sy;
        direction = Direction.RotateLeft(sd);

        chunk.write(OpCode.POP);
        {
            CompileResult result = CompileResult.CONTINUE;
            for (int count = 0; result == CompileResult.CONTINUE && count < 4096; count++) {
                result = compileLine();
            }
            if (result == CompileResult.ERROR) { return CompileResult.ERROR; }
        }

        chunk.write(OpCode.RETURN);

        chunk.modify(pospatch, chunk.size());

        x = sx;
        y = sy;
        direction = Direction.RotateRight(sd);

        chunk.write(OpCode.POP);
        {
            CompileResult result = CompileResult.CONTINUE;
            for (int count = 0; result == CompileResult.CONTINUE && count < 4096; count++) {
                result = compileLine();
            }
            if (result == CompileResult.ERROR) { return CompileResult.ERROR; }
        }

        return CompileResult.OK;
    }

    private CompileResult compileLine() {
        while (true) {
            switch (direction) {
                case RIGHT:
                    x++;
                    break;
    
                case LEFT:
                    x--;
                    break;
    
                case DOWN:
                    y--;
                    break;
    
                case UP:
                    y++;
                    break;
            }
    
            // went off grid, done
            if (y < 0 || y >= source.length || x < 0 || x >= source[0].length()) {
                return CompileResult.OK;
            }
    
            char current = source[y].charAt(x);
    
            // blocks that can be processed right away and do not reset Compiler#prev
            switch (current) {
                case '>':
                    direction = Direction.RIGHT;
                    return CompileResult.CONTINUE;
    
                case '<':
                    direction = Direction.LEFT;
                    return CompileResult.CONTINUE;
    
                case 'v':
                    direction = Direction.UP;
                    return CompileResult.CONTINUE;
    
                case '^':
                    direction = Direction.DOWN;
                    return CompileResult.CONTINUE;

                case 'B':
                    chunk.write(OpCode.RETURN);
                    return CompileResult.OK;

                case 'C':
                    chunk.write(OpCode.CONSTANT);
                    chunk.write(chunk.writeConstant(new StringValue(strings.getOrDefault(String.format("fcspellstring %d %d", wcx + x - (source.length / 2), wcy + y - (source[0].length() / 2)), ""))));
                    return CompileResult.CONTINUE;

                case 'S':
                    op = (op < 0 ? OpCode.STORE : OpCode.STORE_3);
                    break;

                case 'L':
                    op = (op < 0 ? OpCode.LOAD : OpCode.LOAD_3);
                    break;

                case 'T':
                    if (getJumpAddress(direction) >= 0) {
                        chunk.write(OpCode.JUMP);
                        chunk.write(getJumpAddress(direction));
                        return CompileResult.OK;
                    }
                    else {
                        infos.add(new IfInfo(x, y));
                        return compileIf();
                    }
            }

            if (op < 0 && valid(current) && valid(prev[0])) {
                op = OpCode.parseColors(prev[0], current);
            }

            switch (op) {
                case OpCode.NEGATE:
                case OpCode.NEGATE_3:
                case OpCode.ADD:
                case OpCode.ADD_3:
                case OpCode.SUBTRACT:
                case OpCode.SUBTRACT_3:
                case OpCode.MULTIPLY:
                case OpCode.MULTIPLY_3:
                case OpCode.DIVIDE:
                case OpCode.DIVIDE_3:
                case OpCode.DUPLICATE:
                case OpCode.DUPLICATE_3:
                case OpCode.POP:
                case OpCode.COMPARE:
                case OpCode.RAYCAST_BLOCKPOS:
                case OpCode.RAYCAST_BLOCKSIDE:
                case OpCode.ENTITY_POS:
                case OpCode.ENTITY_EYE_POS:
                case OpCode.ENTITY_LOOK:
                case OpCode.ENTITY_LOOKED_AT:
                case OpCode.ENTITY_NBT:
                case OpCode.SPAWN_ENTITY:
                case OpCode.ADD_MOTION:
                case OpCode.CREATE_EXPLOSION:
                case OpCode.MOVE_BLOCK:
                case OpCode.PAUSE:
                case OpCode.MODIFY_ENTITY_NBT:
                case OpCode.NEW_NBT:
                case OpCode.NBT_GET:
                case OpCode.NBT_SET:
                    chunk.write(op);
                    reset();
                    break;

                case OpCode.LOAD:
                case OpCode.LOAD_3:
                case OpCode.STORE:
                case OpCode.STORE_3:
                    if (valid(current, prev[0])) {
                        chunk.write(op);
                        chunk.write(OpCode.parseColors(prev[0], current));
                        reset();
                    }
                    else {
                        pushCurrentToPrev(current);
                    }
                    break;

                case OpCode.CONSTANT:
                    if (valid(current, prev[0], prev[1], prev[2])) {
                        chunk.write(OpCode.CONSTANT);
                        chunk.write(chunk.writeConstant(new LongValue(OpCode.parseColors(prev[0], current))));
                        reset();
                    }
                    else {
                        pushCurrentToPrev(current);
                    }
                    break;

                case OpCode.CONSTANT_LONG:
                    if (valid(current, prev[0], prev[1], prev[2], prev[3], prev[4])) {
                        chunk.write(OpCode.CONSTANT);
                        chunk.write(chunk.writeConstant(new LongValue(OpCode.parseColors(prev[2], prev[1], prev[0], current))));
                        reset();
                    }
                    else {
                        pushCurrentToPrev(current);
                    }
                    break;

                case OpCode.CONSTANT_DOUBLE:
                    if (valid(current, prev[0], prev[1], prev[2], prev[3], prev[4], prev[5], prev[6])) {
                        chunk.write(OpCode.CONSTANT);
                        chunk.write(chunk.writeConstant(new DoubleValue(OpCode.parseColors(prev[4], prev[3], prev[2], prev[1], prev[0], current))));
                        reset();
                    }
                    else {
                        pushCurrentToPrev(current);
                    }
                    break;

                case OpCode.CASTER:
                    chunk.write(OpCode.CONSTANT);
                    chunk.write(chunk.writeConstant(new EntityValue(event.getPlayer().getEntityId())));
                    reset();
                    break;

                default:
                    pushCurrentToPrev(current);
                    break;
            }
        }
    }

    private void reset() {
        for (int i = 0; i < NUM_PREV; i++) {
            prev[i] = ' ';
        }
        op = -1;
    }

    private void pushCurrentToPrev(char current) {
        if (current != ' ') { // ignore "whitespace"
            for (int i = NUM_PREV - 1; i > 0; i--) {
                prev[i] = prev[i - 1];
            }
            prev[0] = current;
        }
    }

    private int getJumpAddress(Direction checkDirection) {
        for (IfInfo ifInfo : infos) {
            if (x == ifInfo.x && y == ifInfo.y) {
                switch (checkDirection) {
                    case RIGHT:
                        if (ifInfo.right >= 0) { return ifInfo.right; }
                        break;

                    case LEFT:
                        if (ifInfo.left >= 0) { return ifInfo.left; }
                        break;

                    case UP:
                        if (ifInfo.up >= 0) { return ifInfo.up; }
                        break;

                    case DOWN:
                        if (ifInfo.down >= 0) { return ifInfo.down; }
                        break;
                }
            }
        }
        return -1;
    }

    private static boolean valid(char character) {
        return character >= '0' && character <= '9';
    }

    private static boolean valid(char... characters) {
        for (int i = 0; i < characters.length; i++) {
            if (!valid(characters[i])) {
                return false;
            }
        }
        return true;
    }
}