package com.mindysupports.bean;

import com.mindysupports.dto.LinkDTO;
import com.mindysupports.dto.LinkResultDTO;
import com.mindysupports.util.Constants;
import com.mindysupports.util.DBUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean(name = "linkResultDashboardBean")
public class LinkResultDashboardBean {

    private LinkDTO selectedLink;
    private List<LinkResultDTO> linkResults = new ArrayList<LinkResultDTO>();
    private LinkResultDTO editLinkResult = new LinkResultDTO();
    private LinkResultDTO addedLinkResult = new LinkResultDTO();
    private UploadedFile uploadedFile;
    private StreamedContent file;
    private Date searchStartDate;
    private Date searchEndDate;

    public LinkResultDashboardBean() {
        selectedLink = (LinkDTO) FacesContext.getCurrentInstance().getExternalContext().getFlash().get(Constants.LINK_DETAILS_KEY);
        searchResults();
    }

    @PostConstruct
    public void init(){
    }

    public void searchResults(){
        Connection connection = DBUtils.getConnection();
        Statement stmt = null;
        try {
            linkResults.clear();
            stmt = connection.createStatement();
            String sql = "SELECT * FROM pallas.link_result p WHERE 1=1";
            sql = sql + " AND p.type = '" + selectedLink.getType() + "' ";
            sql = sql + " AND p.link_name = '" + selectedLink.getName()+ "' ";
            if (searchStartDate != null) {
                sql = sql + " AND p.start_date >= '" + new Timestamp(searchStartDate.getTime())+ "' ";
            }
            if (searchEndDate != null) {
                sql = sql + " AND p.end_date <= '" + new Timestamp(searchEndDate.getTime())+ "' ";
            }
            ResultSet rs = stmt.executeQuery(sql);


            while (rs.next()) {
                LinkResultDTO linkResult = new LinkResultDTO();
                linkResult.setId(rs.getLong("id"));
                linkResult.setLinkName(rs.getString("link_name"));
                linkResult.setQuantity(rs.getInt("quantity"));
                linkResult.setkFactor(rs.getInt("k_factor"));
                linkResult.setHoursSpent(rs.getDouble("hours_spent"));
                linkResult.setPayment(rs.getDouble("payment"));
                linkResult.setPersonName(rs.getString("payment"));
                linkResult.setLogin(rs.getString("login"));
                linkResult.setTag(rs.getString("tag"));
                linkResult.setType(rs.getString("type"));
                linkResult.setStartDate(new Date(rs.getTimestamp("start_date").getTime()));
                linkResult.setEndDate(new Date(rs.getTimestamp("end_date").getTime()));
                linkResults.add(linkResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                stmt.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addLinkResult(){
        addedLinkResult.setLinkName(selectedLink.getName());
        addedLinkResult.setType(selectedLink.getType());
        Connection connection = DBUtils.getConnection();
        PreparedStatement stmt = null;
        try {
            String query = "INSERT INTO pallas.link_result (link_name, quantity, k_factor, " +
                    "hours_spent, payment, person_name, login, start_date, end_date, tag, type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, addedLinkResult.getLinkName());
            stmt.setInt(2, addedLinkResult.getQuantity());
            stmt.setInt(3, addedLinkResult.getkFactor());
            stmt.setDouble(4, addedLinkResult.getHoursSpent());
            stmt.setDouble(5, addedLinkResult.getPayment());
            stmt.setString(6, addedLinkResult.getPersonName());
            stmt.setString(7, addedLinkResult.getLogin());
            stmt.setTimestamp(8, new Timestamp(addedLinkResult.getStartDate().getTime()));
            stmt.setTimestamp(9, new Timestamp(addedLinkResult.getEndDate().getTime()));
            stmt.setString(10, addedLinkResult.getTag());
            stmt.setString(11, addedLinkResult.getType());
            stmt.execute();
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
        searchResults();
    }


    public void updateResult(){

    }

    public void deleteLink(LinkResultDTO linkToDelete){

    }

    public void upload(){

    }

    public void exportToXls(){

    }



    public void showEditDialog(LinkResultDTO link){
        editLinkResult = link;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('editWidget').show();");
    }

    public void hideEditDialog(){
        searchResults();
    }

    public void showAddDialog(){
        addedLinkResult = new LinkResultDTO();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addWidget').show();");
    }

    public LinkDTO getSelectedLink() {
        return selectedLink;
    }

    public void setSelectedLink(LinkDTO selectedLink) {
        this.selectedLink = selectedLink;
    }

    public List<LinkResultDTO> getLinkResults() {
        return linkResults;
    }

    public void setLinkResults(List<LinkResultDTO> linkResults) {
        this.linkResults = linkResults;
    }

    public LinkResultDTO getEditLinkResult() {
        return editLinkResult;
    }

    public void setEditLinkResult(LinkResultDTO editLinkResult) {
        this.editLinkResult = editLinkResult;
    }

    public LinkResultDTO getAddedLinkResult() {
        return addedLinkResult;
    }

    public void setAddedLinkResult(LinkResultDTO addedLinkResult) {
        this.addedLinkResult = addedLinkResult;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public Date getSearchStartDate() {
        return searchStartDate;
    }

    public void setSearchStartDate(Date searchStartDate) {
        this.searchStartDate = searchStartDate;
    }

    public Date getSearchEndDate() {
        return searchEndDate;
    }

    public void setSearchEndDate(Date searchEndDate) {
        this.searchEndDate = searchEndDate;
    }
}
