package com.edue.courso;

public class Course {
    private String CourseCodes;
    private String CourseName;
    private String file;

    Course() {
    }

    public String getCourseCodes() {
        return CourseCodes;
    }

    void setCourseCodes(String courseCodes) {
        CourseCodes = courseCodes;
    }

    public String getCourseName() {
        return CourseName;
    }

    void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getFile() {
        return file;
    }

     void setFile(String file) {
        this.file = file;
    }
}
