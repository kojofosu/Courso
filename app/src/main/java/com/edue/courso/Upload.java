package com.edue.courso;

import java.util.ArrayList;

public class Upload {
    //Upload to database
    private String DeptName;
    private String Programme;
    private String LevelNum;
    private String CourseName;
    private String CourseCode;

    public Upload() {
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

    public String getLevelNum() {
        return LevelNum;
    }

    public void setLevelNum(String levelNum) {
        LevelNum = levelNum;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }
}
