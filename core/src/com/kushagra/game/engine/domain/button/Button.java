package com.kushagra.game.engine.domain.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button {
    private Sprite sprite;
    private boolean isClicked;

    public Button(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        isClicked = false;
    }

    public void render(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void update() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            if (sprite.getBoundingRectangle().contains(mouseX, mouseY)) {
                isClicked = true;
            }
        }
    }
}


