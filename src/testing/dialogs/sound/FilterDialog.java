package testing.dialogs.sound;

import arc.scene.ui.layout.*;
import mindustry.ui.*;
import testing.dialogs.*;
import testing.dialogs.sound.TUFilters.*;

public class FilterDialog extends TUBaseDialog{
    private final Table config = new Table();
    private FilterModule<?> lastFilter;

    public FilterDialog(){
        super("@tu-filter-menu.name");
        TUFilters.init();

        cont.table(sel -> {
            int col = 0;
            for(int i = 0; i < TUFilters.filters.length; i++){
                FilterModule<?> fm = TUFilters.filters[i];
                col++;
                sel.button(fm.name, () -> setConfig(fm))
                    .checked(t -> fm.enabled)
                    .wrapLabel(false).uniform().grow()
                    .get().setStyle(Styles.togglet);
                if(col == 4){
                    sel.row();
                    col = 0;
                }
            }
        }).fillX().row();

        cont.add(config).padTop(6);

        setConfig(TUFilters.filters[0]);
        shown(() -> setConfig(lastFilter));
    }

    private void setConfig(FilterModule<?> fm){
        config.clear();
        lastFilter = fm;
        config.table(t -> {
            t.defaults().left().top();
            fm.buildUI(t);
        }).growX().top();
    }
}
