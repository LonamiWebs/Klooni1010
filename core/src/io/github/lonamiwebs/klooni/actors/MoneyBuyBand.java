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

    // Used to interpolate between strings
    private StringBuilder shownText;

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
        add(confirmButton).pad(8, 0, 8, 4);
        confirmButton.setVisible(false);

        cancelButton = new SoftButton(3, "cancel_texture");
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCurrentMoney();
                confirmButton.setVisible(false);
                cancelButton.setVisible(false);
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
        setText("money: 0");
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
    private void setTempText(String text) {
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
            }
            else {
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

    // Asks the user to buy the given theme, or shows
    // that they don't have enough money to buy it
    public void askBuy(final Theme toBuy) {
        int moneyIHaz = 10; // TODO use a real value
        if (toBuy.getPrice() > moneyIHaz) {
            setTempText("cannot buy!");
            confirmButton.setVisible(false);
            cancelButton.setVisible(false);
        }
        else {
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
        super.draw(batch, parentAlpha);
    }

    //endregion
}
