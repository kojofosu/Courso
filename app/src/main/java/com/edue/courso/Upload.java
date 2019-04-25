package com.edue.courso;

import java.util.ArrayList;

public class Upload {
    //Upload to database
    private String DeptName;
    private String Programme;
    private String levelNum;
    private String CourseCodes;
    private String CourseName;
    private String File;


    public Upload() {
    }

    public String getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(String levelNum) {
        this.levelNum = levelNum;
    }

    public String getCourseCodes() {
        return CourseCodes;
    }

    public void setCourseCodes(String courseCodes) {
        CourseCodes = courseCodes;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getFile() {
        return File;
    }

    public void setFile(String file) {
        File = file;
    }

    public String getDeptName() {
        return DeptName;
    }

    public void setDeptName(String deptName) {
        DeptName = deptName;
    }

    public String getProgramme() {
        return Programme;
    }

    public void setProgramme(String programme) {
        Programme = programme;
    }


}
