package com.kushagra.game.engine.domain.cube;


import com.badlogic.gdx.graphics.Color;

public enum CubeType {
    AIR("#000000"),
    GRASS("#00ff00"),
    DIRT("#964B00"),
    STONE("#242424"),
    SKYBLUE("#87CEEB"),
    OCEANBLUE("#000080"),
    SUNSET("#FF4500"),
    SNOW("#FFFFFF"),
    FIRE("#FF0000"),
    LEAF("#008000"),
    SAND("#F4A460"),
    ASH("#B2BEB5"),
    SILVER("#C0C0C0"),
    BRICK("#B22222"),
    WATER("#0000FF"),
    MUD("#8B4513"),
    GRANITE("#696969"),
    CLOUD("#F5F5F5"),
    CHARCOAL("#36454F");

    private final String colorName;

    CubeType(String colorName) {
        this.colorName = colorName;
    }

    public Color getColor() {
        return Color.valueOf(this.colorName);
    }
}




