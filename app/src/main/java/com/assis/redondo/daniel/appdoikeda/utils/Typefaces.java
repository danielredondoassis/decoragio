package com.assis.redondo.daniel.appdoikeda.utils;

/**
 * Created by Rogerio Shimizu on 9/10/13.
 */

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;

public class Typefaces {

    private static final String TAG = Typefaces.class.getSimpleName();

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public interface Font {
        String getName();
    }

    public static Typeface get(AssetManager assets, Font font) {
        synchronized (cache) {
            if (!cache.containsKey(font.getName())) {
                try {
                    Typeface t = Typeface.createFromAsset(assets, "fonts/" + font.getName());
                    cache.put(font.getName(), t);
                } catch (Exception e) {
                    Log.e(TAG, "Typeface error '" + font.getName()
                            + "' : " + e.getMessage());
                    return null;
                }
            }
            return cache.get(font.getName());
        }
    }

    public static void setTextFont(AssetManager assets, TextView text, Font font) {
        if(text==null) return;
        Typeface tf =  get(assets, font);
        if(tf != null) {
            text.setTypeface(tf);
        }
    }

    public static void setButtonTextFont(AssetManager assets, Button button, Font font) {
        if(button==null) return;
        Typeface tf =  get(assets, font);
        if(tf != null) {
            button.setTypeface(tf);
        }
    }

}