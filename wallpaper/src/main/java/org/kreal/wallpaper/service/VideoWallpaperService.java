package org.kreal.wallpaper.service;

import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by lthee on 2017/5/5.
 */

public class VideoWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }
    class VideoEngine extends Engine{
        private MediaPlayer mediaPlayer = new MediaPlayer();

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mediaPlayer.setDisplay(holder);
            try {
                mediaPlayer.setDataSource("/sdcard/CC/123,mp4");
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        mp.setLooping(true);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
//            if(visible)
//                mediaPlayer.start();
//            else mediaPlayer.stop();
        }
    }
}
