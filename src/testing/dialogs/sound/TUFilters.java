package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.audio.Filters.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import testing.ui.*;

public class TUFilters{
    public static FilterModule<?>[] filters;
    private static boolean init;

    public static void init(){
        filters = new FilterModule[]{
            new FilterModule<BiquadFilter>("biquad", new BiquadFilter(){{
                set(0, 500, 1);
            }}){
                boolean type = false;
                float freq = 500f;
                float res = 1f;

                {
                    update = () -> {
                        filter.set(type ? 1 : 0, freq, res);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            sl.add("@tu-filters-biquad-type").padRight(6f).right();
                            sl.button("@tu-filters-biquad-wet", () -> {
                                    type = !type;
                                    update.run();
                                }).checked(type)
                                .update(tb -> tb.setText("@tu-filters-biquad-" + (type ? "dry" : "wet")))
                                .wrapLabel(false)
                                .colspan(2).left();
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    freq = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(freq),
                                TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) > 0,
                                10f, 1000f, 1f, freq, (n, f) -> {
                                    freq = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-freq", "@tu-filters-biquad-freq-desc"
                            );
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    res = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(res),
                                TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) > 0,
                                0.1f, 3f, 0.1f, res, (n, f) -> {
                                    res = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-biquad-res", "@tu-filters-biquad-res-desc"
                            );
                        });
                    };
                }
            },
            new FilterModule<EchoFilter>("echo", new EchoFilter(){{
                set(0.5f, 0.7f, 0.95f);
            }}){
                float del = 0.5f;
                float dec = 0.7f;
                float fil = 0.95f;

                {
                    update = () -> {
                        filter.set(del, dec, fil);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    del = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(del),
                                TextFieldFilter.floatsOnly, null,
                                0f, 10f, 0.01f, del, (n, f) -> {
                                    del = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-del", "@tu-filters-echo-del-desc"
                            );
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    dec = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(dec),
                                TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) <= 1,
                                0.1f, 1f, 0.01f, dec, (n, f) -> {
                                    dec = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-echo-dec", "@tu-filters-echo-dec-desc"
                            );
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    fil = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(fil),
                                TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) <= 1,
                                0f, 1f, 0.01f, fil, (n, f) -> {
                                    fil = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-echo-fil", "@tu-filters-echo-fil-desc"
                            );
                        });
                    };
                }
            },
            new FilterModule<LofiFilter>("lofi", new LofiFilter(){{
                set(8000, 5);
            }}){
                float sr = 8000;
                float dep = 5;

                {
                    update = () -> {
                        filter.set(sr, dep);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    sr = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(sr),
                                TextFieldFilter.floatsOnly, null,
                                0f, 16000f, 1f, sr, (n, f) -> {
                                    sr = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-lofi-sr", null
                            );
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    dep = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(dep),
                                TextFieldFilter.floatsOnly, null,
                                0f, 10f, 0.01f, dep, (n, f) -> {
                                    dep = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-lofi-dep", null
                            );
                        });
                    };
                }
            },
            new FilterModule<FlangerFilter>("flanger", new FlangerFilter(){{
                set(0.005f, 1f);
            }}){
                float del = 0.005f;
                float freq = 1f;
                {
                    update = () -> {
                        filter.set(del, freq);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    del = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(del),
                                TextFieldFilter.floatsOnly, null,
                                0f, 1f, 0.001f, del, (n, f) -> {
                                    del = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-del", "@tu-filters-flanger-del-desc"
                            );
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    freq = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(freq),
                                TextFieldFilter.floatsOnly, null,
                                0f, 10f, 0.01f, freq, (n, f) -> {
                                    freq = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-freq", "@tu-filters-flanger-freq-desc"
                            );
                        });
                    };
                }
            },
            new FilterModule<WaveShaperFilter>("waveshaper", new WaveShaperFilter(){{
                set(1);
            }}){
                float am = 1f;

                {
                    update = () -> {
                        filter.set(am);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    am = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(am),
                                TextFieldFilter.floatsOnly, null,
                                0f, 2f, 0.01f, am, (n, f) -> {
                                    am = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-amount", "@tu-filters-waveshaper-amount-desc"
                            );
                        });
                    };
                }
            },
            new FilterModule<BassBoostFilter>("bassboost", new BassBoostFilter(){{
                set(6);
            }}){
                float am = 6f;

                {
                    update = () -> {
                        filter.set(am);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    am = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(am),
                                TextFieldFilter.floatsOnly, null,
                                0f, 10f, 0.01f, am, (n, f) -> {
                                    am = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-amount", "@tu-filters-bassboost-amount-desc"
                            );
                        });
                    };
                }
            },
            new FilterModule<RobotizeFilter>("robotize", new RobotizeFilter(){{
                set(30, 0);
            }}){
                float freq = 30;
                int wav = 0;

                {
                    update = () -> {
                        filter.set(freq, wav);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    freq = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(freq),
                                TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) > 0,
                                0.1f, 100f, 0.1f, freq, (n, f) -> {
                                    freq = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-amount", "@tu-filters-robotize-amount-desc"
                            );
                            sl.row();
                            sl.add("@tu-filters-robotize-wav").padRight(6f).right();
                            sl.table(w -> {
                                w.defaults().left();
                                ButtonGroup<CheckBox> group = new ButtonGroup<>();
                                for(RobotizeWaveforms f : RobotizeWaveforms.values()){
                                    w.check("@tu-filters-robotize-" + f.name(), b -> {
                                        wav = f.type;
                                        update.run();
                                    }).group(group).checked(wav == f.type);
                                    w.row();
                                }
                            }).colspan(2).left();
                        });
                    };
                }
            },
            new FilterModule<FreeverbFilter>("freeverb", new FreeverbFilter(){{
                set(0, 0.5f, 0.5f, 1);
            }}){
                float rms = 0.5f;
                float damp = 0.5f;
                float width = 1;

                {
                    update = () -> {
                        filter.set(0, rms, damp, width);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    rms = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(rms),
                                TextFieldFilter.floatsOnly, null,
                                0f, 1f, 0.01f, rms, (n, f) -> {
                                    rms = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-freeverb-rms", null
                            );
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    damp = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(damp),
                                TextFieldFilter.floatsOnly, null,
                                0f, 1f, 0.01f, damp, (n, f) -> {
                                    damp = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-freeverb-damp", null
                            );
                            sl.row();
                            TUElements.sliderSet(
                                sl, text -> {
                                    width = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(width),
                                TextFieldFilter.floatsOnly, null,
                                0f, 2f, 0.01f, width, (n, f) -> {
                                    width = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "@tu-filters-freeverb-width", null
                            );
                        });
                    };
                }
            }
        };

        setupFilters();
    }

    protected static void setupFilters(){
        if(init) return;
        for(FilterModule<?> fm : filters){
            Core.audio.setFilter(fm.id, fm.filter);
            Core.audio.setFilterParam(0, fm.id, Filters.paramWet, 0f);
        }
        init = true;
    }

    public static void closed(){
        if(!init) return;
        for(FilterModule<?> fm : filters){
            fm.enabled = false;
            Core.audio.setFilterParam(0, fm.id, Filters.paramWet, 0);
        }
    }

    public static class FilterModule<A extends AudioFilter>{
        private static int idCount = 0;
        public final int id;
        public String name;
        public A filter;
        public boolean enabled;
        protected Runnable update;
        protected Cons2<Table, A> buildUI;

        public FilterModule(String name, A filter){
            id = idCount++;
            this.name = name;
            this.filter = filter;
        }

        public void enable(boolean b){
            enabled = b;
            Core.audio.setFilterParam(0, id, Filters.paramWet, enabled ? 1f : 0f);
        }

        public void filterUpdate(){
            Core.audio.setFilter(id, filter);
            Core.audio.setFilterParam(0, id, Filters.paramWet, enabled ? 1 : 0);
        }

        public void buildUI(Table t){
            buildUI.get(t, filter);
        }

        protected void addHeader(Table t){
            TUElements.divider(t, Core.bundle.get("tu-filters-" + name) + " " + Core.bundle.get("tu-filters-filter"), Color.lightGray);
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
