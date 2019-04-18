package com.edue.courso;

import java.util.ArrayList;

public class CourseCodes {
    private String codeName;
    private ArrayList<Files> files;

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public ArrayList<Files> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<Files> files) {
        this.files = files;
    }
}
