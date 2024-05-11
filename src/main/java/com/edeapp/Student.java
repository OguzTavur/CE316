package com.edeapp;

public class Student {
    private String id;
    private String result;

    private boolean isCompiled;

    public boolean isCompiled() {
        return isCompiled;
    }

    public void setCompiled(boolean compiled) {
        isCompiled = compiled;
    }

    public boolean isRan() {
        return isRan;
    }

    public void setRan(boolean ran) {
        isRan = ran;
    }

    private boolean isRan;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
