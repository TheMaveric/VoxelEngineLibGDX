package com.kushagra.game.engine.domain;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Chunk {
    private final int chunkSize;

    private final float posX;
    private final float posY;
    private final float posZ;

    private final Array<ModelInstance> chunkModelInstances;
    private final OpenSimplexNoise openSimplexNoise;
    private final Long seed;
    private boolean hasChanged;

    public Chunk(int chunkSize, float posX, float posY, float posZ, OpenSimplexNoise openSimplexNoise, Long seed) {
        this.chunkSize = chunkSize;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.seed = seed;
        this.openSimplexNoise = openSimplexNoise;
        chunkModelInstances = new Array<>();
        generateChunk();
    }

    public Array<ModelInstance> getChunkModelInstances() {
        return chunkModelInstances;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void setChanged(boolean changed) {
        hasChanged = changed;
    }

    private void generateChunk() {
        int cubeCount = chunkSize * chunkSize * chunkSize;

        for (int i = 0; i < cubeCount; i++) {
            float cubePosX = posX + (i % chunkSize);
            float cubePosY = posY + ((i / chunkSize) % chunkSize);
            float cubePosZ = posZ + (i / (chunkSize * chunkSize));

            double noiseValue = openSimplexNoise.noise3_ImproveXY(seed, posX, posY, posZ);
            CubeType cubeType = CubeFactory.getCubeTypeFromNoiseValue(noiseValue);

            if (cubeType != CubeType.AIR) {
                Vector3 cubePosition = new Vector3(cubePosX, cubePosY, cubePosZ);
                Cube cube = new Cube(cubeType, cubePosition);
                chunkModelInstances.add(cube.getModelInstance());
            }
        }
    }
}
