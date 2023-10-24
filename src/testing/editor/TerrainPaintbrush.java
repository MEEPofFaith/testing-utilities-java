package testing.editor;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.editor.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import mindustry.world.*;
import testing.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.util.TUVars.*;

/** Based on {@link MapView} */
public class TerrainPaintbrush{
    private final Vec2[][] brushPolygons = new Vec2[MapEditor.brushSizes.length][0];

    private float hold = 0f;
    private KeyCode button;

    private final Point2 firstTouch = new Point2();
    private PainterTool tool = PainterTool.pencil;
    private PainterTool lastTool;
    private boolean drawing;
    private int lastX, lastY;
    private int startX, startY;
    public boolean drawGrid;

    public TerrainPaintbrush(){
        for(int i = 0; i < MapEditor.brushSizes.length; i++){
            float size = MapEditor.brushSizes[i];
            float mod = size % 1f;
            brushPolygons[i] = Geometry.pixelCircle(size, (index, x, y) -> Mathf.dst(x, y, index - mod, index - mod) <= size - 0.5f);
        }

        Events.run(Trigger.update, () -> {
            if(state.isGame() && Setup.terrainFrag.shown()){
                if(scene.hasMouse()){
                    touchUp(lastX, lastY);
                }else{
                    if(TestUtils.canTeleport()) return;

                    int tx = World.toTile(input.mouseWorldX()),
                        ty = World.toTile(input.mouseWorldY());

                    //if(!TestUtils.anyClick()){
                    if(!TestUtils.click()){
                        hold = 0f;
                        touchUp(tx, ty);
                        return;
                    }
                    player.shooting(false);

                    if(mobile){
                        hold += TUVars.delta();
                        if(hold < 0.43f * 60f) return;
                    }

                    if(!drawing){
                        touchDown(tx, ty, mobile ? KeyCode.mouseLeft : TestUtils.getClick());
                    }else if(lastX != tx || lastY != ty){
                        touchDragged(tx, ty);
                    }
                }
            }
        });

        Events.run(Trigger.draw, () -> {
            Draw.z(Layer.overlayUI);

            if(state.isGame() && Setup.terrainFrag.shown()){
                drawPendingCliffs();
                drawGrid();

                if(!scene.hasMouse()){
                    Draw.z(Layer.overlayUI + 0.02f);
                    drawBrush();
                }
            }
        });
    }

    private void drawGrid(){
        if(!drawGrid) return;

        int x1 = (int)(Math.max(0, camera.position.x - camera.width / 2f - tilesize) / tilesize),
            y1 = (int)(Math.max(0, camera.position.y - camera.height / 2f - tilesize) / tilesize),
            x2 = (int)(Math.min(world.unitWidth(), camera.position.x + camera.width / 2f + tilesize) / tilesize),
            y2 = (int)(Math.min(world.unitHeight(), camera.position.y + camera.height / 2f + tilesize) / tilesize);

        Lines.stroke(1);
        for(int i = x1; i <= x2; i++){
            setGridColor(i);
            Lines.line(i * tilesize - 4f, y1 * tilesize - 4f, i * tilesize - 4f, y2 * tilesize - 4f);
        }
        for(int i = y1; i <= y2; i++){
            setGridColor(i);
            Lines.line(x1 * tilesize - 4f, i * tilesize - 4f, x2 * tilesize - 4f, i * tilesize - 4f);
        }
        Draw.color();
        Draw.z(Layer.overlayUI);
    }

    private void setGridColor(int i){
        if(i % 16 == 0){
            Draw.color(Pal.accent);
            Draw.z(Layer.overlayUI + 0.01f);
        }else{
            Draw.color(Color.gray);
            Draw.z(Layer.overlayUI);
        }
        Draw.alpha(0.5f);
    }

