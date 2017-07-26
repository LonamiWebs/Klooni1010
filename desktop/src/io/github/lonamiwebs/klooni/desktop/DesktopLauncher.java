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
package io.github.lonamiwebs.klooni.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.lonamiwebs.klooni.Klooni;

class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Klooni 1010!";
        config.width = Klooni.GAME_WIDTH;
        config.height = Klooni.GAME_HEIGHT;
        config.addIcon("ic_launcher/icon128.png", Files.FileType.Internal);
        config.addIcon("ic_launcher/icon32.png", Files.FileType.Internal);
        config.addIcon("ic_launcher/icon16.png", Files.FileType.Internal);
        new LwjglApplication(new Klooni(null), config);
    }
}
