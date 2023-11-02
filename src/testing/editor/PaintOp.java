package testing.editor;

import mindustry.gen.*;

/** Based on {@link TileOp}. Stores an extra byte for tile data. */
public class PaintOp{
    private static final long xMask = 0xffffL;
    private static final long yMask = 0xffffL << 16;
    private static final long typeMask = 0xffL << 32;
    private static final long valueMask = 0xffffL << 40;
    private static final long dataMask = 0xffL << 56;

    public static short x(long paintOp){
        return (short)(paintOp & xMask);
    }

    public static short y(long paintOp){
        return (short)((paintOp & yMask) >>> 16);
    }

    public static byte type(long paintOp){
        return (byte)((paintOp & typeMask) >>> 32);
    }

    public static short value(long paintOp){
        return (short)((paintOp & valueMask) >>> 40);
    }

    public static byte data(long paintOp){
        return (byte)((paintOp & dataMask) >>> 56);
    }

    public static long get(short x, short y, byte type, short value){
        return get(x, y, type, value, (byte)0);
    }

    public static long get(short x, short y, byte type, short value, byte data){
        return (long)x & xMask | (long)y << 16 & yMask | (long)type << 32 & typeMask | (long)value << 40 & valueMask | (long)data << 56 & dataMask;
    }
}
