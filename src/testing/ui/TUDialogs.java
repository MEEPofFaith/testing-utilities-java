package testing.ui;

import testing.dialogs.*;

public class TUDialogs{
    public static UnitDialog unitDialog;
    public static TeamDialog teamDialog;
    public static WaveChangeDialog waveChangeDialog;
    public static StatusDialog statusDialog;

    public static void load(){
        unitDialog = new UnitDialog();
        teamDialog = new TeamDialog();
        waveChangeDialog = new WaveChangeDialog();
        statusDialog = new StatusDialog();
    }
}
