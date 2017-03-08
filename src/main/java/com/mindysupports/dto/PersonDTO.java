package com.mindysupports.dto;


import java.util.Date;

public class PersonDTO {
    private int id;
    private String login;
    private String name;
    private String city;
    private String position;
    private String pePayroll;
    private int hoursType;
    private Date dateOfEmpoyment;
    private Date dateOfLeave;
    private String schedule;
    private double salaryNetto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPePayroll() {
        return pePayroll;
    }

    public void setPePayroll(String pePayroll) {
        this.pePayroll = pePayroll;
    }

    public int getHoursType() {
        return hoursType;
    }

    public void setHoursType(int hoursType) {
        this.hoursType = hoursType;
    }

    public Date getDateOfEmpoyment() {
        return dateOfEmpoyment;
    }

    public void setDateOfEmpoyment(Date dateOfEmpoyment) {
        this.dateOfEmpoyment = dateOfEmpoyment;
    }

    public Date getDateOfLeave() {
        return dateOfLeave;
    }

    public void setDateOfLeave(Date dateOfLeave) {
        this.dateOfLeave = dateOfLeave;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public double getSalaryNetto() {
        return salaryNetto;
    }

    public void setSalaryNetto(double salaryNetto) {
        this.salaryNetto = salaryNetto;
    }
}
