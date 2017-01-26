package io.github.lonamiwebs.klooni.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import io.github.lonamiwebs.klooni.Klooni;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig () {
        return new GwtApplicationConfiguration(408, 680);
    }

    @Override
    public ApplicationListener createApplicationListener () {
        return new Klooni();
    }
}
