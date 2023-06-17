package com.kushagra.game.engine.application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class FPSCounter {

    private long startTime;
    private int frameCount;

    public void log() {
        frameCount++;

        if (TimeUtils.nanoTime() - startTime > 1_000_000_000L) {
            Gdx.app.log("FPSCounter", "fps: " + frameCount);
            frameCount = 0;
            startTime = TimeUtils.nanoTime();
        }
    }
}

