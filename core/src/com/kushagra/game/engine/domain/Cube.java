package com.kushagra.game.engine.domain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Cube {
    public static final float CUBE_SIZE = 1f;

    private final ModelInstance modelInstance;

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public Cube(CubeType cubeType, Vector3 position) {
        this.modelInstance = createCubeModelInstance(cubeType, position);
    }

    public ModelInstance createCubeModelInstance(CubeType cubeType, Vector3 position) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = null;
        try {
            material = new Material(ColorAttribute.createDiffuse(cubeType.getColor())); // Assuming cubeType.getColor() returns the desired color
        } catch (Exception e) {
            e.printStackTrace();
        }
        MeshPartBuilder meshBuilder = modelBuilder.part("cube", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked, material);

        float halfSize = CUBE_SIZE / 2f;

        // Front face
        meshBuilder.rect(
                -halfSize, -halfSize, halfSize,
                halfSize, -halfSize, halfSize,
                halfSize, halfSize, halfSize,
                -halfSize, halfSize, halfSize,
                0f, 0f, 1f);

        // Back face
        meshBuilder.rect(
                halfSize, -halfSize, -halfSize,
                -halfSize, -halfSize, -halfSize,
                -halfSize, halfSize, -halfSize,
                halfSize, halfSize, -halfSize,
                0f, 0f, -1f);

        // Top face
        meshBuilder.rect(
                -halfSize, halfSize, halfSize,
                halfSize, halfSize, halfSize,
                halfSize, halfSize, -halfSize,
                -halfSize, halfSize, -halfSize,
                0f, 1f, 0f);

        // Bottom face
        meshBuilder.rect(
                -halfSize, -halfSize, -halfSize,
                halfSize, -halfSize, -halfSize,
                halfSize, -halfSize, halfSize,
                -halfSize, -halfSize, halfSize,
                0f, -1f, 0f);

        // Left face
        meshBuilder.rect(
                -halfSize, -halfSize, -halfSize,
                -halfSize, -halfSize, halfSize,
                -halfSize, halfSize, halfSize,
                -halfSize, halfSize, -halfSize,
                -1f, 0f, 0f);

        // Right face
        meshBuilder.rect(
                halfSize, -halfSize, halfSize,
                halfSize, -halfSize, -halfSize,
                halfSize, halfSize, -halfSize,
                halfSize, halfSize, halfSize,
                1f, 0f, 0f);

        Model cubeModel = modelBuilder.end();
        ModelInstance modelInstance = new ModelInstance(cubeModel);
        modelInstance.transform.setToTranslation(position);
        return modelInstance;
    }
}
