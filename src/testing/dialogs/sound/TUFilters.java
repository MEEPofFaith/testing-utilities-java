package testing.dialogs.sound;

import arc.*;
import arc.audio.*;
import arc.audio.Filters.*;
import arc.func.*;
import arc.scene.ui.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.graphics.*;
import testing.ui.*;

public class TUFilters{
    public static FilterModule<?>[] filters;
    private static boolean init;

    public static void init(){
        filters = new FilterModule[]{
            new FilterModule<BiquadFilter>("Biquad", new BiquadFilter(){{
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
                            sl.add("Type").padRight(6f).right();
                            sl.button("Wet", () -> {
                                    type = !type;
                                    update.run();
                                }).checked(type)
                                .update(tb -> tb.setText(type ? "Dry" : "Wet"))
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
                                }, "Frequency", ""
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
                                }, "Resonance", ""
                            );
                        });
                    };
                }
            },
            new FilterModule<EchoFilter>("Echo", new EchoFilter(){{
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
                                }, "Delay", ""
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
                                }, "Decay", ""
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
                                }, "Filter", ""
                            );
                        });
                    };
                }
            },
            new FilterModule<LofiFilter>("Lofi", new LofiFilter(){{
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
                                }, "Sample Rate", ""
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
                                }, "Depth", ""
                            );
                        });
                    };
                }
            },
            new FilterModule<FlangerFilter>("Flanger", new FlangerFilter(){{
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
                                }, "Delay", ""
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
                                }, "Frequency", ""
                            );
                        });
                    };
                }
            },
            new FilterModule<WaveShaperFilter>("Wave Shaper", new WaveShaperFilter(){{
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
                                }, "Amount", ""
                            );
                        });
                    };
                }
            },
            new FilterModule<BassBoostFilter>("Bass Boost", new BassBoostFilter(){{
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
                                }, "Amount", ""
                            );
                        });
                    };
                }
            },
            new FilterModule<RobotizeFilter>("Robotize", new RobotizeFilter(){{
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
                                }, "Amount", ""
                            );
                            sl.row();
                            sl.add("Waveform").padRight(6f).right();
                            sl.table(w -> {
                                w.defaults().left();
                                ButtonGroup<CheckBox> group = new ButtonGroup<>();
                                for(RobotizeWaveforms f : RobotizeWaveforms.values()){
                                    w.check(f.name(), b -> {
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
            new FilterModule<FreeverbFilter>("Freeverb", new FreeverbFilter(){{
                set(0, 0.5f, 0.5f, 1);
            }}){
                float mode = 0;
                float rms = 0.5f;
                float damp = 0.5f;
                float width = 1;

                {
                    update = () -> {
                        filter.set(mode, rms, damp, width);
                        filterUpdate();
                    };
                    buildUI = (t, a) -> {
                        addHeader(t);
                        t.table(sl -> {
                            TUElements.sliderSet(
                                sl, text -> {
                                    mode = Strings.parseFloat(text);
                                    update.run();
                                }, () -> String.valueOf(mode),
                                TextFieldFilter.floatsOnly, null,
                                0f, 1f, 0.01f, mode, (n, f) -> {
                                    mode = n;
                                    f.setText(String.valueOf(n));
                                    update.run();
                                }, "Mode", ""
                            );
                            sl.row();
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
                                }, "Room Size", ""
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
                                }, "Damp", ""
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
                                }, "Width", ""
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
            TUElements.divider(t, name + " Filter", Pal.accent);
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
