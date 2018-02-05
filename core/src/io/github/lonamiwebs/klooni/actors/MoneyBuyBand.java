/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017  Lonami Exo | LonamiWebs

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
package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;

public class MoneyBuyBand extends Table {

    //region Members

    private final Label infoLabel;
    private final SoftButton confirmButton, cancelButton;

    private String infoText;
    private boolean showingTemp;

    // The theme card that is going to be bought next. We can't
    // only save the Theme because we need to tell the ThemeCard
    // that it was bought so it can reflect the new theme status.
    private ShopCard toBuy;

    // Used to interpolate between strings
    private final StringBuilder shownText;

    // When the next text update will take place
    private long nextTextUpdate;

    // When the temporary text should be reverted next
    private long nextTempRevertUpdate;

    // Milliseconds
    private final static long SHOW_ONE_CHARACTER_EVERY = 30;
    private final static long TEMP_TEXT_DELAY = 2 * 1000;

    //endregion

    //region Constructor

    public MoneyBuyBand(final Klooni game) {
        infoText = "";
        shownText = new StringBuilder();

        final Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font_small");

        infoLabel = new Label(infoText, labelStyle);
        infoLabel.setAlignment(Align.left);
        add(infoLabel).expandX().left().padLeft(20);

        confirmButton = new SoftButton(0, "ok_texture");
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (toBuy != null)
                    toBuy.performBuy();
                showCurrentMoney();
                hideBuyButtons();
            }
        });
        add(confirmButton).pad(8, 0, 8, 4);
        confirmButton.setVisible(false);

        cancelButton = new SoftButton(3, "cancel_texture");
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCurrentMoney();
                hideBuyButtons();
            }
        });
        add(cancelButton).pad(8, 4, 8, 8);
        cancelButton.setVisible(false);

        setBackground(new TextureRegionDrawable(new TextureRegion(Theme.getBlankTexture())));
        showCurrentMoney();
    }

    //endregion

    //region Private methods

    private void showCurrentMoney() {
        setText("money: " + Klooni.getMoney());
    }

    private void hideBuyButtons() {
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        toBuy = null;
    }

    // Set the text to which the shown text will interpolate.
    // This will remove any temporary shown text or otherwise
    // it would mess up this new text.
    private void setText(String text) {
        infoText = text;
        showingTemp = false;
        nextTextUpdate = TimeUtils.millis() + SHOW_ONE_CHARACTER_EVERY;
    }

    // Temporary text will always reset to the shown money
    // because it would make no sense to go back to the buy "confirm?"
    //
    // Can also be used to show a temporary notification text.
    public void setTempText(String text) {
        setText(text);
        showingTemp = true;
        nextTempRevertUpdate = TimeUtils.millis() + TEMP_TEXT_DELAY;
    }

    // Funky method to interpolate between the information
    // text and the currently being shown text
    private void interpolateText() {
        // If the currently shown text does not match the information text,
        // then that means that we need to interpolate between them.
        if (!shownText.toString().equals(infoText)) {
            // We need the pick the minimum text length limit
            // or charAt() will throw an IndexOutOfBoundsException
            int limit = Math.min(shownText.length(), infoText.length());
            for (int i = 0; i < limit; ++i) {
                // As soon as we found a character which differs, we can interpolate
                // to the new string by updating that single character
                if (shownText.charAt(i) != infoText.charAt(i)) {
                    shownText.setCharAt(i, infoText.charAt(i));
                    infoLabel.setText(shownText);
                    return;
                }
            }

            // All the preceding characters matched, so now
            // what's left is to check for the string length
            if (shownText.length() > infoText.length()) {
                // The old text was longer than the new one, so shorten it
                shownText.setLength(shownText.length() - 1);
            } else {
                // It can't be equal length or we wouldn't be here,
                // so avoid checking shown.length() < info.length().
                // We need to append the next character that we want to show
                shownText.append(infoText.charAt(shownText.length()));
            }
            infoLabel.setText(shownText);
        }
    }

    //endregion

    //region Public methods

    // Asks the user to buy the given theme or effect,
    // or shows that they don't have enough money to buy it
    public void askBuy(final ShopCard toBuy) {
        if (toBuy.getPrice() > Klooni.getMoney()) {
            setTempText("cannot buy!");
            confirmButton.setVisible(false);
            cancelButton.setVisible(false);
        } else {
            this.toBuy = toBuy;
            setText("confirm?");
            confirmButton.setVisible(true);
            cancelButton.setVisible(true);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        long now = TimeUtils.millis();
        if (now > nextTextUpdate) {
            interpolateText();
            nextTextUpdate = TimeUtils.millis() + SHOW_ONE_CHARACTER_EVERY;
            if (now > nextTempRevertUpdate && showingTemp) {
                // We won't be showing temp anymore if the current money is shown
                showCurrentMoney();
            }
        }
        setColor(Klooni.theme.bandColor);
        infoLabel.setColor(Klooni.theme.textColor);
        super.draw(batch, parentAlpha);
    }

    //endregion
}
