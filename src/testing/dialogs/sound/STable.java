package testing.dialogs.sound;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;

public abstract class STable{
    abstract void createSelection(Table t, TextField search);
    abstract void createPlay(Table t);
    abstract void rebuild();
    abstract void stopSounds();
}
