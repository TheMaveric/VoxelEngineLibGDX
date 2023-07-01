package com.kushagra.game.engine.application.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class Player {
    private Vector3 position;
    private Vector3 prevPosition;
    private float moveSpeed = 1f;

    private ModelInstance modelInstance;
    private ModelInstance viewModelInstance;
    private PerspectiveCamera camera;

    public Player(Vector3 position, Model model, PerspectiveCamera camera) {
        this.position = position;
        this.prevPosition = position;
        this.modelInstance = new ModelInstance(model);
        this.camera = camera;
    }

    public void update(float deltaTime, Array<ModelInstance> instances) {
        handleInput(deltaTime);
        //applyGravity(deltaTime);
        handleCollision(instances, 1f);
    }

    private void handleInput(float deltaTime) {
        // Get the camera's forward and right directions
        Vector3 cameraForward = camera.direction.cpy().nor();
        Vector3 cameraRight = cameraForward.cpy().crs(camera.up).nor();

        Vector3 movement = new Vector3();

        // Handle input for movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.add(cameraForward);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.sub(cameraForward);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.sub(cameraRight);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.add(cameraRight);
        }

        // Normalize movement vector and scale by moveSpeed and deltaTime
        if (movement.len2() > 0) {
            movement.nor().scl(moveSpeed * deltaTime);
        }

        // Apply movement to the player's position
        position.add(movement);

        // Handle jumping
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            jump();
        }
        float speed = Math.min(moveSpeed, 4);
        // Handle running
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            moveSpeed = 2.0f * speed; // Increase move speed when walking
        } else {
            moveSpeed = speed; // Reset move speed when not walking
        }
    }

    private void jump() {
        // Add your jump implementation here
        // For example, you can apply an upward velocity to the player's position
        float jumpHeight = 10.0f; // Adjust jump height as needed
        position.y += jumpHeight;
    }

    private void applyGravity(float deltaTime) {
        float gravity = -9.8f; // Adjust gravity as needed
        position.y += gravity * deltaTime;
    }

    private void handleCollision(Array<ModelInstance> instances, float collidingDistance) {
        // Update the player's position in the model instance
        modelInstance.transform.setTranslation(position);

        // Get the player's bounding box
        BoundingBox playerBounds = new BoundingBox();
        Vector3 playerDimensions = new Vector3();
        Vector3 objectDimensions = new Vector3();
        playerBounds.getDimensions(playerDimensions);
        modelInstance.calculateBoundingBox(playerBounds);
        playerBounds.min.add(position);
        playerBounds.max.add(position);

        for (ModelInstance instance : instances) {
            // Skip if the instance is the player's own model
            if (instance == modelInstance) {
                continue;
            }

            // Get the bounding box of the current game object
            BoundingBox objectBounds = new BoundingBox();
            instance.calculateBoundingBox(objectBounds);

            // Check the distance between the player and the instance
            Vector3 playerCenter =new Vector3();
            playerBounds.getCenter(playerCenter);
            Vector3 objectCenter =new Vector3();
            objectBounds.getCenter(objectCenter);
            float distance = playerCenter.dst(objectCenter);
            if (distance > collidingDistance) {
                continue; // Skip to the next object
            }

            // Perform collision detection using bounding boxes
            if (playerBounds.intersects(objectBounds)) {
                //System.out.println("COLLIDING "+ playerBounds +" "+objectBounds);
                // Handle the collision based on your requirements
                // Example: Push the player away from the object along the collision normal
                Vector3 collisionNormal = playerCenter.sub(objectCenter).nor();

                objectBounds.getDimensions(objectDimensions);
                float pushDistance = playerDimensions.len() / 2 + objectDimensions.len() / 2 - distance;
                Vector3 pushVector = collisionNormal.scl(pushDistance);
                position.add(pushVector);
                modelInstance.transform.setTranslation(position);
                playerBounds.set(playerBounds.min.set(position), playerBounds.max.set(position).add(modelInstance.transform.getScale(new Vector3())));
            }
        }
    }


    public Vector3 getPosition() {
        return position;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public ModelInstance getViewModelInstance() {
        return viewModelInstance;
    }


    public void setViewModelInstance(ModelInstance viewModelInstance) {
        this.viewModelInstance = viewModelInstance;
    }

}
