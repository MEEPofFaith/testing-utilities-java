package testing.ui;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.style.*;
import mindustry.graphics.*;

/** Displays a drawable and a second, smaller one, in the corner. Can be swapped with {@link ToggleStack#swap()}. Do not add more children to this group. */
public class ToggleStack extends Element{
    private Color primaryColor = Color.white, secondaryColor = Pal.remove;
    private Drawable primaryDrawable, secondaryDrawable;
    private float stackSize = 0.5f;
    private boolean swap = false;
    private boolean behind = true;

    public ToggleStack(Drawable primaryDrawable, Drawable secondaryDrawable){
        this.primaryDrawable = primaryDrawable;
        this.secondaryDrawable = secondaryDrawable;
    }

    public void swap(){
        swap = !swap;
    }

    public float stackSize(){
        return stackSize;
    }

    public void setStackSize(float stackSize){
        this.stackSize = stackSize;
    }

    public Color baseColor(){
        return primaryColor;
    }

    public void setPrimaryColor(Color primaryColor){
        this.primaryColor = primaryColor;
    }

    public Color stackColor(){
        return secondaryColor;
    }

    public void setSecondaryColor(Color secondaryColor){
        this.secondaryColor = secondaryColor;
    }

    public void setColors(Color baseColor, Color stackColor){
        this.primaryColor = baseColor;
        this.secondaryColor = stackColor;
    }

    @Override
    public void draw(){
        validate();

        float x = this.x;
        float y = this.y;
        float width = getWidth();
        float height = getHeight();
        Drawable base = swap ? secondaryDrawable : primaryDrawable;
        Drawable stack = swap ? primaryDrawable : secondaryDrawable;

        if(behind){
            Draw.color(secondaryColor);
            stack.draw(x + width * (1f - stackSize), y, width * stackSize, height * stackSize);
        }
        Draw.color(primaryColor);
        base.draw(x, y, width, height);
        if(!behind){
            Draw.color(secondaryColor);
            stack.draw(x + width * (1f - stackSize), y, width * stackSize, height * stackSize);
        }
    }
}
