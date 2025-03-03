package de.oliver.fancynpcs.skins.mineskin;

public class RatelimitException extends RuntimeException {

    private final long nextRequestTime;

    public RatelimitException(long nextRequestTime) {
        super("Rate limit reached. Next request possible at " + nextRequestTime);
        this.nextRequestTime = nextRequestTime;
    }

    public long getNextRequestTime() {
        return nextRequestTime;
    }
}
