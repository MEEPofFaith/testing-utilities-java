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
        opData = 5;

    private final LongSeq array = new LongSeq();
    private final IntSeq extraData = new IntSeq();

    public boolean isEmpty(){
        return array.isEmpty();
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
        long l = array.get(i);
        Tile tile = painter.tile(PaintOp.x(l), PaintOp.y(l));
        int data = tile.extraData;
        array.set(i, PaintOp.get(PaintOp.x(l), PaintOp.y(l), PaintOp.type(l), getTile(tile, PaintOp.type(l)), tile.data));
        extraData.set(i, tile.extraData);
        setTile(tile, PaintOp.type(l), PaintOp.value(l), PaintOp.data(l), data);
    }

    private short getTile(Tile tile, byte type){
        return switch(type){
            case opFloor -> tile.floorID();
            case opOverlay -> tile.overlayID();
            case opBlock -> tile.blockID();
            case opRotation -> tile.build == null ? 0 : (byte)tile.build.rotation;
            case opTeam -> (byte)tile.getTeamID();
            case opData -> 0; //In separate array
            default -> throw new IllegalArgumentException("Invalid type.");
        };
    }

    private void setTile(Tile tile, byte type, short to, byte data, int extraData){
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
                        if(data == 0){
                            painter.pendingCliffs.add(tile); //Pending cliff was added
                        }else{
                            painter.pendingCliffs.remove(tile); //Preexisting cliff was added
                        }
                    }else if(tile.block() instanceof Cliff){
                        if(tile.data == 0) painter.pendingCliffs.remove(tile); //Pending cliff was removed
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
            }
            tile.extraData = extraData;
        });
    }
}
