package com.artemis.demo.quick.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.demo.quick.components.PositionComponent;
import com.artemis.demo.quick.components.VelocityComponent;
import com.artemis.systems.EntityProcessingSystem;

public class MovementSystem extends EntityProcessingSystem {

    ComponentMapper<PositionComponent> pm;
    ComponentMapper<VelocityComponent> vm;

    @SuppressWarnings("unchecked")
    public MovementSystem() {
        super(Aspect.getAspectForAll(
                PositionComponent.class, VelocityComponent.class));
    }

    @Override
    public void initialize() {
        pm = world.getMapper(PositionComponent.class);
        vm = world.getMapper(VelocityComponent.class);
    }

    @Override
    protected void process(Entity e) {
        // Get the components from the entity using component mappers.
        PositionComponent positionComponent = pm.get(e);
        VelocityComponent velocityComponent = vm.get(e);

        // Update the position.
        positionComponent.position.x += velocityComponent.velocity.x * world.getDelta();
        positionComponent.position.y += velocityComponent.velocity.y * world.getDelta();
    }
}
