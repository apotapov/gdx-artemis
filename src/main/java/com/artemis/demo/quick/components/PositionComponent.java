package com.artemis.demo.quick.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {

    public Vector2 position;

    public PositionComponent() {
        this(0, 0);
    }

    public PositionComponent(float x, float y) {
        position = new Vector2(x, y);
    }

    public void add(float x, float y) {
        position.add(x, y);
    }

    @Override
    public void reset() {
        position.set(0, 0);
    }

}
