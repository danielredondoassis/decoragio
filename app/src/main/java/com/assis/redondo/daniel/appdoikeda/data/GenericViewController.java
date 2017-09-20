package com.assis.redondo.daniel.appdoikeda.data;

import android.content.Context;

import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.DataEvent;
import com.assis.redondo.daniel.appdoikeda.utils.dispatcher.EventDispatcher;
import com.google.gson.Gson;

public class GenericViewController extends EventDispatcher {

    private static GenericViewController instance;
    static final Object sLock = new Object();
    private Gson mGson;
    private Context context;
    private String contactOrPlaceReference;
    private int mPagerCurrentPos;
    private int mCalendarFragmentPos;
    private int mTransactionPagerCurrentPos;
    private int mViewTypeSelected;


    public static GenericViewController getInstance(Context ctx) {
        synchronized (sLock) {
            if (instance == null) {
                instance = new GenericViewController(ctx.getApplicationContext());
            }
            return instance;
        }
    }

    public GenericViewController(Context ctx) {
        context = ctx;
    }

    public void adjustRecyclerViewSize(int size){
        dispatchEvent(new DataEvent(DataEvent.ADJUST_RECYCLER_SIZE, DataEvent.EventStatus.FINISH, "", size));
    }

    public String getContactOrPlaceReference() {
        return contactOrPlaceReference;
    }

    public void setContactOrPlaceReference(String contactOrPlaceReference) {
        this.contactOrPlaceReference = contactOrPlaceReference;
        dispatchEvent(new DataEvent(DataEvent.SEARCH_FOR_FRAME, DataEvent.EventStatus.FINISH, "", contactOrPlaceReference));
    }

    public void refreshList(){
        dispatchEvent(new DataEvent(DataEvent.REFRESH_LIST, DataEvent.EventStatus.FINISH, "", 0));
    }

    public void showLoading() {
        dispatchEvent(new DataEvent(DataEvent.SHOW_LOADING, DataEvent.EventStatus.FINISH, "", 0));
    }

    public void hideLoading() {
        dispatchEvent(new DataEvent(DataEvent.HIDE_LOADING, DataEvent.EventStatus.FINISH, "", 0));
    }
}
