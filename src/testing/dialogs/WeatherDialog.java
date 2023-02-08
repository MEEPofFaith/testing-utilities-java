package testing.dialogs;

import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class WeatherDialog extends TUBaseDialog{
    TextField search;
    Table selection = new Table();
    Weather weather = Weathers.rain;
    float intensity = 100f, duration = 60f;

    float minDur = 0.125f, maxDur = 600f;

    public WeatherDialog(){
        super("@tu-weather-menu.name");

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all -> {
            all.add(selection);
            all.row();

            all.table(s -> {
                TUElements.sliderSet(
                    s, text -> intensity = Mathf.clamp(Strings.parseFloat(text), 0f, 100f), () -> String.valueOf(intensity),
                    TextFieldFilter.floatsOnly, Strings::canParsePositiveFloat,
                    0f, 100f, 1f, intensity, (n, f) -> {
                        intensity = Mathf.clamp(n, 0f, 100f);
                        f.setText(String.valueOf(intensity));
                    },
                    "@tu-weather-menu.intensity",
                    "@tu-tooltip.weather-intensity"
                );
                s.row();

                TUElements.sliderSet(
                    s, text -> duration = Strings.parseFloat(text), () -> String.valueOf(duration),
                    TextFieldFilter.floatsOnly, Strings::canParsePositiveFloat,
                    minDur, maxDur, 0.125f, duration, (n, f) -> {
                        duration = n;
                        f.setText(String.valueOf(n));
                    },
                    "@tu-status-menu.duration",
                    "@tu-tooltip.weather-duration"
                );
            });
            all.row();

            ImageButton wb = all.button(TUIcons.get(Icon.add), TUVars.buttonSize, this::createWeather).get();
            TUElements.boxTooltip(wb, "@tu-tooltip.weather-create");
            wb.label(() -> "@tu-weather-menu.create").padLeft(6).growX();
            wb.setDisabled(() -> intensity <= 0 || duration <= 0);
            all.row();

            all.table(b -> {
                ImageButton rb = b.button(TUIcons.get(Icon.cancel), TUStyles.lefti, TUVars.buttonSize, this::removeWeather).get();
                TUElements.boxTooltip(rb, "@tu-tooltip.weather-remove");
                rb.label(() -> "@tu-weather-menu.remove").padLeft(6).growX();

                ImageButton cb = b.button(TUIcons.get(Icon.trash), TUStyles.righti, TUVars.buttonSize, this::clearWeather).get();
                cb.label(() -> "@tu-weather-menu.clear").padLeft(6).growX();
            });
        });
    }

    @Override
    protected void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.label(
            () -> bundle.get("tu-menu.selection") + weather.localizedName
        ).padBottom(6);
        selection.row();

        Seq<Weather> array = content.<Weather>getBy(ContentType.weather).select(w -> w.localizedName.toLowerCase().contains(text.toLowerCase()));
        selection.table(list -> {
            list.left().defaults().minWidth(250);

            int cols = 3;
            int count = 0;

            for(Weather w : array){
                TextButton button = list.button(w.localizedName, () -> weather = w).uniform().grow().get();
                //button.getLabel().setWrap(false);
                if(w.fullIcon.found()){
                    button.add(new Image(w.fullIcon));
                    button.getCells().reverse();
                }

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
        selection.row();
    }

    void createWeather(){
        if(input.shift()){
            Utils.copyJS("Vars.content.getByID(ContentType.weather, @).create(@, @);",
                weather.id, intensity / 100f, duration * 60f
            );
            return;
        }

        weather.create(intensity / 100f, duration * 60f);
    }

    void removeWeather(){
        if(input.shift()){
            Utils.copyJS("Groups.weather.each(w => w.weather == weather, w => w.remove());");
            return;
        }

        Groups.weather.each(w -> w.weather == weather, WeatherState::remove);
    }

    void clearWeather(){
        Groups.weather.each(WeatherState::remove);
    }
}
