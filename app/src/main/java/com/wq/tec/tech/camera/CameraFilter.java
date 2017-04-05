package com.wq.tec.tech.camera;

/**
 * Created by NoName on 2017/4/5.
 */

public enum CameraFilter {

    WITHEN(1);

    private int level;

    CameraFilter(int level){
        this.level = level;
    }

    public CameraFilter setLevel(int level) {
        this.level = level;
        return this;
    }

    public int getLevel() {
        return level;
    }
}
