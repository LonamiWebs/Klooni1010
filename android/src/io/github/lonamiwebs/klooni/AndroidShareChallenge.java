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
