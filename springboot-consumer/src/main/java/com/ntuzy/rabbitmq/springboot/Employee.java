package com.ntuzy.rabbitmq.springboot;

import java.io.Serializable;

public class Employee implements Serializable {
    private String empno;
    private String name;
    private Integer age;


    public Employee(String empno, String name, Integer age) {
        this.empno = empno;
        this.name = name;
        this.age = age;
    }

    public String getEmpno() {
        return empno;
    }

    public void setEmpno(String empno) {
        this.empno = empno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
