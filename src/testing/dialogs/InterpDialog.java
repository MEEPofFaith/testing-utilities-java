package testing.dialogs;

import mindustry.ui.dialogs.*;

import static arc.math.Interp.*;;

public class InterpDialog extends BaseDialog{
    InterpGraph graph;
    //Configs
    int configType = 0;
    float powP = 2;
    float expV = 2, expP = 10;
    float elasticV = 2, elasticP = 10, elasticS = 1;
    int elasticB = 6;
    float swingS = 1.5f;
    int bounceB = 4;

    public InterpDialog(){
        super("interp-dialog");

        cont.add(graph = new InterpGraph()).grow();
        cont.row();
        cont.table(b -> {
            b.defaults().size(140f, 60f);

            /* Button layout (Wow... that's a lot of different interps)
                Linear    Pow         Sine      Exp      Circle      Elastic      Swing      Bounce
                Reverse   PowIn       SineIn    ExpIn    CircleIn    ElasticIn    SwingIn    BounceIn
                Slope     PowOut      SineOut   ExpOut   CircleOut   ElasticOut   SwingOut   BounceOut
             */

            b.button("linear", () -> {
                graph.setInterp(linear);
                configType = 0;
            });
            b.button("pow", () -> {
                graph.setInterp(new Pow(powP));
                configType = 1;
            });
            b.button("sine", () -> {
                graph.setInterp(sine);
                configType = 0;
            });
            b.button("exp", () -> {
                graph.setInterp(new Exp(expV, expP));
                configType = 4;
            });
            b.button("circle", () -> graph.setInterp(circle));
            b.button("elastic", () -> {
                graph.setInterp(new Elastic(elasticV, elasticP, elasticB, elasticS));
                configType = 7;
            });
            b.button("swing", () -> {
                graph.setInterp(new Swing(swingS));
                configType = 10;
            });
            b.button("bounce", () -> {
                graph.setInterp(new Bounce(bounceB));
                configType = 13;
            });

            b.row();

            b.button("reverse", () -> {
                graph.setInterp(reverse);
                configType = 0;
            });
            b.button("powIn", () -> {
                graph.setInterp(new PowIn(powP));
                configType = 2;
            });
            b.button("sineIn", () -> {
                graph.setInterp(sineIn);
                configType = 0;
            });
            b.button("expIn", () -> {
                graph.setInterp(new ExpIn(expV, expP));
                configType = 5;
            });
            b.button("circleIn", () -> graph.setInterp(circleIn));
            b.button("elasticIn", () -> {
                graph.setInterp(new ElasticIn(elasticV, elasticP, elasticB, elasticS));
                configType = 8;
            });
            b.button("swingIn", () -> {
                graph.setInterp(new SwingIn(swingS));
                configType = 11;
            });
            b.button("bounceIn", () -> {
                graph.setInterp(new BounceIn(bounceB));
                configType = 14;
            });

            b.row();

            b.button("slope", () -> {
                graph.setInterp(slope);
                configType = 0;
            });
            b.button("powOut", () -> {
                graph.setInterp(new PowOut(powP));
                configType = 3;
            });
            b.button("sineOut", () -> {
                graph.setInterp(sineOut);
                configType = 0;
            });
            b.button("expOut", () -> {
                graph.setInterp(new ExpOut(expV, expP));
                configType = 6;
            });
            b.button("circleOut", () -> graph.setInterp(circleOut));
            b.button("elasticOut", () -> {
                graph.setInterp(new ElasticOut(elasticV, elasticP, elasticB, elasticS));
                configType = 9;
            });
            b.button("swingOut", () -> {
                graph.setInterp(new SwingOut(swingS));
                configType = 12;
            });
            b.button("bounceOut", () -> {
                graph.setInterp(new BounceOut(bounceB));
                configType = 15;
            });
        });

        addCloseButton();
    }
}
