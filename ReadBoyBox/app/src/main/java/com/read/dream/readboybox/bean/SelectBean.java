package com.read.dream.readboybox.bean;

/**
 * add by lzy
 * 设置时间选择表格中，指定行和列的百分比
*/
public class SelectBean {
    private float percent;  //取值
    private int row;
    private int rol;

    public SelectBean() {
    }

    public SelectBean(float percent, int row, int rol) {
        this.percent = percent;
        this.row = row;
        this.rol = rol;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "SelectBean{" +
                "percent=" + percent +
                ", row=" + row +
                ", rol=" + rol +
                '}';
    }
}
