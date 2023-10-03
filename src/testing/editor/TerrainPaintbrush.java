package testing.editor;

import arc.*;
import mindustry.editor.*;
import mindustry.game.EventType.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/** Based on {@link MapView} */
public class TerrainPaintbrush{
    private PainterTool tool = PainterTool.none;
    private EditorTool lastTool;
    private boolean drawing;
    private int lastx, lasty;
    private int startx, starty;
    private float mousex, mousey;

    public TerrainPaintbrush(){
        Events.run(Trigger.update, () -> {
            if(state.isGame()){
                if(!scene.hasMouse()){

                }else{

                }
            }
        });
    }



    public PainterTool getTool(){
        return tool;
    }

    public void setTool(PainterTool tool){
        this.tool = tool;
    }
}
