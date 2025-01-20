package util;

public class Time {
    // initialized as soon as the application starts up
    public static float timedStarted = System.nanoTime();
    public static float getTime() {
        // convert nanaSecond to second
        return (System.nanoTime() - timedStarted) * 1E-9F;
    }
}
