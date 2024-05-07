package com.edeapp;

public class Student {
    private String id;
    private String result;

    public Student(String Id, String Result) {
        this.id = Id;
        this.result = Result;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String isResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
