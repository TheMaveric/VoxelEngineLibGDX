package com.kushagra.game.engine.domain.chunk;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.kushagra.game.engine.domain.noise.OpenSimplex2F;
import com.kushagra.game.engine.domain.cube.Cube;
import com.kushagra.game.engine.domain.cube.CubeFactory;
import com.kushagra.game.engine.domain.cube.CubeType;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    private final int NOISE_SCALE = 10000;
    private final int chunkWidth=CHUNK_SIZE;
    private final int chunkHeight = 255;
    private final int chunkDepth=CHUNK_SIZE;
    private final float posX;
    private final float posY;
    private final float posZ;
    private final Long seed;
    private ModelBuilder modelBuilder;
    private Array<ModelInstance> chunkModelInstances;
    private Cube[][][] cubeCache;

    public Chunk(int chunkWidth, int chunkDepth, float posX, float posY, float posZ, Long seed, ModelBuilder modelBuilder) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.seed = seed;
        this.modelBuilder = modelBuilder;
        chunkModelInstances = new Array<>();
        cubeCache = new Cube[chunkWidth][chunkHeight][chunkDepth];
        generateChunk();
    }

    private void generateChunk() {
        int cubeCount = chunkWidth * chunkDepth;

        for (int i = 0; i < cubeCount; i++) {
            int cubeX = i % chunkWidth;
            int cubeY = 0;
            int cubeZ = i / chunkWidth;

            float cubePosX = posX + cubeX;
            float cubePosY = posY + cubeY;
            float cubePosZ = posZ + cubeZ;

            if (cubeCache[cubeX][cubeY][cubeZ] != null) {
                // Use the cached cube if available
                chunkModelInstances.add(cubeCache[cubeX][cubeY][cubeZ].getModelInstance());
                System.out.println("CACHE HIT");
            } else {
                OpenSimplex2F openSimplex2F = new OpenSimplex2F(seed);
                double noiseValue = openSimplex2F.noise2_XBeforeY(cubePosX / NOISE_SCALE, cubePosZ / NOISE_SCALE);
                CubeType cubeType = CubeFactory.getCubeTypeFromNoiseValue(noiseValue, cubeX, cubeY, cubeZ);
                if (!CubeType.CHARCOAL.equals(cubeType) && !CubeType.AIR.equals(cubeType) && isCubeVisible(cubeX,cubeY,cubeZ)) {
                    Vector3 cubePosition = new Vector3(cubePosX, (float) (cubePosY + (noiseValue * 255)), cubePosZ);
                    //Vector3 cubePosition = new Vector3(cubePosX, (float) (cubePosY + (noiseValue * 255)), cubePosZ);
                    Cube cube = new Cube(cubeType, cubePosition, modelBuilder);
                    chunkModelInstances.add(cube.getModelInstance());

                    // Cache the generated cube for future use
                    cubeCache[cubeX][cubeY][cubeZ] = cube;
                }
            }
        }
    }


    public Array<ModelInstance> getChunkModelInstances() {
        return chunkModelInstances;
    }

    public Vector3 getPosition() {
        return new Vector3(posX, posY, posZ);
    }

    private boolean isCubeVisible(int x, int y, int z) {
        // Check if the cube is completely surrounded by other cubes

        // Check the existence of the top and bottom neighboring cubes
        if (!cubeExists(x, y + 1, z) || !cubeExists(x, y - 1, z)) {
            return true; // If either the top or bottom cube is missing, the current cube is visible
        }

        // Check the existence of the left and right neighboring cubes
        if (!cubeExists(x - 1, y, z) || !cubeExists(x + 1, y, z)) {
            return true; // If either the left or right cube is missing, the current cube is visible
        }

        // Check the existence of the front and back neighboring cubes
        if (!cubeExists(x, y, z - 1) || !cubeExists(x, y, z + 1)) {
            return true; // If either the front or back cube is missing, the current cube is visible
        }

        return false; // If all neighboring cubes exist, the current cube is completely surrounded and not visible
    }

    private boolean cubeExists(int x, int y, int z) {
        // Check if the neighboring cube exists within the chunk boundaries
        return x >= 0 && x < chunkWidth &&
                y >= 0 && y < chunkHeight &&
                z >= 0 && z < chunkDepth;
    }
}
