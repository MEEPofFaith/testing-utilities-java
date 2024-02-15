package testing.dialogs.sound;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.ui.*;
import testing.dialogs.sound.TUFilters.*;

public class FilterTable extends Table{
    private final Table config = new Table();
    private FilterModule<?> lastFilter;

    public FilterTable(){
        TUFilters.init();
        table(sel -> {
            int col = 0;
            for(int i = 0; i < TUFilters.filters.length; i++){
                FilterModule<?> fm = TUFilters.filters[i];
                col++;
                TextButton tb = sel.button("@tu-filters-" + fm.name, () -> setConfig(fm))
                    .checked(t -> fm == lastFilter)
                    .wrapLabel(false).uniform().grow().get();
                tb.setStyle(Styles.togglet);
                tb.check("", fm::enable).get().setChecked(fm.enabled);
                tb.getCells().reverse();

                if(col == 4){
                    sel.row();
                    col = 0;
                }
            }
        }).fillX().row();

        add(config).padTop(6);

        setConfig(TUFilters.filters[0]);
    }

    public void shown(){
        setConfig(lastFilter);
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
