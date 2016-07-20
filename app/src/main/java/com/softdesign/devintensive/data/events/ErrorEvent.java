package com.softdesign.devintensive.data.events;

public class ErrorEvent {
    public final int mErrorCode;

    public ErrorEvent(int errorCode) {
        this.mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
