package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;

// Small wrapper to use themed image buttons more easily
public class SoftButton extends ImageButton {

    //region Members

    private int styleIndex;
    public Drawable image;

    //endregion

    //region Constructor

    public SoftButton(final int styleIndex, final String imageName) {
        super(Klooni.theme.getStyle(styleIndex));

        this.styleIndex = styleIndex;
        updateImage(imageName);
    }

    //endregion

    //region Public methods

    public void updateImage(final String imageName) {
        image = Theme.skin.getDrawable(imageName);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Always update the style to make sure we're using the right image.
        // This might not always be the case since two buttons can be using
        // the "same" style (except for the image up, i.e. after coming from
        // the customize menu), so make sure to update it always.
        ImageButtonStyle style = getStyle();
        Klooni.theme.updateStyle(style, styleIndex);
        style.imageUp = image;

        getImage().setColor(Klooni.theme.foreground);
        super.draw(batch, parentAlpha);
    }

    //endregion
}
