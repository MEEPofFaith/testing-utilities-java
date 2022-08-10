package testing.dialogs;

import arc.math.*;
import mindustry.ui.dialogs.*;

public class InterpDialog extends BaseDialog{
    InterpGraph graph;

    public InterpDialog(){
        super("interp-dialog");

        cont.add(graph = new InterpGraph()).grow();
        cont.row();
        cont.table(b -> {
            b.defaults().size(120f, 60f);

            /* TODO Button layout (Wow... that's a lot of different interps)
                Linear    Pow         Sin      Exp      Circle      Elastic      Swing      Bounce
                Reverse   PowIn       SinIn    ExpIn    CircleIn    ElasticIn    SwingIn    BounceIn
                Slope     PowOut      SinOut   ExpOut   CircleOut   ElasticOut   SwingOut   BounceOut
                          PowInInv
                          PowOutInv
             */

            b.button("linear", () -> graph.setInterp(Interp.linear));
            b.button("test", () -> graph.setInterp(Interp.smoother));
            b.button("test2", () -> graph.setInterp(Interp.elastic));
            b.button("test3", () -> graph.setInterp(Interp.swing));
            b.button("test4", () -> graph.setInterp(Interp.bounce));
        });

        addCloseButton();
    }
}
