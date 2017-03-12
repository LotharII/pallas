package com.mindysupports.bean;

import com.mindysupports.dto.HourTypeDTO;
import com.mindysupports.dto.PersonDTO;
import com.mindysupports.dto.PersonSearchCriteria;
import com.mindysupports.util.DBUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean(name = "personBean")
public class PersonBean {

    private List<PersonDTO> persons = new ArrayList<PersonDTO>();
    private PersonDTO editPerson = new PersonDTO();
    private List<HourTypeDTO> hourTypes = new ArrayList<HourTypeDTO>();
    private PersonSearchCriteria searchCriteria = new PersonSearchCriteria();
    private UploadedFile uploadedFile;

    public PersonBean(){
        searchPersons();
        searchHourTypes();
    }

    private void searchHourTypes(){
        Connection connection = DBUtils.getConnection();
        Statement stmt = null;
        try {
            hourTypes.clear();
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pallas.hours_type");
            while (rs.next()) {
                HourTypeDTO hourTypeDTO = new HourTypeDTO();
                hourTypeDTO.setAmount(rs.getInt("amount"));
                hourTypeDTO.setType(rs.getString("type"));
                hourTypes.add(hourTypeDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void upload() throws IOException, InvalidFormatException, ParseException{
        InputStream contents = uploadedFile.getInputstream();
        Workbook wb = WorkbookFactory.create(contents);
        Sheet sheet = wb.getSheetAt(0);
        List<PersonDTO> newPersons = new ArrayList<PersonDTO>();
        for (Row row : sheet) {
            if (row.getPhysicalNumberOfCells() < 8){
                break;
            }
            PersonDTO person = new PersonDTO();
            person.setName(row.getCell(0).getStringCellValue());
            person.setLogin(row.getCell(1).getStringCellValue());
            person.setCity(row.getCell(2).getStringCellValue());
            person.setPosition(row.getCell(3).getStringCellValue());
            person.setPePayroll(row.getCell(4).getStringCellValue());
            person.setHoursType(row.getCell(5).getStringCellValue());
            SimpleDateFormat parser=new SimpleDateFormat("dd.MM.yyyy");
            Date date = parser.parse(row.getCell(6).getStringCellValue());
            person.setDateOfEmployment(date);
            date = row.getCell(7) != null ? parser.parse(row.getCell(7).getStringCellValue()) : null;
            person.setDateOfLeave(date);
            person.setSchedule(row.getCell(8).getStringCellValue());
            newPersons.add(person);
        }
        insertPersonsBatch(newPersons);
        searchPersons();
    }

    public void deletePeson(PersonDTO person){
        Connection connection = DBUtils.getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute("DELETE FROM pallas.person WHERE id = " + person.getId());
        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                stmt.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        searchPersons();
    }

    public void insertPersonsBatch(List<PersonDTO> persons) {
        Connection connection = DBUtils.getConnection();
        PreparedStatement ps = null;
        try {
            connection.setAutoCommit(false);
            String query = "INSERT INTO pallas.person (login, person_name, city, position, " +
                    "pe_payroll, hours_type, date_of_employment, date_of_leave, schedule) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(query);
            for (PersonDTO person : persons){
                ps.setString(1, person.getLogin());
                ps.setString(2, person.getName());
                ps.setString(3, person.getCity());
                ps.setString(4, person.getPosition());
                ps.setString(5, person.getPePayroll());
                ps.setString(6, person.getHoursType());
                ps.setTimestamp(7, new Timestamp(person.getDateOfEmployment().getTime()));
                if (person.getDateOfLeave() != null){
                    ps.setTimestamp(8, new Timestamp(person.getDateOfLeave().getTime()));
                } else {
                    ps.setNull(8, Types.TIMESTAMP);
                }
                ps.setString(9, person.getSchedule());
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void clear(){
        searchCriteria = new PersonSearchCriteria();
        searchPersons();
    }

    public void searchPersons() {
        Connection connection = DBUtils.getConnection();
        try {
            persons.clear();
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM pallas.person p JOIN pallas.hours_type h ON p.hours_type = h.type WHERE 1=1";
            String name = searchCriteria.getName();
            if (name != null && !name.isEmpty()){
                sql = sql + " AND p.person_name LIKE '%" + name + "%' ";
            }
            String hoursType = searchCriteria.getHoursType();
            if (hoursType != null && !hoursType.isEmpty()){
                sql = sql + " AND p.hours_type = '" + hoursType+ "' ";
            }
            String city = searchCriteria.getCity();
            if (city != null && !city.isEmpty()){
                sql = sql + " AND p.city = '" + city+ "' ";
            }
            String login = searchCriteria.getLogin();
            if (login != null && !login.isEmpty()){
                sql = sql + " AND p.login LIKE '%" + login+ "%'' ";
            }
            String pePayroll = searchCriteria.getPePayroll();
            if (pePayroll != null && !pePayroll.isEmpty()){
                sql = sql + " AND p.pe_payroll LIKE '%" + pePayroll+ "%' ";
            }
            String position = searchCriteria.getPosition();
            if (position != null && !position.isEmpty()){
                sql = sql + " AND p.position LIKE '%" + position+ "%' ";
            }
            String schedule = searchCriteria.getSchedule();
            if (schedule != null && !schedule.isEmpty()){
                sql = sql + " AND p.schedule LIKE '%" + schedule+ "%' ";
            }
            if (searchCriteria.getDateOfEmployment() != null){
                Timestamp dateOfEmployment = new Timestamp(searchCriteria.getDateOfEmployment().getTime());
                sql = sql + " AND p.date_of_employment = '" + dateOfEmployment+ "' ";
            }

            if (searchCriteria.getDateOfLeave() != null){
            Timestamp dateOfLeave = new Timestamp(searchCriteria.getDateOfLeave().getTime());
                sql = sql + " AND p.date_of_leave = '" + dateOfLeave+ "' ";
            }

            ResultSet rs = stmt.executeQuery(sql);


            while (rs.next()) {
                PersonDTO person = new PersonDTO();
                person.setId(rs.getInt("id"));
                person.setName(rs.getString("person_name"));
                person.setLogin(rs.getString("login"));
                person.setCity(rs.getString("city"));
                person.setPosition(rs.getString("position"));
                person.setPePayroll(rs.getString("pe_payroll"));
                person.setHoursAmount(rs.getInt("amount"));
                person.setHoursType(rs.getString("hours_type"));
                person.setDateOfEmployment(new Date(rs.getTimestamp("date_of_employment").getTime()));
                if (rs.getTimestamp("date_of_leave") != null){
                    person.setDateOfLeave(new Date(rs.getTimestamp("date_of_leave").getTime()));
                }
                person.setSchedule(rs.getString("schedule"));
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

    public void showEditDialog(PersonDTO person){
        editPerson = person;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('editWidget').show();");
    }

    public void hideEditDialog(){
        searchPersons();
    }

    public void updatePerson(){
        Connection connection = DBUtils.getConnection();
        try {
            persons.clear();

            String sql = "UPDATE pallas.person "
                    +" SET person_name = ?"
                    +",  login = ?"
                    +",  city = ?"
                    +",  position = ?"
                    +",  pe_payroll = ?"
                    +",  hours_type = ?"
                    +",  date_of_employment = ?"
                    +",  date_of_leave = ?"
                    +",  schedule = ?"
                    +" WHERE id = ?"
                    + ";";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, editPerson.getName());
            stmt.setString(2,  editPerson.getLogin());
            stmt.setString(3, editPerson.getCity());
            stmt.setString(4, editPerson.getPosition());
            stmt.setString(5, editPerson.getPePayroll());
            stmt.setString(6, editPerson.getHoursType());
            stmt.setTimestamp(7, editPerson.getDateOfEmployment() != null ? new Timestamp(editPerson.getDateOfEmployment().getTime()): null);
            stmt.setTimestamp(8, editPerson.getDateOfLeave() != null ? new Timestamp(editPerson.getDateOfLeave().getTime()) : null);
            stmt.setString(9, editPerson.getSchedule());
            stmt.setInt(10, editPerson.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchPersons();
    }

    public PersonDTO getEditPerson() {
        return editPerson;
    }

    public void setEditPerson(PersonDTO editPerson) {
        this.editPerson = editPerson;
    }

    public List<HourTypeDTO> getHourTypes() {
        return hourTypes;
    }

    public void setHourTypes(List<HourTypeDTO> hourTypes) {
        this.hourTypes = hourTypes;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public PersonSearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(PersonSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
}
