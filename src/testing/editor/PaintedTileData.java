package testing.editor;

import arc.func.*;
import mindustry.editor.DrawOperation.*;
import mindustry.editor.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;
import static testing.util.TUVars.*;

/** Wrapper for {@link Tile} that functions similarly to {@link EditorTile}, but for use while in-game. */
public class PaintedTileData{
    public Tile tile;

    public PaintedTileData(Tile tile){
        this.tile = tile;
    }

    public void setFloor(Floor type){
        Floor tFloor = floor();
        if(type instanceof OverlayFloor){
            //don't place on liquids
            if(tFloor.hasSurface() || !type.needsSurface){
                setOverlayID(type.id);
            }
            return;
        }

        if(tFloor == type && overlayID() == 0) return;
        if(overlayID() != 0) op(OpType.overlay, overlayID());
        if(tFloor != type) op(OpType.floor, tFloor.id);
        tile.setFloor(type);
    }

    public void setBlock(Block type, Team team, int rotation){
        setBlock(type, team, rotation, type::newBuilding);
    }

    public void setBlock(Block type, Team team, int rotation, Prov<Building> entityprov){
        Block tBlock = block();
        Building tBuild = tile.build;
        if(tBlock == type && (tBuild == null || tBuild.rotation == rotation)){
            return;
        }

        if(!isCenter()){
            PaintedTileData cen = painter.getData(tBuild.tile);
            cen.op(OpType.rotation, (byte)tBuild.rotation);
            cen.op(OpType.team, (byte)tBuild.team.id);
            cen.op(OpType.block, tBlock.id);
        }else{
            if(tBuild != null) op(OpType.rotation, (byte)tBuild.rotation);
            if(tBuild != null) op(OpType.team, (byte)tBuild.team.id);
            op(OpType.block, tBlock.id);
        }
        tile.setBlock(type, team, rotation, entityprov);
    }
    
    public void setTeam(Team team){
        if(getTeamID() == team.id) return;
        op(OpType.team, (byte)getTeamID());
        tile.setTeam(team);
    }

    public void setOverlay(Block overlay){
        Floor tFloor = tile.floor();
        Floor tOverlay = tile.overlay();
        if(!tFloor.hasSurface() && overlay.asFloor().needsSurface && (overlay instanceof OreBlock || !tFloor.supportsOverlay))
            return;
        if(tOverlay == overlay) return;
        op(OpType.overlay, tOverlay.id);
        tile.setOverlay(overlay);
    }
    
    public boolean isCenter(){
        return tile.isCenter();
    }

    public short x(){
        return tile.x;
    }

    public short y(){
        return tile.y;
    }

    public Team team(){
        return tile.team();
    }

    public int getTeamID(){
        return team().id;
    }
    
    public Floor overlay(){
        return tile.overlay();
    }

    public short overlayID(){
        return overlay().id;
    }
    
    public Block block(){
        return tile.block();
    }

    public short blockID(){
        return block().id;
    }
    
    public Floor floor(){
        return tile.floor();
    }

    public short floorID(){
        return floor().id;
    }

    public Building build(){
        return tile.build;
    }

    public void setOverlayID(short ore){
        setOverlay(content.block(ore));
    }

    private void op(OpType type, short value){
        painter.addTileOp(TileOp.get(x(), y(), (byte)type.ordinal(), value));
    }
}