    private void drawPendingCliffs(){
        if(painter.pendingCliffs.isEmpty()) return;

        Draw.alpha(0.75f + Mathf.sinDeg(Time.time * 2f) * 0.25f);
        for(Tile t : painter.pendingCliffs){
            Draw.rect(Blocks.cliff.uiIcon, t.worldx(), t.worldy(), tilesize, tilesize);
        }
        Draw.color();
    }

    private void drawBrush(){
        if(TestUtils.canTeleport()) return;

        int index = 0;
        for(int i = 0; i < MapEditor.brushSizes.length; i++){
            if(painter.brushSize() == MapEditor.brushSizes[i]){
                index = i;
                break;
            }
        }

        Draw.color(Pal.accent);
        Lines.stroke(Scl.scl(2f));

        float x = World.toTile(input.mouseWorldX()) * tilesize,
            y = World.toTile(input.mouseWorldY()) * tilesize;

        if((!painter.drawBlock.isMultiblock() || tool == PainterTool.eraser) && tool != PainterTool.fill){
            if(tool == PainterTool.line && drawing){
                Lines.poly(brushPolygons[index], startX * tilesize - 4f, startY * tilesize - 4f, tilesize);
                Lines.poly(brushPolygons[index], lastX * tilesize - 4f, lastY * tilesize - 4f, tilesize);
            }

            if((tool.edit || (tool == PainterTool.line && !drawing)) && (!mobile || drawing)){
                //pencil square outline
                if(tool == PainterTool.pencil && tool.mode == 1){
                    Lines.square(x, y, (painter.brushSize == 1.5f ? 1f : painter.brushSize) * tilesize + 4f);
                }else{
                    Lines.poly(brushPolygons[index], x - 4f, y - 4f, tilesize);
                }
            }
        }else{
            float size = painter.drawBlock.size * tilesize,
                offset = (1 - painter.drawBlock.size % 2) * tilesize / 2f;

            if(tool == PainterTool.line && drawing){
                Lines.rect(startX * tilesize - size / 2 + offset, startY * tilesize - size / 2 + offset, size, size);
                Lines.rect(lastX * tilesize - size / 2 + offset, lastY * tilesize - size / 2 + offset, size, size);
            }else if((tool.edit || tool == PainterTool.line) && (!mobile || drawing)){
                Lines.rect(x - size / 2 + offset, y - size / 2 + offset, size, size);
            }
        }
        Draw.color();
    }

    private void touchDown(int x, int y, KeyCode button){
        this.button = button;

        //These are already bound to other things - I don't have a way to stop them.
        /*
        if(button == KeyCode.mouseRight){
            lastTool = tool;
            tool = PainterTool.eraser;
        }

        if(button == KeyCode.mouseMiddle){
            lastTool = tool;
            tool = PainterTool.pick;
        }
         */

        startX = x;
        startY = y;
        lastX = x;
        lastY = y;
        tool.touched(x, y);
        firstTouch.set(x, y);

        drawing = true;
    }

    private void touchDragged(int x, int y){
        if(!drawing) return;

        if(tool.draggable && !(x == lastX && y == lastY)){
            Bresenham2.line(lastX, lastY, x, y, (cx, cy) -> tool.touched(cx, cy));
        }

        if(tool == PainterTool.line && tool.mode == 1){
            if(Math.abs(x - firstTouch.x) > Math.abs(y - firstTouch.y)){
                lastX = x;
                lastY = firstTouch.y;
            }else{
                lastX = firstTouch.x;
                lastY = y;
            }
        }else{
            lastX = x;
            lastY = y;
        }
    }

    private void touchUp(int x, int y){
        if(!drawing) return;
        drawing = false;

        if(tool == PainterTool.line){
            tool.touchedLine(startX, startY, x, y);
        }

        painter.flushOp();

        if((button == KeyCode.mouseMiddle || button == KeyCode.mouseRight) && lastTool != null){
            tool = lastTool;
            lastTool = null;
        }
    }

    public PainterTool getTool(){
        return tool;
    }

    public void setTool(PainterTool tool){
        this.tool = tool;
    }
}
