package com.edeapp;

public class Student {
    private String id;
    private String output;

    private boolean result;

    private boolean isCompiled;
    private boolean isRan;

    public Student(String id, boolean result) {
        this.id = id;
        this.result = result;
    }

    public Student(){

    }


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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
