package testing.ui;

import arc.scene.event.*;
import mindustry.gen.*;
import testing.dialogs.*;

public class TUDialogs{
    public static UnitDialog unitDialog;
    public static BlockDialog blockDialog;
    public static TeamDialog teamDialog;
    public static WaveChangeDialog waveChangeDialog;
    public static StatusDialog statusDialog;
    public static WeatherDialog weatherDialog;
    public static InterpDialog interpDialog;
    public static SoundDialog soundDialog;

    public static void load(){
        unitDialog = new UnitDialog();
        blockDialog = new BlockDialog();
        teamDialog = new TeamDialog();
        waveChangeDialog = new WaveChangeDialog();
        statusDialog = new StatusDialog();
        weatherDialog = new WeatherDialog();
        interpDialog = new InterpDialog();
        soundDialog = new SoundDialog();

        //Prevent the press sound from playing when clicking buttons in the sounds dialog
        ClickListener.clicked = () -> {
            if(!soundDialog.isShown()) Sounds.press.play();
        };
    }
}
