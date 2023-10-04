package testing.editor;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.editor.DrawOperation.*;
import mindustry.editor.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

/** Based on {@link MapEditor}. Made to operate in a live map instead of the editor. */
public class TerrainPainter{
    private PaintOperationStack stack = new PaintOperationStack();
    private PaintOperation currentOp;
    private PaintedTileData[][] data;
    private boolean loading;

    public final Seq<Tile> pendingCliffs = new Seq<>();
    public float brushSize = 1;
    public int rotation;
    public Block drawBlock = Blocks.boulder;
    public Team drawTeam = Team.sharded;

    public boolean isLoading(){
        return loading;
    }

    private void reset(){
        flushCliffs();
        clearOp();
    }

    public void beginEditing(){
        loading = true;
        Tiles tiles = tiles();
        data = new PaintedTileData[width()][height()];
        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                Tile t = tiles.get(x, y);
                data[x][y] = new PaintedTileData(t);
            }
        }
        loading = false;
    }

    /** Converts all tiles in the world to normal {@link Tile}s. */
    public void endEditing(){
        data = null;
        reset();
        Events.fire(new WorldLoadEvent());
    }

    public void load(Runnable r){
        loading = true;
        r.run();
        loading = false;
    }

    public Tiles tiles(){
        return world.tiles;
    }

    public Tile tile(int x, int y){
        return world.rawTile(x, y);
    }

    public int width(){
        return world.width();
    }

    public int height(){
        return world.height();
    }

    public void drawBlocksReplace(int x, int y){
        drawBlocks(x, y, data -> data.block() != Blocks.air || drawBlock.isFloor());
    }

    public void drawBlocks(int x, int y){
        drawBlocks(x, y, false, false, data -> true);
    }

    public void drawBlocks(int x, int y, Boolf<PaintedTileData> tester){
        drawBlocks(x, y, false, false, tester);
    }

    public void drawBlocks(int x, int y, boolean square, boolean forceOverlay, Boolf<PaintedTileData> tester){
        if(drawBlock.isMultiblock()){
            x = Mathf.clamp(x, (drawBlock.size - 1) / 2, width() - drawBlock.size / 2 - 1);
            y = Mathf.clamp(y, (drawBlock.size - 1) / 2, height() - drawBlock.size / 2 - 1);
            if(!hasOverlap(x, y)){
                data(x, y).setBlock(drawBlock, drawTeam, rotation);
            }
        }else{
            boolean isFloor = drawBlock.isFloor() && drawBlock != Blocks.air;

            Cons<PaintedTileData> drawer = data -> {
                if(!tester.get(data)) return;

                if(isFloor){
                    if(forceOverlay){
                        data.setOverlay(drawBlock.asFloor());
                    }else{
                        if(!(drawBlock.asFloor().wallOre && !data.block().solid)){
                            data.setFloor(drawBlock.asFloor());
                        }
                    }
                }else if(!(data.block().isMultiblock() && !drawBlock.isMultiblock())){
                    if(drawBlock.rotate && data.build() != null && data.build().rotation != rotation){
                        addPaintOp(PaintOp.get(data.x(), data.y(), (byte)OpType.rotation.ordinal(), (byte)rotation));
                    }

                    data.setBlock(drawBlock, drawTeam, rotation);
                }
            };

            if(drawBlock instanceof SteamVent){ //Always draw a single vent
                drawSquare(x, y, 1, drawer);
            }else if(square){
                drawSquare(x, y, brushSize, drawer);
            }else{
                drawCircle(x, y, brushSize, drawer);
            }
        }
    }

    boolean hasOverlap(int x, int y){
        Tile tile = world.tile(x, y);
        //allow direct replacement of blocks of the same size
        if(tile != null && tile.isCenter() && tile.block() != drawBlock && tile.block().size == drawBlock.size && tile.x == x && tile.y == y){
            return false;
        }

        //else, check for overlap
        int offsetx = -(drawBlock.size - 1) / 2;
        int offsety = -(drawBlock.size - 1) / 2;
        for(int dx = 0; dx < drawBlock.size; dx++){
            for(int dy = 0; dy < drawBlock.size; dy++){
                int worldx = dx + offsetx + x;
                int worldy = dy + offsety + y;
                Tile other = world.tile(worldx, worldy);

                if(other != null && other.block().isMultiblock()){
                    return true;
                }
            }
        }

        return false;
    }

    public void drawCircle(int x, int y, float brushSize, Cons<PaintedTileData> drawer){
        int clamped = (int)brushSize;
        for(int rx = -clamped; rx <= clamped; rx++){
            for(int ry = -clamped; ry <= clamped; ry++){
                if(Mathf.within(rx, ry, brushSize - 0.5f + 0.0001f)){
                    int wx = x + rx, wy = y + ry;

                    if(wx < 0 || wy < 0 || wx >= width() || wy >= height()){
                        continue;
                    }

                    drawer.get(data(wx, wy));
                }
            }
        }
    }

    public void drawSquare(int x, int y, float brushSize, Cons<PaintedTileData> drawer){
        int clamped = (int)brushSize;
        for(int rx = -clamped; rx <= clamped; rx++){
            for(int ry = -clamped; ry <= clamped; ry++){
                int wx = x + rx, wy = y + ry;

                if(wx < 0 || wy < 0 || wx >= width() || wy >= height()){
                    continue;
                }

                drawer.get(data(wx, wy));
            }
        }
    }

    public void flushCliffs(){
        if(pendingCliffs.isEmpty()) return;

        for(Tile tile : pendingCliffs){
            if(!tile.block().isStatic() || tile.block() != Blocks.cliff) continue;
            int rotation = 0;
            for(int i = 0; i < 8; i++){
                Tile other = world.tiles.get(tile.x + Geometry.d8[i].x, tile.y + Geometry.d8[i].y);
                if(other != null && !other.block().isStatic()){
                    rotation |= (1 << i);
                }
            }
            addPaintOp(PaintOp.get(tile.x, tile.y, (byte)OpType.block.ordinal(), Blocks.cliff.id, tile.data));
            tile.data = (byte)rotation;
        }
        for(Tile tile : pendingCliffs){
            if(tile.block() == Blocks.cliff && tile.data == 0){
                tile.setBlock(Blocks.air);
            }
        }

        flushOp();
        pendingCliffs.clear();
    }

    public void clearOp(){
        stack.clear();
    }

    public void undo(){
        if(stack.canUndo()){
            stack.undo();
        }
    }

    public void redo(){
        if(stack.canRedo()){
            stack.redo();
        }
    }

    public boolean canUndo(){
        return stack.canUndo();
    }

    public boolean canRedo(){
        return stack.canRedo();
    }

    public void flushOp(){
        if(currentOp == null || currentOp.isEmpty()) return;
        stack.add(currentOp);
        currentOp = null;
    }

    public void addPaintOp(long data){
        if(loading) return;

        if(currentOp == null) currentOp = new PaintOperation();
        currentOp.addOperation(data);
    }

    public PaintedTileData data(int x, int y){
        return data[x][y];
    }

    public PaintedTileData data(Tile tile){
        return data(tile.x, tile.y);
    }
}
