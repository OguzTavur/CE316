package com.edeapp;

public class Student {
    private String id;
    private boolean result;

    public Student(String Id, boolean Result) {
        this.id = Id;
        this.result = Result;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
