package com.assis.redondo.daniel.appdoikeda.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by DT on 9/25/15.
 */
@DatabaseTable
public class DbInsumoModel {

    public static final String INSUMO_ID = "insumoId";
    public static final String BACKGROUND_VALUE = "backgroundValue";
    public static final String COLLAGE_VALUE = "collageValue";
    public static final String GLASS_VALUE = "glassValue";
    public static final String PASSPARTOUT_VALUE = "passpartoutValue";

    @DatabaseField(id=true, columnName = INSUMO_ID)
    private int insumoId;

    @DatabaseField(columnName = BACKGROUND_VALUE)
    private String backgroundValue;
    @DatabaseField(columnName = COLLAGE_VALUE)
    private String collageValue;
    @DatabaseField(columnName = GLASS_VALUE)
    private String glassValue;
    @DatabaseField(columnName = PASSPARTOUT_VALUE)
    private String passpartoutValue;

    public int getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(int insumoId) {
        this.insumoId = insumoId;
    }

    public String getBackgroundValue() {
        return backgroundValue;
    }

    public String getCollageValue() {
        return collageValue;
    }

    public String getGlassValue() {
        return glassValue;
    }

    public String getPasspartoutValue() {
        return passpartoutValue;
    }

    public void setBackgroundValue(String backgroundValue) {
        this.backgroundValue = backgroundValue;
    }

    public void setCollageValue(String collageValue) {
        this.collageValue = collageValue;
    }

    public void setGlassValue(String glassValue) {
        this.glassValue = glassValue;
    }

    public void setPasspartoutValue(String passpartoutValue) {
        this.passpartoutValue = passpartoutValue;
    }
}
