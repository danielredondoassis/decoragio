package com.assis.redondo.daniel.appdoikeda.api.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by DT on 1/11/16.
 */
public class ProjectModel {

    private long id;
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal frame;
    private String passePartout;
    private boolean glassOption;
    private boolean backgroundOption;
    private boolean collageOption;
    private BigDecimal projectTotal;
    private Date created_at;


    public long getId() {
        return id;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = new BigDecimal(height);
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = new BigDecimal(width);
    }

    public BigDecimal getFrameValue() {
        return frame;
    }

    public void setFrame(BigDecimal frame) {
        this.frame = frame;
    }

    public String getPassePartout() {
        return passePartout;
    }

    public void setPassePartout(String passePartout) {
        this.passePartout = passePartout;
    }

    public boolean isGlassOption() {
        return glassOption;
    }

    public void setGlassOption(boolean glassOption) {
        this.glassOption = glassOption;
    }

    public boolean isBackgroundOption() {
        return backgroundOption;
    }

    public void setBackgroundOption(boolean backgroundOption) {
        this.backgroundOption = backgroundOption;
    }

    public boolean isCollageOption() {
        return collageOption;
    }

    public void setCollageOption(boolean collageOption) {
        this.collageOption = collageOption;
    }

    public BigDecimal getProjectTotal() {
        return projectTotal;
    }

    public void setProjectTotal(BigDecimal projectTotal) {
        this.projectTotal = projectTotal;
    }

    public Date getCreatedAt() {
        return created_at;
    }
}
