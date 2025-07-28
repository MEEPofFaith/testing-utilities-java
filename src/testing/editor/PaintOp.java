package testing.editor;

import mindustry.gen.*;

/** Based on {@link TileOp}. */
public class PaintOp{
    private static final long xMask = 0xffffL;
    private static final long yMask = 0xffffL << 16;
    private static final long typeMask = 0xffL << 32;
    private static final long valueMask = 0xffffffL << 40;

    public static short x(long paintOp){
        return (short)(paintOp & xMask);
    }

    public static short y(long paintOp){
        return (short)((paintOp & yMask) >>> 16);
    }

    public static byte type(long paintOp){
        return (byte)((paintOp & typeMask) >>> 32);
    }

    public static int value(long paintOp){
        return (int)((paintOp & valueMask) >>> 40);
    }

    public static long get(short x, short y, byte type, int value){
        return (long)x & xMask | (long)y << 16 & yMask | (long)type << 32 & typeMask | (long)value << 40 & valueMask;
    }
}
