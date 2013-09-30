package com.badlogic.gdx.artemis;

public interface EntityObserver {
	
	void added(Entity e);
	
	void changed(Entity e);
	
	void deleted(Entity e);
	
	void enabled(Entity e);
	
	void disabled(Entity e);

}
