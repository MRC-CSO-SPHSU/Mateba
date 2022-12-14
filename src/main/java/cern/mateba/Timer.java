/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba;

/**
 * A handy stopwatch for benchmarking. Like a real stop watch used on ancient
 * running tracks you can start the watch, stop it, start it again, stop it
 * again, display the elapsed time and reset the watch.
 *
 * @author wolfgang.hoschek@cern.ch
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 */
public class Timer {
    private long baseTime;

    private long elapsedTime;

    /**
     * Constructs a new timer, initially not started. Use start() to start the
     * timer.
     */
    public Timer() {
        this.reset();
    }

    /**
     * Shows how to use a timer in convenient ways.
     */
    public static void test(int size) {
        // benchmark this piece
        Timer t = new Timer().start();
        int j = 0;
        for (int i = 0; i < size; i++) {
            j++;
        }
        t.stop();
        t.display();
        System.out.println("I finished the test using " + t);

        // do something we do not want to benchmark
        j = 0;
        for (int i = 0; i < size; i++) {
            j++;
        }

        // benchmark another piece and add to last benchmark
        t.start();
        j = 0;
        for (int i = 0; i < size; i++) {
            j++;
        }
        t.stop().display();

        // benchmark yet another piece independently
        t.reset(); // set timer to zero
        t.start();
        j = 0;
        for (int i = 0; i < size; i++) {
            j++;
        }
        t.stop().display();
    }

    /**
     * Prints the elapsed time on System.out
     *
     * @return <tt>this</tt> (for convenience only).
     */
    public Timer display() {
        System.out.println(this);
        return this;
    }

    /**
     * Same as <tt>seconds()</tt>.
     */
    public double elapsedTime() {
        return seconds();
    }

    /**
     * Returns the elapsed time in milli seconds; does not stop the timer, if
     * started.
     */
    public double millis() {
        long elapsed = elapsedTime;
        if (baseTime != 0) { // we are started
            elapsed += System.nanoTime() - baseTime;
        }
        return elapsed / 1000000.0;
    }

    /**
     * Returns the elapsed time in nano seconds; does not stop the timer, if
     * started.
     */
    public long nanos() {
        long elapsed = elapsedTime;
        if (baseTime != 0) { // we are started
            elapsed += System.nanoTime() - baseTime;
        }
        return elapsed;
    }

    /**
     * <tt>T = this - other</tt>; Constructs and returns a new timer which is
     * the difference of the receiver and the other timer. The new timer is not
     * started.
     *
     * @param other the timer to subtract.
     * @return a new timer.
     */
    public Timer minus(Timer other) {
        Timer copy = new Timer();
        copy.elapsedTime = nanos() - other.nanos();
        return copy;
    }

    /**
     * Returns the elapsed time in minutes; does not stop the timer, if started.
     */
    public double minutes() {
        return seconds() / 60.0;
    }

    /**
     * <tt>T = this + other</tt>; Constructs and returns a new timer which is
     * the sum of the receiver and the other timer. The new timer is not
     * started.
     *
     * @param other the timer to add.
     * @return a new timer.
     */
    public Timer plus(Timer other) {
        Timer copy = new Timer();
        copy.elapsedTime = nanos() + other.nanos();
        return copy;
    }

    /**
     * Resets the timer.
     *
     * @return <tt>this</tt> (for convenience only).
     */
    public Timer reset() {
        elapsedTime = 0;
        baseTime = 0;
        return this;
    }

    /**
     * Returns the elapsed time in seconds; does not stop the timer, if started.
     */
    public double seconds() {
        return (nanos()) / 1000000000.0;
    }

    /**
     * Starts the timer.
     *
     * @return <tt>this</tt> (for convenience only).
     */
    public Timer start() {
        baseTime = System.nanoTime();
        return this;
    }

    /**
     * Stops the timer. You can start it again later, if necessary.
     *
     * @return <tt>this</tt> (for convenience only).
     */
    public Timer stop() {
        if (baseTime != 0) {
            elapsedTime = elapsedTime + (System.nanoTime() - baseTime);
        }
        baseTime = 0;
        return this;
    }

    /**
     * Returns a String representation of the receiver.
     */

    public String toString() {
        return "Time=" + String.format("%.4f", this.elapsedTime()) + " secs";

    }
}
