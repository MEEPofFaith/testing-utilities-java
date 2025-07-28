package testing.editor;

public class PaintOpData{
    private static final int dataMask = 0xFF;
    private static final int floorMask = 0xFF << 8;
    private static final int overlayMask = 0xFF << 16;

    public static byte data(long l){
        return (byte)(l & dataMask);
    }

    public static byte floor(long l){
        return (byte)((l & floorMask) >>> 8);
    }

    public static byte overlay(long l){
        return (byte)((l & overlayMask) >>> 16);
    }

    public static int get(byte data, byte floorData, byte overlayData){
        return (int)data & dataMask | (int)floorData << 8 & floorMask | (int)overlayData << 16 & overlayMask;
    }
}
