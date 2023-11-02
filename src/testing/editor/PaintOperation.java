package testing.editor;

import arc.struct.*;
import mindustry.content.*;
import mindustry.editor.*;
import mindustry.editor.DrawOperation.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;
import static testing.util.TUVars.*;

/** Based on {@link DrawOperation} */
public class PaintOperation{
    private LongSeq array = new LongSeq();

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
        array.set(i, PaintOp.get(PaintOp.x(l), PaintOp.y(l), PaintOp.type(l), getTile(tile, PaintOp.type(l)), tile.data));
        setTile(painter.tile(PaintOp.x(l), PaintOp.y(l)), PaintOp.type(l), PaintOp.value(l), PaintOp.data(l));
    }

    private short getTile(Tile tile, byte type){
        if(type == OpType.floor.ordinal()){
            return tile.floorID();
        }else if(type == OpType.block.ordinal()){
            return tile.blockID();
        }else if(type == OpType.rotation.ordinal()){
            return tile.build == null ? 0 : (byte)tile.build.rotation;
        }else if(type == OpType.team.ordinal()){
            return (byte)tile.getTeamID();
        }else if(type == OpType.overlay.ordinal()){
            return tile.overlayID();
        }
        throw new IllegalArgumentException("Invalid type.");
    }

    private void setTile(Tile tile, byte type, short to, byte data){
        painter.load(() -> {
            if(type == OpType.floor.ordinal()){
                if(content.block(to) instanceof Floor floor){
                    tile.setFloor(floor);
                }
            }else if(type == OpType.block.ordinal()){
                Block block = content.block(to);

                if(block == Blocks.cliff){
                    if(data == 0){
                        painter.pendingCliffs.add(tile); //Pending cliff was added
                    }else{
                        painter.pendingCliffs.remove(tile); //Preexisting cliff was added
                    }
                }else if(tile.block() == Blocks.cliff){
                    if(tile.data == 0) painter.pendingCliffs.remove(tile); //Pending cliff was removed
                }

                tile.setBlock(block, tile.team(), tile.build == null ? 0 : tile.build.rotation);
                tile.data = data;
            }else if(type == OpType.rotation.ordinal()){
                if(tile.build != null) tile.build.rotation = to;
            }else if(type == OpType.team.ordinal()){
                tile.setTeam(Team.get(to));
            }else if(type == OpType.overlay.ordinal()){
                tile.setOverlayID(to);
            }
        });
    }
}
