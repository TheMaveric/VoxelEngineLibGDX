package com.kushagra.game.engine.domain;


public class CubeFactory {
    public static CubeType getCubeTypeFromNoiseValue(double noiseValue) {
        if (noiseValue > 0.5) {
            return CubeType.STONE;
        } else if (noiseValue > 0.3) {
            return CubeType.DIRT;
        } else if (noiseValue > 0.2) {
            return CubeType.GRASS;
        } else {
            return CubeType.AIR;
        }
    }
}

