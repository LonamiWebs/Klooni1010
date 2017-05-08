package io.github.lonamiwebs.klooni.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import io.github.lonamiwebs.klooni.Klooni;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig () {
        return new GwtApplicationConfiguration(Klooni.GAME_WIDTH, Klooni.GAME_HEIGHT);
    }

    @Override
    public ApplicationListener createApplicationListener () {
        return new Klooni(null);
    }
}
