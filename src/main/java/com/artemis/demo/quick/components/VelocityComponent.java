package com.artemis.demo.quick.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class VelocityComponent implements Component {

    public Vector2 velocity;

    public VelocityComponent() {
        this(0, 0);
    }

    public VelocityComponent(float x, float y) {
        velocity = new Vector2(x, y);
    }

    public void add(float x, float y) {
        velocity.add(x, y);
    }

    @Override
    public void reset() {
        velocity.set(0, 0);
    }

}
