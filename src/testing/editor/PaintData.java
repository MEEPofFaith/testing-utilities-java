package testing.editor;

public class PaintData{
    private static final long floorMask = 0xFFL;
    private static final long overlayMask = 0xFFL << 8;
    private static final long extraMask = 0xFFFFFFFFL << 16;

    public static byte floor(long l){
        return (byte)(l & floorMask);
    }

    public static byte overlay(long l){
        return (byte)((l & overlayMask) >>> 8);
    }

    public static int extra(long l){
        return (int)((l & extraMask) >>> 16);
    }

    public static long get(byte floorData, byte overlayData, int extraData){
        return (long)floorData & floorMask | (long)overlayData << 8 & overlayMask | (long)extraData << 16 & extraMask;
    }
}
