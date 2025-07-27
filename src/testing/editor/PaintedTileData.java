package testing.editor;

import arc.func.*;
import mindustry.content.*;
import mindustry.editor.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static arc.Core.settings;
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
            type.placeEnded(tile, null, 0, type.lastConfig);
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

        if(tFloor == type) return;
        op(PaintOperation.opFloor, tFloor.id);

        tile.setFloor(type);
        setConfig(type, 0);
        type.floorChanged(tile);
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
            type.placeEnded(tile, null, 0, type.lastConfig);
            return;
        }

        Block tBlock = block();
        Building tBuild = tile.build;
        if(tBlock == type && (tBuild == null || tBuild.rotation == rotation)){
            return;
        }

        byte data = 0;
        if(type instanceof Cliff){
            painter.pendingCliffs.add(tile);
            tile.data = 0;
        }else if(tBlock instanceof Cliff){
            painter.pendingCliffs.remove(tile);
            data = tile.data;
            tile.data = 0;
        }

        if(!isCenter()){
            PaintedTileData cen = painter.data(tBuild.tile);
            cen.op(PaintOperation.opRotation, (byte)tBuild.rotation);
            cen.op(PaintOperation.opTeam, (byte)tBuild.team.id);
            cen.op(PaintOperation.opBlock, tBlock.id, data);
        }else{
            if(tBuild != null) op(PaintOperation.opRotation, (byte)tBuild.rotation);
            if(tBuild != null) op(PaintOperation.opTeam, (byte)tBuild.team.id);
            op(PaintOperation.opBlock, tBlock.id, data);
        }

        tile.setBlock(type, team, rotation, entityprov);
        setConfig(type, rotation);
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
            overlay.placeEnded(tile, null, 0, overlay.lastConfig);
            return;
        }

        Floor tFloor = tile.floor();
        Floor tOverlay = tile.overlay();

        if(!tFloor.hasSurface() && overlay.asFloor().needsSurface && (overlay instanceof OreBlock || !tFloor.supportsOverlay)) return;
        if(tOverlay == overlay) return;
        op(PaintOperation.opOverlay, tOverlay.id);
        tile.setOverlay(overlay);
        setConfig(overlay, 0);
    }

    public void setConfig(Block block, int rotation){
        dataOp(tile.floorData, tile.overlayData, tile.extraData);
        block.placeEnded(tile, null, rotation, block.lastConfig);
    }

    public void setData(byte floorData, byte overlayData, int extraData){
        if(!settings.getBool("tu-data-painting", false)) return;

        if(skip()){
            tile.floorData = floorData;
            tile.overlayData = overlayData;
            tile.extraData = extraData;
            return;
        }

        byte tFloor = floorData();
        byte tOverlay = overlayData();
        int tExtra = extraData();

        if(tFloor == floorData && tOverlay == overlayData && tExtra == extraData) return;
        op(PaintOperation.opData, (short)0);
        dataOp(tFloor, tOverlay, tExtra);

        tile.floorData = floorData;
        tile.overlayData = overlayData;
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

    private void op(byte type, short value){
        op(type, value, (byte)0);
    }

    private void op(byte type, short value, byte data){
        painter.addPaintOp(PaintOp.get(x(), y(), type, value, data));
    }

    private void dataOp(byte floorData, byte overlayData, int extraData){
        painter.addDataOp(
            PaintData.get(floorData, overlayData, extraData)
        );
    }
}
