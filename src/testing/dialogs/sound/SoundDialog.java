package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.scene.ui.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import testing.dialogs.*;
import testing.ui.*;

import static arc.Core.*;

public class SoundDialog extends TUBaseDialog{
    /** Audio bus for sounds played by the dialog. Will remain unpaused unlike other audio busses. */
    private static final AudioBus soundRoomBus = new AudioBus();

    private FilterTable filters = null;
    private final SoundsTable soundsTable;
    private TextField search;

    public SoundDialog(){
        super("@tu-sound-menu.name");

        soundsTable = new SoundsTable(soundRoomBus);

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        soundsTable.createSelection(cont, search);

        TUElements.divider(cont, null, Color.lightGray);

        cont.pane(t -> {
            soundsTable.createPlay(t);
            if(!Core.settings.getBool("tu-allow-filters", false)) return;
            TUElements.divider(t, "Audio Filters", Pal.accent);
            t.table(fil -> {
                fil.add(filters = new FilterTable());
            }).center();
        });

        shown(() -> {
            //Pause the ui audio bus while open so that button press sounds doesn't play.
            audio.setPaused(Sounds.press.bus.id, true);
            if(filters != null) filters.shown();
        });
        hidden(() -> {
            soundsTable.stopSounds();
            if(filters != null) TUFilters.closed();
            audio.setPaused(Sounds.press.bus.id, false);
        });
    }

    @Override
    protected void rebuild(){
        soundsTable.rebuild();
    }
}
