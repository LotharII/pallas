package com.mindysupports.bean;

import com.mindysupports.dto.PersonDTO;
import com.mindysupports.util.DBUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "personBean")
public class PersonBean {

    private List<PersonDTO> persons = new ArrayList<PersonDTO>();

    public PersonBean(){
        Connection connection = DBUtils.getConnection();
        try {
            persons.clear();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pallas.person p JOIN pallas.hours_type h ON p.hours_type = h.type");
            while (rs.next()) {
                PersonDTO person = new PersonDTO();
                person.setId(rs.getInt("id"));
                person.setName(rs.getString("person_name"));
                person.setLogin(rs.getString("login"));
                person.setCity(rs.getString("city"));
                person.setPosition(rs.getString("position"));
                person.setPePayroll(rs.getString("pe-payroll"));
                person.setHoursType(rs.getInt("amount"));
                person.setDateOfEmpoyment(rs.getDate("date_of_empoyment"));
                person.setDateOfLeave(rs.getDate("date_of_leave"));
                person.setSchedule(rs.getString("schedule"));
                person.setSalaryNetto(rs.getDouble("salary_netto"));
                persons.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @PostConstruct
    public void init() throws SQLException{

    }

    public List<PersonDTO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonDTO> persons) {
        this.persons = persons;
    }
}
