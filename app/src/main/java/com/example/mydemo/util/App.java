package com.example.mydemo.util;

public class App{
    private String packageName;
    private String realName;
    private long frontTime;
    private boolean systemApp;

    public App() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public long getFrontTime() {
        return frontTime;
    }

    public void setFrontTime(long frontTime) {
        this.frontTime = frontTime;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    public void setSystemApp(boolean systemApp) {
        this.systemApp = systemApp;
    }

    @Override
    public String toString() {
        return "App{" +
                "packageName='" + packageName + '\'' +
                ", realName='" + realName + '\'' +
                ", frontTime=" + frontTime +
                ", systemApp=" + systemApp +
                '}';
    }
}
