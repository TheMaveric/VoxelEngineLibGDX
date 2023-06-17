package com.kushagra.game.engine.domain;


import com.badlogic.gdx.graphics.Color;

public enum CubeType {
    AIR("#000000"),
    GRASS("#00ff00"),
    DIRT("#964B00"),
    STONE("#242424");

    private final String colorName;

    CubeType(String colorName) {
        this.colorName = colorName;
    }

    public Color getColor() {
        return Color.valueOf(this.colorName);
    }
}




