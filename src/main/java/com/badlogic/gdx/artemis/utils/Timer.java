package com.badlogic.gdx.artemis.utils;

public abstract class Timer {

    protected float delay;
    protected boolean repeat;
    protected float acc;
    protected boolean done;
    protected boolean stopped;

    public Timer(float delay) {
        this(delay, false);
    }

    public Timer(float delay, boolean repeat) {
        this.delay = delay;
        this.repeat = repeat;
        this.acc = 0;
    }

    public void update(float delta) {
        if (!done && !stopped) {
            acc += delta;

            if (acc >= delay) {
                acc -= delay;

                if (repeat) {
                    reset();
                } else {
                    done = true;
                }

                execute();
            }
        }
    }

    public void reset() {
        stopped = false;
        done = false;
        acc = 0;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isRunning() {
        return !done && acc < delay && !stopped;
    }

    public void stop() {
        stopped = true;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public abstract void execute();

    public float getPercentageRemaining() {
        if (done) {
            return 100;
        } else if (stopped) {
            return 0;
        } else {
            return 1 - (delay - acc) / delay;
        }
    }

    public float getDelay() {
        return delay;
    }

}
