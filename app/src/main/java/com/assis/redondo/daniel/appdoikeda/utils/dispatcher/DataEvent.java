package com.assis.redondo.daniel.appdoikeda.utils.dispatcher;

import android.content.Context;

public class DataEvent extends SimpleEvent {

    private Object mObject;
    private String mErrorMessage;
    private EventStatus mEventType;
    private int mData;
    private Context mCtx;

    public enum EventStatus {
        SUCCESS,
        FAIL,
        START,
        FINISH
    }

    public static final String ADJUST_RECYCLER_SIZE = "ADJUST_RECYCLER_SIZE";
    public static final String SEARCH_FOR_FRAME = "SEARCH_FOR_FRAME";
    public static final String REFRESH_LIST = "REFRESH_LIST";
    public static final String SHOW_LOADING = "SHOW_LOADING";
    public static final String HIDE_LOADING = "HIDE_LOADING";


    public DataEvent(String type, int data) {
        super(type);
        mData = data;
    }

    public DataEvent(String type, EventStatus eventType, String message) {
        super(type);
        mEventType = eventType;
        mErrorMessage = message;
    }

    public DataEvent(String type, EventStatus eventType, String message, Object object) {
        super(type);
        mEventType = eventType;
        mErrorMessage = message;
        mObject = object;
    }

    public Object getObject() {
        return mObject;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
    public String getDateUsed() {
        return mErrorMessage;
    }


    public EventStatus getEventType() {
        return mEventType;
    }

    public int getData() {
        return mData;
    }

    public Context getContext() {
        return mCtx;
    }

}
