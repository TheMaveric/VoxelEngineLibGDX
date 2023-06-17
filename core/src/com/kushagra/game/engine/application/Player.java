package com.kushagra.game.engine.application;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

public class Player {
    private Vector3 position;
    private float moveSpeed;

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    private boolean grounded;

    public Player(Vector3 position, float moveSpeed) {
        this.position = position;
        this.moveSpeed = moveSpeed;
        this.grounded = false;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void updatePosition(Vector3 direction, Vector3 up, float deltaTime) {
        Vector3 displacement = new Vector3();

        // Move forward (W key)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            displacement.add(direction);
        }
        // Move backward (S key)
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            displacement.sub(direction);
        }
        // Strafe left (A key)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            Vector3 left = new Vector3(direction).crs(up).nor();
            displacement.sub(left);
        }
        // Strafe right (D key)
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            Vector3 right = new Vector3(direction).crs(up).nor();
            displacement.add(right);
        }
        // Move up (Space key)
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            displacement.add(up);
        }
        // Move down (Shift key)
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            displacement.sub(up);
        }

        // Normalize the displacement vector to ensure consistent movement speed
        if (displacement.len() > 0) {
            displacement.nor();
        }
        // Apply the movement speed and delta time to the displacement vector
        displacement.scl(moveSpeed * deltaTime);
        position.add(displacement);

    }



    public void applyGravity(float gravity, float deltaTime) {
        Vector3 gravityForce = new Vector3(0f, +gravity, 0f).scl(deltaTime);
        position.add(gravityForce);

        // Check if the player is grounded (above the ground level)
        if (position.y <= 0) {
            position.y = 0; // Set player's position to ground level
            grounded = true;
        } else {
            grounded = false;
        }
    }

    public boolean isGrounded() {
        return grounded;
    }
}
