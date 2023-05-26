package testing.dialogs.world;

import mindustry.graphics.*;
import testing.dialogs.*;
import testing.ui.*;

public class WorldDialog extends TUBaseDialog{
    private final PlanetTable planetTable;
    private final WeatherTable weatherTable;

    public WorldDialog(){
        super("@tu-world-menu.name");

        TUElements.divider(cont, "@tu-planet-menu.name", Pal.accent);
        planetTable = new PlanetTable();
        planetTable.top();
        cont.add(planetTable).growX().top().row();

        TUElements.divider(cont, "@tu-weather-menu.name", Pal.accent);
        weatherTable = new WeatherTable();
        weatherTable.top();
        cont.add(weatherTable).growX().top();
    }

    @Override
    protected void rebuild(){
        planetTable.rebuild();
        weatherTable.rebuild();
    }
}
