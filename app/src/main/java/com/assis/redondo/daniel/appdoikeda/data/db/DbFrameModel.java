package com.assis.redondo.daniel.appdoikeda.data.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by DT on 9/25/15.
 */
@DatabaseTable
public class DbFrameModel {

    public static final String FRAME_ID = "frameCode";
    public static final String FRAME_VALUE = "frameValue";

    @DatabaseField(id=true, columnName = FRAME_ID)
    private String frameId;

    @DatabaseField(columnName = FRAME_VALUE)
    private String frameValue;

    public String getFrameId() {
        return frameId;
    }
    public String getFrameValue() {
        return frameValue;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public void setFrameValue(String frameValue) {
        this.frameValue = frameValue;
    }
}
