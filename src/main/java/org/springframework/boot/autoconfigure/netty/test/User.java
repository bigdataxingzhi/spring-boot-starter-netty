package org.springframework.boot.autoconfigure.netty.test;


import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Null;

public class User {

    @Null()
    private String name;

    @Length(min=200,max=500,groups = TestGroup.class)
    private String grade;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


}
