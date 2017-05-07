package io.github.lonamiwebs.klooni;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import io.github.lonamiwebs.klooni.Klooni;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final AndroidShareChallenge shareChallenge = new AndroidShareChallenge(this);
        initialize(new Klooni(shareChallenge), config);
    }
}
