package com.artemis.demo.quick;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.demo.quick.components.PositionComponent;
import com.artemis.demo.quick.components.VelocityComponent;
import com.artemis.demo.quick.systems.MovementSystem;
import com.artemis.demo.quick.systems.RenderingSystem;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class QuickGameDemo extends Game {

    World world;
    OrthographicCamera camera;

    /**
     * Initializes all of the in-game objects
     */
    @Override
    public void create() {

        // create a camera
        camera = new OrthographicCamera();

        createWorld();

        //create a few of entities
        createEntity(200, 200, 20, 20);
        createEntity(200, 200, -20, 20);
        createEntity(200, 200, 20, -20);
        createEntity(200, 200, -20, -20);
    }

    /**
     * Creates the world and adds entity systems to it.
     */
    protected void createWorld() {
        world = new World();

        world.setSystem(new MovementSystem());
        world.setSystem(new RenderingSystem(camera));

        world.initialize();
    }

    /**
     * Creates an entity with a given position (x, y) and velocity (vx, vy)
     */
    protected void createEntity(float x, float y, float vx, float vy) {
        Entity e = world.createEntity();
        e.addComponent(new PositionComponent(x, y));
        e.addComponent(new VelocityComponent(vx, vy));
        e.addToWorld();
    }

    /**
     * Game loop.
     */
    @Override
    public void render() {
        world.setDelta(Gdx.graphics.getDeltaTime());
        world.process();
    }

    /**
     * Update the camera if the game screen is resized.
     */
    @Override
    public void resize(int width, int height) {
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        this.camera.position.set(centerX, centerY, 0);
        this.camera.viewportWidth = width;
        this.camera.viewportHeight = height;
    }
}
