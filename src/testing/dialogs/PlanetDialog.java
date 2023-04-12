package testing.dialogs;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import testing.ui.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class PlanetDialog extends TUBaseDialog{
    private final Table selection = new Table();
    private TextField search;
    private Planet planet = Planets.serpulo;

    public PlanetDialog(){
        super("@tu-planet-menu.name");

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all -> {
            all.add(selection);
        });

        TUElements.boxTooltip(
            buttons.button("$tu-planet-menu.set", Icon.editor, this::setPlanet).get(),
            "@tu-tooltip.planet-set"
        );
    }

    @Override
    protected void rebuild(){
        selection.clear();
        String text = search.getText();

        selection.label(
            () -> bundle.get("tu-menu.selection") + (planet == null ? bundle.get("rules.anyenv") : "[#" + planet.iconColor + "]" + planet.localizedName)
        ).padBottom(6);
        selection.row();
        Seq<Planet> array = content.planets().select(e -> text.isEmpty() || e.localizedName.toLowerCase().contains(text.toLowerCase()));
        selection.table(list -> {
            list.left().defaults().minWidth(250);

            int cols = 3;
            int count = 0;

            for(Planet p : array){
                TextButton button = list.button(p.localizedName, () -> planet = p).uniform().grow().get();
                //button.getLabel().setWrap(false);
                button.image(Icon.icons.get(p.icon, Icon.commandRallySmall)).color(p.iconColor);
                button.getCells().reverse();

                if((++count) % cols == 0){
                    list.row();
                }
            }

            TextButton button = list.button("@rules.anyenv", () -> planet = null).uniform().grow().get();
            //button.getLabel().setWrap(false);
            button.add(new Image(Icon.none));
            button.getCells().reverse();
        }).growX().left().padBottom(10);
    }

    private void setPlanet(){
        if(planet == null){
            state.rules.env = Vars.defaultEnv;
            state.rules.attributes.clear();
            state.rules.hiddenBuildItems.clear();
        }else{
            state.rules.env = planet.defaultEnv;
            state.rules.attributes.clear();
            state.rules.attributes.add(planet.defaultAttributes);
            state.rules.hiddenBuildItems.clear();
            state.rules.hiddenBuildItems.addAll(planet.hiddenItems);
        }
        hide();
    }

    public TextureRegion getIcon(){
        if(planet == null) return Icon.none.getRegion();
        return Icon.icons.get(planet.icon, Icon.commandRally).getRegion();
    }

    public Color getIconColor(){
        if(planet == null) return Color.white;
        return planet.iconColor;
    }
}
