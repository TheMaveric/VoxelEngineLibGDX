package com.kushagra.game.engine.domain.cube;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Cube {
    private static final float CUBE_SIZE = 1f;
    private static final String PART_NAME = "cube";

    private final ModelInstance modelInstance;

    public Cube(CubeType cubeType, Vector3 position, ModelBuilder modelBuilder) {
        Model cubeModel = createCubeModel(cubeType, modelBuilder);
        modelInstance = new ModelInstance(cubeModel, position);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    private Model createCubeModel(CubeType cubeType, ModelBuilder modelBuilder) {
        Material material = new Material(ColorAttribute.createDiffuse(cubeType.getColor()));

        modelBuilder.begin();
        MeshBuilder meshBuilder = new MeshBuilder();

        float halfSize = CUBE_SIZE / 2f;
        short[] indices = {
                // Front face
                0, 1, 2, 2, 3, 0,
                // Back face
                4, 5, 6, 6, 7, 4,
                // Top face
                8, 11, 10, 10, 9, 8,
                // Bottom face
                12, 13, 14, 14, 15, 12,
                // Left face
                16, 17, 18, 18, 19, 16,
                // Right face
                20, 21, 22, 22, 23, 20
        };

        float[] vertices = {
                // Front face
                -halfSize, -halfSize, halfSize, 1f, 0f, 0f, 1f,
                halfSize, -halfSize, halfSize, 1f, 0f, 0f, 1f,
                halfSize, halfSize, halfSize, 1f, 0f, 0f, 1f,
                -halfSize, halfSize, halfSize, 1f, 0f, 0f, 1f,
                // Back face
                halfSize, -halfSize, -halfSize, 1f, 1f, 0f, 1f,
                -halfSize, -halfSize, -halfSize, 1f, 1f, 0f, 1f,
                -halfSize, halfSize, -halfSize, 1f, 1f, 0f, 1f,
                halfSize, halfSize, -halfSize, 1f, 1f, 0f, 1f,
                // Top face
                -halfSize, halfSize, -halfSize, 1f, 0f, 1f, 1f,
                halfSize, halfSize, -halfSize, 1f, 0f, 1f, 1f,
                halfSize, halfSize, halfSize, 1f, 0f, 1f, 1f,
                -halfSize, halfSize, halfSize, 1f, 0f, 1f, 1f,
                // Bottom face
                -halfSize, -halfSize, -halfSize, 1f, 1f, 1f, 1f,
                halfSize, -halfSize, -halfSize, 1f, 1f, 1f, 1f,
                halfSize, -halfSize, halfSize, 1f, 1f, 1f, 1f,
                -halfSize, -halfSize, halfSize, 1f, 1f, 1f, 1f,
                // Left face
                -halfSize, -halfSize, -halfSize, 1f, 0f, 0f, 1f,
                -halfSize, -halfSize, halfSize, 1f, 0f, 0f, 1f,
                -halfSize, halfSize, halfSize, 1f, 0f, 0f, 1f,
                -halfSize, halfSize, -halfSize, 1f, 0f, 0f, 1f,
                // Right face
                halfSize, -halfSize, halfSize, 1f, 1f, 0f, 1f,
                halfSize, -halfSize, -halfSize, 1f, 1f, 0f, 1f,
                halfSize, halfSize, -halfSize, 1f, 1f, 0f, 1f,
                halfSize, halfSize, halfSize, 1f, 1f, 0f, 1f
        };

        meshBuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked);
        meshBuilder.addMesh(vertices, indices);
        Mesh mesh = meshBuilder.end();

        modelBuilder.part(PART_NAME, mesh, GL20.GL_TRIANGLES, material);

        return modelBuilder.end();
    }
}
