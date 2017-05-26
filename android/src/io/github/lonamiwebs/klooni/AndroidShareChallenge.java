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
package io.github.lonamiwebs.klooni;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;

class AndroidShareChallenge extends ShareChallenge {

    private final Handler handler;
    private final Context context;

    AndroidShareChallenge(final Context context) {
        handler = new Handler();
        this.context = context;
    }

    @Override
    File getShareImageFilePath() {
        return new File(context.getExternalCacheDir(), "share_challenge.png");
    }

    @Override
    public void shareScreenshot(final boolean ok) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!ok) {
                    Toast.makeText(context, "Failed to create the file", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String text = "Check out my score at 1010 Klooni!";
                final Uri pictureUri = Uri.fromFile(getShareImageFilePath());
                final Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/png");

                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);

                context.startActivity(Intent.createChooser(shareIntent, "Challenge your friends..."));
            }
        });
    }
}
