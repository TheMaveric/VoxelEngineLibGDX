package com.kushagra.game.engine.domain.cube;

public class CubeFactory {
    private static double threshold = 0.0;

    public static void setThreshold(double value) {
        threshold = value;
    }

    public static CubeType getCubeTypeFromNoiseValue(double noiseValue, int cubeX, int cubeY, int cubeZ) {
        double threshold = 0.0; // Adjust the threshold value as needed
        if (cubeY < 2) {
            if (noiseValue > threshold + 0.9) {
                return CubeType.STONE;
            } else if (noiseValue > threshold + 0.8) {
                return CubeType.DIRT;
            } else if (noiseValue > threshold + 0.7) {
                return CubeType.GRASS;
            } /*else if (noiseValue > threshold + 0.6) {
                return CubeType.SKYBLUE;//
            } else if (noiseValue > threshold + 0.5) {
                return CubeType.OCEANBLUE;//
            } else if (noiseValue > threshold + 0.4) {
                return CubeType.SUNSET;//
            } else if (noiseValue > threshold + 0.3) {
                return CubeType.SNOW;//
            } else if (noiseValue > threshold + 0.2) {
                return CubeType.FIRE;//
            }*/ else if (noiseValue > threshold + 0.1) {
                return CubeType.LEAF;
            } else if (noiseValue > threshold) {
                return CubeType.SAND;
            } else if (noiseValue > threshold - 0.1) {
                return CubeType.ASH;//
            } else if (noiseValue > threshold - 0.2) {
                return CubeType.SILVER;
            } else if (noiseValue > threshold - 0.3) {
                return CubeType.BRICK;
            } else if (noiseValue > threshold - 0.4) {
                return CubeType.WATER;
            } else if (noiseValue > threshold - 0.5) {
                return CubeType.MUD;
            } else if (noiseValue > threshold - 0.6) {
                return CubeType.GRANITE;
            } else {
                return CubeType.CHARCOAL;
            }
        } else if (cubeY > 220) {
            //if (noiseValue > threshold + 0.5) {
            return CubeType.CLOUD;
            //} else
            //    return CubeType.AIR;
        } else return CubeType.AIR;
    }

}
