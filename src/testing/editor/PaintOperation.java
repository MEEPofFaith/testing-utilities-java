package testing.editor;

import arc.struct.*;
import mindustry.editor.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;
import static testing.util.TUVars.*;

/** Based on {@link DrawOperation} */
public class PaintOperation{
    static final byte
        opFloor = 0,
        opBlock = 1,
        opRotation = 2,
        opTeam = 3,
        opOverlay = 4,
        opData = 5,
        opExtraData = 6;

    private final LongSeq array = new LongSeq();

    public boolean isEmpty(){
        return array.isEmpty();
    }

    public int size(){
        return array.size;
    }

    public void remove(int amount){
        array.setSize(Math.max(0, array.size - amount));
    }

    public void addOperation(long op){
        array.add(op);
    }

    public void undo(){
        for(int i = array.size - 1; i >= 0; i--){
            updateTile(i);
        }
    }

    public void redo(){
        for(int i = 0; i < array.size; i++){
            updateTile(i);
        }
    }

    private void updateTile(int i){
        long op = array.get(i);
        Tile tile = painter.tile(PaintOp.x(op), PaintOp.y(op));
        array.set(i, PaintOp.get(tile.x, tile.y, PaintOp.type(op), getTile(tile, PaintOp.type(op))));
        setTile(tile, PaintOp.type(op), PaintOp.value(op));
    }

    private int getTile(Tile tile, byte type){
        return switch(type){
            case opFloor -> tile.floorID();
            case opOverlay -> tile.overlayID();
            case opBlock -> tile.blockID();
            case opRotation -> tile.build == null ? 0 : (byte)tile.build.rotation;
            case opTeam -> (byte)tile.getTeamID();
            case opData -> PaintOpData.get(tile.data, tile.floorData, tile.overlayData);
            case opExtraData -> tile.extraData;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }

    private void setTile(Tile tile, byte type, int to){
        painter.load(() -> {
            switch(type){
                case opFloor -> {
                    if(content.block(to) instanceof Floor floor){
                        tile.setFloor(floor);
                    }
                }
                case opOverlay -> {
                    if(content.block(to) instanceof Floor floor){
                        tile.setOverlay(floor);
                    }
                }
                case opBlock -> {
                    Block block = content.block(to);

                    if(block instanceof Cliff){
                        painter.pendingCliffs.add(tile); //Pending cliff was added
                    }else if(tile.block() instanceof Cliff && tile.data == 0){
                        painter.pendingCliffs.remove(tile); //Pending cliff was removed
                    }

                    tile.setBlock(block, tile.team(), tile.build == null ? 0 : tile.build.rotation);
                    if(tile.build != null){
                        tile.build.enabled = true;
                    }
                }
                case opRotation -> {
                    if(tile.build != null) tile.build.rotation = to;
                }
                case opTeam -> tile.setTeam(Team.get(to));
                case opData -> {
                    tile.data = PaintOpData.data(to);
                    tile.floorData = PaintOpData.floor(to);
                    tile.overlayData = PaintOpData.overlay(to);

                    tile.recache();
                    tile.recacheWall();
                }
                case opExtraData -> {
                    tile.extraData = to;

                    tile.recache();
                    tile.recacheWall();
                }
            }
        });
    }
}
