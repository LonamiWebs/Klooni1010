package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;

public class SoftButton extends ImageButton {

    private int styleIndex;
    private Drawable image;

    public SoftButton(int styleIndex, String imageName) {
        super(Klooni.theme.getStyle(styleIndex));

        this.styleIndex = styleIndex;
        image = Theme.skin.getDrawable(imageName);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Always update the style to make sure we're using the right colors
        ImageButtonStyle style = getStyle();
        Klooni.theme.updateStyle(style, styleIndex);
        style.imageUp = image;

        super.draw(batch, parentAlpha);
    }
}
