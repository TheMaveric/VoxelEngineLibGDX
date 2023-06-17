/*
package com.kushagra.game.engine.application;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.List;

public class CollisionDetector {
    // Cube collision detection
    public boolean isColliding(Vector3 position, Vector3 direction, float boundingBoxHeight, List<ModelInstance> cubes) {
        // Calculate the player's AABB (axis-aligned bounding box) using the position and bounding box height
        float halfHeight = boundingBoxHeight / 2f;
        Vector3 min = new Vector3(position.x - halfHeight, position.y, position.z - halfHeight);
        Vector3 max = new Vector3(position.x + halfHeight, position.y + boundingBoxHeight, position.z + halfHeight);

        // Perform collision checks with cubes in your game world
        for (ModelInstance cube : cubes) {
            // Get the minimum and maximum coordinates of the cube's bounding box
            Vector3 cubeMin = new Vector3();
            Vector3 cubeMax = new Vector3();
            BoundingBox boundingBox = new BoundingBox();
            cube.calculateBoundingBox(boundingBox);
            boolean colliding = collisionDetector.isColliding(player.getPosition(), boundingBox);



            // Perform AABB collision detection
            if (max.x >= cubeMin.x && min.x <= cubeMax.x &&
                    max.y >= cubeMin.y && min.y <= cubeMax.y &&
                    max.z >= cubeMin.z && min.z <= cubeMax.z) {
                // Collision detected
                return true;
            }
        }

        // No collision detected
        return false;
    }
}*/
