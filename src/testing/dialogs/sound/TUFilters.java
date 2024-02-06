package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.audio.Filters.*;
import arc.func.*;
import arc.scene.ui.layout.*;

public class TUFilters{
    public static FilterModule<?>[] filters;
    private static boolean initialized = false;

    public static void init(){
        filters = new FilterModule[]{
            new FilterModule<BiquadFilter>("biquad", new BiquadFilter(){{
                set(0, 500, 1);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    t.label(() -> name);
                    addToggle(t);
                };
            }},
            new FilterModule<EchoFilter>("echo", new EchoFilter(){{
                set(0.5f, 0.7f, 0.95f);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    addToggle(t);
                };
            }},
            new FilterModule<LofiFilter>("lofi", new LofiFilter(){{
                set(8000, 5);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    addToggle(t);
                };
            }},
            new FilterModule<FlangerFilter>("flanger", new FlangerFilter(){{
                set(0.5f, 5);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    addToggle(t);
                };
            }},
            new FilterModule<WaveShaperFilter>("waveshaper", new WaveShaperFilter(){{
                set(1);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    addToggle(t);
                };
            }},
            new FilterModule<BassBoostFilter>("bassboost", new BassBoostFilter(){{
                set(6);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    addToggle(t);
                };
            }},
            new FilterModule<RobotizeFilter>("robotize", new RobotizeFilter(){{
                set(30, 0);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    addToggle(t);
                };
            }},
            new FilterModule<FreeverbFilter>("freeverb", new FreeverbFilter(){{
                set(0, 0.5f, 0.5f, 1);
            }}){{
                buildUI = (t, a) -> {
                    //TODO
                    addToggle(t);
                };
            }}
        };
    }

    protected void setupFilters(){
        if(initialized) return;
        for(FilterModule<?> fm : filters){
            Core.audio.setFilter(fm.id, fm.filter);
            Core.audio.setFilterParam(0, fm.id, Filters.paramWet, 0f);
        }
        initialized = true;
    }

    public void stopAll(){
        if(!initialized) return;
        for(FilterModule<?> fm : filters){
            fm.enabled = false;
        }
    }

    public static class FilterModule<A extends AudioFilter>{
        private static int idCount = 3; //Start at 3, vanilla uses the lower ids.
        public final int id;
        public String name;
        public A filter;
        public boolean enabled;
        protected Cons2<Table, A> buildUI;

        public FilterModule(String name, A filter){
            id = idCount++;
            this.name = name;
            this.filter = filter;
        }

        public void buildUI(Table t){
            buildUI.get(t, filter);
        }

        protected void addToggle(Table t){
            t.check("mod.enable", b -> enabled = b);
        }
    }

    public enum RobotizeWaveforms{
        square(0),
        saw(1),
        sin(2),
        triangle(3),
        bounce(4),
        jaws(5),
        humps(6),
        fsquare(7),
        fsaw(8);

        int type;

        RobotizeWaveforms(int type){
            this.type = type;
        }
    }
}
