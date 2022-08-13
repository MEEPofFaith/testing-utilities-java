package testing.dialogs;

import arc.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.TextField.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import testing.ui.*;
import testing.util.*;

import java.util.*;

import static arc.math.Interp.*;

public class InterpDialog extends BaseDialog{
    InterpGraph graph;
    Table configTable;
    //Configs
    int configType = 0;
    int powP = 2;
    float expV = 2, expP = 5;
    float elasticV = 2, elasticP = 10, elasticS = 1;
    int elasticB = 6;
    float swingS = 1.5f;
    int bounceB = 4;
    String lastPressed = "linear";

    public InterpDialog(){
        super("@tu-interp-menu.name");

        cont.add(graph = new InterpGraph()).grow();
        cont.row();
        ScrollPane pane = cont.pane(Styles.horizontalPane, p -> {
            p.table(b -> {
                b.defaults().size(140f, 60f);

                /* Button layout (Wow... that's a lot of different interps)
                    Linear    Smooth     Sine      Circle      Pow      Exp      Elastic      Swing      Bounce
                    Reverse   Smooth2    SineIn    CircleIn    PowIn    ExpIn    ElasticIn    SwingIn    BounceIn
                    Slope     Smoother   SineOut   CircleOut   PowOut   ExpOut   ElasticOut   SwingOut   BounceOut
                 */

                TextButtonStyle style = Styles.togglet;
                style.checked = Tex.buttonOver;

                TextButtonStyle oldStyle = Core.scene.getStyle(TextButtonStyle.class);
                Core.scene.addStyle(TextButtonStyle.class, style);

                b.button("linear", () -> {
                    graph.setInterp(linear);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("smooth", () -> {
                    graph.setInterp(smooth);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("sine", () -> {
                    graph.setInterp(sine);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("circle", () -> {
                    graph.setInterp(circle);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("pow", () -> setConfigType(1));
                b.button("exp", () -> setConfigType(4));
                b.button("elastic", () -> setConfigType(7));
                b.button("swing", () -> setConfigType(10));
                b.button("bounce", () -> setConfigType(13));

                b.row();

                b.button("reverse", () -> {
                    graph.setInterp(reverse);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("smooth2", () -> {
                    graph.setInterp(smooth2);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("sineIn", () -> {
                    graph.setInterp(sineIn);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("circleIn", () -> {
                    graph.setInterp(circleIn);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("powIn", () -> setConfigType(2));
                b.button("expIn", () -> setConfigType(5));
                b.button("elasticIn", () -> setConfigType(8));
                b.button("swingIn", () -> setConfigType(11));
                b.button("bounceIn", () -> setConfigType(14));

                b.row();

                b.button("slope", () -> {
                    graph.setInterp(slope);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("smoother", () -> {
                    graph.setInterp(smoother);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("sineOut", () -> {
                    graph.setInterp(sineOut);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("circleOut", () -> {
                    graph.setInterp(circleOut);
                    configType = 0;
                    rebuildConfig();
                });
                b.button("powOut", () -> setConfigType(3));
                b.button("expOut", () -> setConfigType(6));
                b.button("elasticOut", () -> setConfigType(9));
                b.button("swingOut", () -> setConfigType(12));
                b.button("bounceOut", () -> setConfigType(15));

                //ButtonGroup<TextButton> group = new ButtonGroup<>();
                b.getChildren().each(c -> {
                    if(c instanceof TextButton t){
                        //group.add(t);
                        setupButton(t);
                    }
                });

                Core.scene.addStyle(TextButtonStyle.class, oldStyle);
            });
            p.row();
            p.add(configTable = new Table()).height(TUVars.iconSize).padTop(8f).padBottom(8f);
        }).fill().padTop(8f).get();
        pane.setScrollingDisabled(false, true);

        rebuildConfig();
        
        addCloseButton();
    }

    void setupButton(TextButton t){
        t.clicked(() -> lastPressed = t.getText().toString());
        t.update(() -> t.setChecked(Objects.equals(lastPressed, t.getText().toString())));
    }

    void setConfigType(int type){
        configType = type;
        inputInterp();
        rebuildConfig();
    }

    void rebuildConfig(){
        configTable.clear();
        configTable.defaults().height(TUVars.iconSize);

        switch(configType){
            case 1, 2, 3 -> { //Pow
                TUElements.sliderSet(
                    configTable, text -> {
                        powP = Strings.parseInt(text);
                        inputInterp();
                    }, () -> String.valueOf(powP),
                    TextFieldFilter.digitsOnly, s -> Strings.canParseInt(s) && Strings.parseInt(s) > 0,
                    1, 10, 1, powP, (n, f) -> {
                        powP = Mathf.round(n);
                        f.setText(String.valueOf(powP));
                        inputInterp();
                    },
                    "power", null
                );
            }
            case 4, 5, 6 -> { //Exp
                TUElements.sliderSet(
                    configTable, text -> {
                        expV = Strings.parseFloat(text);
                        inputInterp();
                    }, () -> String.valueOf(expV),
                    TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) > 1,
                    1.125f, 10, 0.125f, expV, (n, f) -> {
                        expV = n;
                        f.setText(String.valueOf(expV));
                        inputInterp();
                    },
                    "value", null
                );
                TUElements.sliderSet(
                    configTable, text -> {
                        expP = Strings.parseFloat(text);
                        inputInterp();
                    }, () -> String.valueOf(expP),
                    TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) > 0,
                    0.125f, 10, 0.125f, expP, (n, f) -> {
                        expP = n;
                        f.setText(String.valueOf(expP));
                        inputInterp();
                    },
                    "power", null
                );
            }
            case 7, 8, 9 -> { //Elastic
                TUElements.sliderSet(
                    configTable, text -> {
                        elasticV = Strings.parseFloat(text);
                        inputInterp();
                    }, () -> String.valueOf(elasticV),
                    TextFieldFilter.floatsOnly, s -> Strings.canParseFloat(s) && Strings.parseFloat(s) > 0,
                    1f, 10, 0.125f, elasticV, (n, f) -> {
                        elasticV = n;
                        f.setText(String.valueOf(elasticV));
                        inputInterp();
                    },
                    "value", null
                );
                TUElements.sliderSet(
                    configTable, text -> {
                        elasticP = Strings.parseFloat(text);
                        inputInterp();
                    }, () -> String.valueOf(elasticP),
                    TextFieldFilter.floatsOnly, Strings::canParseFloat,
                    0, 10, 0.125f, elasticP, (n, f) -> {
                        elasticP = n;
                        f.setText(String.valueOf(elasticP));
                        inputInterp();
                    },
                    "power", null
                );
                TUElements.sliderSet(
                    configTable, text -> {
                        elasticB = Strings.parseInt(text);
                        inputInterp();
                    }, () -> String.valueOf(elasticB),
                    TextFieldFilter.digitsOnly, Strings::canParseInt,
                    0, 10, 1, elasticB, (n, f) -> {
                        elasticB = Mathf.round(n);
                        f.setText(String.valueOf(elasticB));
                        inputInterp();
                    },
                    "bounces", null
                );
                TUElements.sliderSet(
                    configTable, text -> {
                        elasticS = Strings.parseFloat(text);
                        inputInterp();
                    }, () -> String.valueOf(elasticS),
                    TextFieldFilter.floatsOnly, Strings::canParseFloat,
                    0, 10, 0.125f, elasticS, (n, f) -> {
                        elasticS = n;
                        f.setText(String.valueOf(elasticS));
                        inputInterp();
                    },
                    "scale", null
                );
            }
            case 10, 11, 12 -> { //Swing
                TUElements.sliderSet(
                    configTable, text -> {
                        swingS = Strings.parseFloat(text);
                        inputInterp();
                    }, () -> String.valueOf(swingS),
                    TextFieldFilter.floatsOnly, Strings::canParseFloat,
                    0, 10, 0.125f, swingS, (n, f) -> {
                        swingS = n;
                        f.setText(String.valueOf(swingS));
                        inputInterp();
                    },
                    "scale", null
                );
            }
            case 13, 14, 15 -> { //Bounce
                TUElements.sliderSet(
                    configTable, text -> {
                        bounceB = Strings.parseInt(text);
                        inputInterp();
                    }, () -> String.valueOf(bounceB),
                    TextFieldFilter.digitsOnly, s -> Strings.canParseInt(s) && Strings.parseInt(s) >= 2 && Strings.parseInt(s) <= 5,
                    2, 5, 1, bounceB, (n, f) -> {
                        bounceB = Mathf.round(n);
                        f.setText(String.valueOf(bounceB));
                        inputInterp();
                    },
                    "bounces", null
                );
            }
        }
    }

    void inputInterp(){
        Interp newInterp = switch(configType){
            case 1 -> new Pow(powP);
            case 2 -> new PowIn(powP);
            case 3 -> new PowOut(powP);
            case 4 -> new Exp(expV, expP);
            case 5 -> new ExpIn(expV, expP);
            case 6 -> new ExpOut(expV, expP);
            case 7 -> new Elastic(elasticV, elasticP, elasticB, elasticS);
            case 8 -> new ElasticIn(elasticV, elasticP, elasticB, elasticS);
            case 9 -> new ElasticOut(elasticV, elasticP, elasticB, elasticS);
            case 10 -> new Swing(swingS);
            case 11 -> new SwingIn(swingS);
            case 12 -> new SwingOut(swingS);
            case 13 -> new Bounce(bounceB);
            case 14 -> new BounceIn(bounceB);
            case 15 -> new BounceOut(bounceB);
            default -> null;
        };

        if(newInterp != null){
            graph.setInterp(newInterp);
        }
    }
}
