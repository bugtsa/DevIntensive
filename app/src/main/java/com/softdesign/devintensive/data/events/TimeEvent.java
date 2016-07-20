package com.softdesign.devintensive.data.events;

public class TimeEvent {
    public final int mTimeCode;

    public TimeEvent(int timeCode) {
        this.mTimeCode = timeCode;
    }

    public int getTimeCode() {
        return mTimeCode;
    }
}
