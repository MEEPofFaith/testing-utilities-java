package testing.editor;

import mindustry.gen.*;

/** Based on {@link TileOp}. */
public class PaintOp{
    private static final long xMask = 0x3fffL; // 14 bits
    private static final long yMask = 0x3fffL; // 14 bits
    private static final long typeMask = 0x7L; // 3 bits
    private static final long valueMask = 0xffffffffffL; // 32 bits

    public static short x(long paintOp){
        return (short)(paintOp & xMask);
    }

    public static short y(long paintOp){
        return (short)((paintOp >>> 14) & yMask);
    }

    public static byte type(long paintOp){
        return (byte)((paintOp >>> 28) & typeMask);
    }

    public static int value(long paintOp){
        return (int)((paintOp >>> 31) & valueMask);
    }

    public static long get(short x, short y, byte type, int value){
        return (long)x & xMask | ((long)y & yMask) << 14 | ((long)type & typeMask) << 28 | ((long)value & valueMask) << 31;
    }
}
