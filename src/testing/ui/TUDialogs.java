package testing.ui;

import testing.dialogs.*;
import testing.dialogs.sound.*;
import testing.dialogs.world.*;

public class TUDialogs{
    public static UnitDialog unitDialog;
    public static BlockDialog blockDialog;
    public static TeamDialog teamDialog;
    public static WaveChangeDialog waveChangeDialog;
    public static StatusDialog statusDialog;
    public static WorldDialog worldDialog;
    public static InterpDialog interpDialog;
    public static FilterDialog filterDialog;
    public static SoundDialog soundDialog;

    public static void load(){
        unitDialog = new UnitDialog();
        blockDialog = new BlockDialog();
        teamDialog = new TeamDialog();
        waveChangeDialog = new WaveChangeDialog();
        statusDialog = new StatusDialog();
        worldDialog = new WorldDialog();
        interpDialog = new InterpDialog();
        filterDialog = new FilterDialog();
        soundDialog = new SoundDialog();
    }
}
