/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017-2019  Lonami Exo @ lonami.dev

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package dev.lonami.klooni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import dev.lonami.klooni.Klooni;
import dev.lonami.klooni.Theme;
import dev.lonami.klooni.game.BaseScorer;
import dev.lonami.klooni.game.GameLayout;

// Horizontal band, used to show the score on the pause menu
public class Band extends Actor {
    private final static float[] multipliers = {0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 4.0f};
    private final static float bestMultiplier;

    static {
        // Use the height to determine the best match
        // We cannot use a size which is over the device height,
        // so use the closest smaller one
        int i;
        float desired = (float) Gdx.graphics.getHeight() / (float) Klooni.GAME_HEIGHT;
        for (i = multipliers.length - 1; i > 0; --i) {
            if (multipliers[i] < desired)
                break;
        }

        // Now that we have the right multiplier, load the skin
        Gdx.app.log("SkinLoader", "Using assets multiplier x" + multipliers[i]);
        bestMultiplier = multipliers[i];
    }
    //region Members

    private final BaseScorer scorer;
    private final Texture bandTexture;

    public final Rectangle scoreBounds;
    public final Rectangle infoBounds;

    private final Label infoLabel;
    private final Label scoreLabel;

    //endregion

    //region Constructor

    public Band(final Klooni game, final GameLayout layout, final BaseScorer scorer) {
        this.scorer = scorer;
        bandTexture = Theme.getBlankTexture();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");

        scoreLabel = new Label("", labelStyle);
        scoreLabel.setAlignment(Align.center);
        infoLabel = new Label("pause menu", labelStyle);
        infoLabel.setAlignment(Align.center);

        scoreBounds = new Rectangle();
        infoBounds = new Rectangle();
        layout.update(this);
    }

    //endregion

    //region Public methods

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // TODO This is not the best way to apply the transformation, but, oh well
        float x = getParent().getX();
        float y = getParent().getY();

        // TODO For some strange reason, the texture coordinates and label coordinates are different
        Vector2 pos = localToStageCoordinates(new Vector2(x, y));
        batch.setColor(Klooni.theme.bandColor);
        batch.draw(bandTexture, pos.x, pos.y, getWidth(), getHeight());

        scoreLabel.setBounds(x + scoreBounds.x, y + scoreBounds.y, scoreBounds.width, scoreBounds.height);
        scoreLabel.setText(Integer.toString(scorer.getCurrentScore()));
        scoreLabel.setColor(Klooni.theme.textColor);
        scoreLabel.draw(batch, parentAlpha);
        BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("font/x" + bestMultiplier + "/" + "geosans-light32.fnt"),
                Gdx.files.internal("font/x" + bestMultiplier + "/" + "geosans-light32.png"), false);
        bitmapFont.getData().scale((float) 0.25);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font=bitmapFont;
        infoLabel.setStyle(labelStyle);
        infoLabel.setBounds(x + infoBounds.x, y + infoBounds.y, infoBounds.width, infoBounds.height);
        infoLabel.setColor(Klooni.theme.textColor);
        infoLabel.draw(batch, parentAlpha);
    }

    // Once game over is set on the menu, it cannot be reverted
    public void setMessage(final String message) {
        if (!message.equals(""))
            infoLabel.setText(message);
    }

    //endregion
}
