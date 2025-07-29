package testing.editor;

import arc.func.*;
import mindustry.content.*;
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
        if(skip()){
            tile.setFloor(type);
            return;
        }

        Floor tFloor = floor();
        if(type instanceof OverlayFloor){
            //don't place on liquids
            if(tFloor.hasSurface() || !type.needsSurface){
                setOverlay(type);
            }
            return;
        }

        if(tFloor != type){
            op(PaintOperation.opFloor, tFloor.id);
            tile.setFloor(type);
            type.floorChanged(tile);
        }
    }

    /** Sets the floor, preserving overlay.*/
    public void setFloorUnder(Floor floor){
        Block overlay = overlay();
        setFloor(floor);
        if(overlay() != overlay){
            setOverlay(overlay);
        }
    }

    public void setBlock(Block type){
        setBlock(type, Team.derelict);
    }

    public void setBlock(Block type, Team team){
        setBlock(type, team, 0);
    }

    public void setBlock(Block type, Team team, int rotation){
        setBlock(type, team, rotation, type::newBuilding);
    }

    public void setBlock(Block type, Team team, int rotation, Prov<Building> entityprov){
        if(skip()){
            tile.setBlock(type, team, rotation, entityprov);
            return;
        }

        Block tBlock = block();
        Building tBuild = tile.build;
        if(tBlock != type || !(tBuild == null || tBuild.rotation == rotation)){
            if(type instanceof Cliff){
                painter.pendingCliffs.add(tile);
                tile.data = 0;
            }else if(tBlock instanceof Cliff){
                painter.pendingCliffs.remove(tile);
            }

            if(!isCenter()){
                PaintedTileData cen = painter.data(tBuild.tile);
                cen.op(PaintOperation.opRotation, (byte)tBuild.rotation);
                cen.op(PaintOperation.opTeam, (byte)tBuild.team.id);
                cen.op(PaintOperation.opBlock, tBlock.id);
            }else{
                if(tBuild != null) op(PaintOperation.opRotation, (byte)tBuild.rotation);
                if(tBuild != null) op(PaintOperation.opTeam, (byte)tBuild.team.id);
                op(PaintOperation.opBlock, tBlock.id);
            }

            tile.setBlock(type, team, rotation, entityprov);
        }
    }
    
    public void setTeam(Team team){
        if(skip()){
            tile.setTeam(team);
            return;
        }

        if(getTeamID() == team.id) return;
        op(PaintOperation.opTeam, (byte)getTeamID());
        tile.setTeam(team);
    }

    public void setOverlay(Block overlay){
        if(skip()){
            tile.setOverlay(overlay);
            return;
        }

        Floor tFloor = tile.floor();
        Floor tOverlay = tile.overlay();

        if(!tFloor.hasSurface() && overlay.asFloor().needsSurface && (overlay instanceof OreBlock || !tFloor.supportsOverlay)) return;
        if(tOverlay != overlay){
            op(PaintOperation.opOverlay, tOverlay.id);
            tile.setOverlay(overlay);
        }
    }

    public void setData(byte data, byte floorData, byte overlayData){
        if(skip()){
            tile.data = data;
            tile.floorData = floorData;
            tile.overlayData = overlayData;
            return;
        }

        byte tData = data();
        byte tFloor = floorData();
        byte tOverlay = overlayData();

        if(tData == data && tFloor == floorData && tOverlay == overlayData) return;
        op(PaintOperation.opData, PaintOpData.get(tData, tFloor, tOverlay));

        tile.data = data;
        tile.floorData = floorData;
        tile.overlayData = overlayData;
        tile.recache();
        tile.recacheWall();
    }

    public void setExtraData(int extraData){
        if(skip()){
            tile.extraData = extraData;
            return;
        }

        int tExtraData = extraData();

        if(tExtraData == extraData) return;
        op(PaintOperation.opExtraData, tExtraData);

        tile.extraData = extraData;
        tile.recache();
        tile.recacheWall();
    }

    private boolean skip(){
        return painter.isLoading() || world.isGenerating();
    }
    
    public boolean isCenter(){
        return tile.isCenter();
    }

    public boolean shouldSaveData(){
        return tile.shouldSaveData();
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

    public byte data(){
        return tile.data;
    }

    public byte floorData(){
        return tile.floorData;
    }

    public byte overlayData(){
        return tile.overlayData;
    }

    public int extraData(){
        return tile.extraData;
    }

    public void setOverlayID(short ore){
        setOverlay(content.block(ore));
    }

    public void remove(){
        setBlock(Blocks.air);
    }

    public void clearOverlay(){
        setOverlayID((short)0);
    }

    private void op(byte type, int value){
        painter.addPaintOp(PaintOp.get(x(), y(), type, value));
    }
}
