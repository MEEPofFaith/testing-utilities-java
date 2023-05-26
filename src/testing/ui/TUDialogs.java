package testing.ui;

import arc.*;
import testing.dialogs.*;
import testing.dialogs.world.*;

public class TUDialogs{
    public static UnitDialog unitDialog;
    public static BlockDialog blockDialog;
    public static TeamDialog teamDialog;
    public static WaveChangeDialog waveChangeDialog;
    public static StatusDialog statusDialog;
    public static WorldDialog worldDialog;
    public static InterpDialog interpDialog;
    public static SoundDialog soundDialog;
    public static FieldEditor fieldEditor;

    public static void load(){
        unitDialog = new UnitDialog();
        blockDialog = new BlockDialog();
        teamDialog = new TeamDialog();
        waveChangeDialog = new WaveChangeDialog();
        statusDialog = new StatusDialog();
        worldDialog = new WorldDialog();
        interpDialog = new InterpDialog();
        soundDialog = new SoundDialog();
        if(Core.settings.getBool("tu-field-editor")) fieldEditor = new FieldEditor();
    }
}
