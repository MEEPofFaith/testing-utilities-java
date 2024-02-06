package testing.dialogs.sound;

import arc.scene.ui.layout.*;
import testing.dialogs.*;
import testing.dialogs.sound.TUFilters.*;

public class FilterDialog extends TUBaseDialog{
    private final Table config = new Table();

    public FilterDialog(){
        super("@tu-filter-menu.name");
        TUFilters.init();

        cont.table(sel -> {
            int col = 0;
            for(int i = 0; i < TUFilters.filters.length; i++){
                FilterModule<?> fm = TUFilters.filters[i];
                col++;
                sel.button(fm.name, () -> setConfig(fm)).uniform().growX();
                if(col == 4){
                    sel.row();
                    col = 0;
                }
            }
        }).growX();

        cont.row();

        cont.table(t -> t.add(config));

        setConfig(TUFilters.filters[0]);
    }

    private void setConfig(FilterModule<?> fm){
        config.clear();
        config.table(fm::buildUI).growX().top();
    }
}
