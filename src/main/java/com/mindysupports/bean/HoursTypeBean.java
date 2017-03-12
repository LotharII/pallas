package com.mindysupports.bean;

import com.mindysupports.dto.HourTypeDTO;
import com.mindysupports.dto.PersonDTO;
import com.mindysupports.util.DBUtils;
import org.primefaces.context.RequestContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "hoursTypeBean")
public class HoursTypeBean {

    private List<HourTypeDTO> hourTypes = new ArrayList<HourTypeDTO>();
    private HourTypeDTO editHours = new HourTypeDTO();
    private HourTypeDTO addType = new HourTypeDTO();

    public HoursTypeBean() {
        searchHoursType();
    }

    public void searchHoursType(){
        Connection connection = DBUtils.getConnection();
        try {
            hourTypes.clear();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pallas.hours_type");
            while (rs.next()) {
                HourTypeDTO hourTypeDTO = new HourTypeDTO();
                hourTypeDTO.setAmount(rs.getInt("amount"));
                hourTypeDTO.setType(rs.getString("type"));
                hourTypes.add(hourTypeDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addHourType(){
        Connection connection = DBUtils.getConnection();
        try {
            String sql = "INSERT INTO pallas.hours_type "
                    + "(amount, type) VALUES"
                    + "(?,?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, addType.getAmount());
            stmt.setString(2, addType.getType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchHoursType();
    }

    public void updateHoursType(){
        Connection connection = DBUtils.getConnection();
        try {
            String sql = "UPDATE pallas.hours_type "
                    +" SET amount = ?"
                    +" WHERE type = ?"
                    + ";";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, editHours.getAmount());
            stmt.setString(2, editHours.getType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchHoursType();
    }

    public void delete(HourTypeDTO hours){
        Connection connection = DBUtils.getConnection();
        try {
            Statement stmt = connection.createStatement();
            String sql = "DELETE FROM pallas.hours_type " +
                    "WHERE type = '" + hours.getType() +"';";;
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchHoursType();
    }

    public void showEditDialog(HourTypeDTO hours){
        editHours = hours;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('editWidget').show();");
    }

    public void showAddDialog(){
        addType = new HourTypeDTO();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addWidget').show();");
    }

    public void hideEditDialog(){
        searchHoursType();
    }

    public List<HourTypeDTO> getHourTypes() {
        return hourTypes;
    }

    public void setHourTypes(List<HourTypeDTO> hourTypes) {
        this.hourTypes = hourTypes;
    }

    public HourTypeDTO getEditHours() {
        return editHours;
    }

    public void setEditHours(HourTypeDTO editHours) {
        this.editHours = editHours;
    }

    public HourTypeDTO getAddType() {
        return addType;
    }

    public void setAddType(HourTypeDTO addType) {
        this.addType = addType;
    }
}
