package com.artemis.utils;

import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Helper class allowing to execute a certain action after
 * an elapsed delay.
 * 
 */
public abstract class Timer implements Poolable {

    protected float delay;
    protected boolean repeat;
    protected float acc;
    protected boolean done;
    protected boolean stopped;

    /**
     * Creates a timer that will execute after a certain delay.
     * Non-repeating.
     * 
     * @param delay delay for the action.
     */
    public Timer(float delay) {
        this(delay, false);
    }

    /**
     * Creates a potentially repeatable timer that will execute
     * after the specified delay.
     * 
     * @param delay Timer delay
     * @param repeat Whether the timer should repeat or not.
     */
    public Timer(float delay, boolean repeat) {
        this.delay = delay;
        this.repeat = repeat;
        this.acc = 0;
    }

    /**
     * Update the timer with the specified delta of time elapsed.
     * 
     * Executes the timer if the delay expires.
     * 
     * @param delta Elapsed time.
     */
    public void update(float delta) {
        if (!done && !stopped) {
            acc += delta;

            if (acc >= delay) {
                acc -= delay;

                if (!repeat) {
                    done = true;
                }

                execute();
            }
        }
    }

    /**
     * Resets the timer to the beginning.
     */
    @Override
    public void reset() {
        stopped = false;
        done = false;
        acc = 0;
    }

    /**
     * @return Returns whether the timer is done.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @return Returns whether the timer is currently running.
     */
    public boolean isRunning() {
        return !done && acc < delay && !stopped;
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        stopped = true;
    }

    /**
     * Set's the timer delay.
     * 
     * @param delay New delay.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Executes the timer.
     */
    public abstract void execute();

    /**
     * @return Returns the percentage of delay remaining.
     */
    public float getPercentageRemaining() {
        if (done) {
            return 100;
        } else if (stopped) {
            return 0;
        } else {
            return 1 - (delay - acc) / delay;
        }
    }

    /**
     * @return Returns the current delay.
     */
    public float getDelay() {
        return delay;
    }

}
