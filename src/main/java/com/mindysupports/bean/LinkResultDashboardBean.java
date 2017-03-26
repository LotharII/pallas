package com.mindysupports.bean;

import com.mindysupports.dto.LinkDTO;
import com.mindysupports.dto.LinkResultDTO;
import com.mindysupports.util.Constants;
import com.mindysupports.util.DBUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        Connection connection = DBUtils.getConnection();
        PreparedStatement stmt = null;
        try {
            String query = "UPDATE pallas.link_result SET link_name = ?, quantity = ?, k_factor = ?, " +
                    "hours_spent = ?, person_name = ?, login = ?, start_date = ?, end_date = ?, tag = ?, type = ? " +
                    "where id = ?";

            stmt = connection.prepareStatement(query);
            stmt.setString(1, editLinkResult.getLinkName());
            stmt.setInt(2, editLinkResult.getQuantity());
            stmt.setInt(3, editLinkResult.getkFactor());
            stmt.setDouble(4, editLinkResult.getHoursSpent());
            stmt.setString(5, editLinkResult.getPersonName());
            stmt.setString(6, editLinkResult.getLogin());
            stmt.setTimestamp(7, new Timestamp(editLinkResult.getStartDate().getTime()));
            stmt.setTimestamp(8, new Timestamp(editLinkResult.getEndDate().getTime()));
            stmt.setString(9, editLinkResult.getTag());
            stmt.setString(10, editLinkResult.getType());
            stmt.setLong(11, editLinkResult.getId());
            stmt.executeUpdate();
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
        searchResults();
    }

    public void deleteLink(LinkResultDTO linkToDelete){
        Connection connection = DBUtils.getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute("DELETE FROM pallas.link_result WHERE id = " + linkToDelete.getId());
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
        searchResults();
    }

    public void upload(){
        try {
            InputStream contents = uploadedFile.getInputstream();
            Workbook wb = WorkbookFactory.create(contents);
            Sheet sheet = wb.getSheetAt(0);
            List<LinkResultDTO> newResults = new ArrayList<LinkResultDTO>();
            for (Row row : sheet) {
                if (row.getPhysicalNumberOfCells() < 9){
                    break;
                }
                LinkResultDTO linkResult = new LinkResultDTO();
                linkResult.setLinkName(row.getCell(0).getStringCellValue());
                linkResult.setQuantity(Double.valueOf(row.getCell(1).getNumericCellValue()).intValue());
                linkResult.setkFactor(Double.valueOf(row.getCell(2).getNumericCellValue()).intValue());
                linkResult.setHoursSpent(Double.valueOf(row.getCell(3).getNumericCellValue()));
                linkResult.setPayment(Double.valueOf(row.getCell(4).getNumericCellValue()));
                linkResult.setPersonName(row.getCell(5).getStringCellValue());
                linkResult.setLogin(row.getCell(6).getStringCellValue());
                linkResult.setTag(row.getCell(7).getStringCellValue());
                linkResult.setType(row.getCell(8).getStringCellValue());
                java.util.Date date = row.getCell(9).getDateCellValue();
                linkResult.setStartDate(date);
                date = row.getCell(10).getDateCellValue();
                linkResult.setEndDate(date);
                newResults.add(linkResult);
            }
            insertResultsBatch(newResults);
            searchResults();
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage("Can not upload file",
                    new FacesMessage("Can not upload file", "Can not upload file"));
        }

    }

    public void insertResultsBatch(List<LinkResultDTO> results) {
        Connection connection = DBUtils.getConnection();
        PreparedStatement stmt = null;
        try {
            connection.setAutoCommit(false);
            String query = "INSERT INTO pallas.link_result (link_name, quantity, k_factor, " +
                    "hours_spent, payment, person_name, login, start_date, end_date, tag, type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            for (LinkResultDTO result : results){

                stmt.setString(1, result.getLinkName());
                stmt.setInt(2, result.getQuantity());
                stmt.setInt(3, result.getkFactor());
                stmt.setDouble(4, result.getHoursSpent());
                stmt.setDouble(5, result.getPayment());
                stmt.setString(6, result.getPersonName());
                stmt.setString(7, result.getLogin());
                stmt.setTimestamp(8, new Timestamp(result.getStartDate().getTime()));
                stmt.setTimestamp(9, new Timestamp(result.getEndDate().getTime()));
                stmt.setString(10, result.getTag());
                stmt.setString(11, result.getType());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
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

    public void exportToXls() throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Links");
        for (int i = 0;i< linkResults.size(); i++){
            LinkResultDTO link = linkResults.get(i);
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(link.getLinkName());
            cell = row.createCell(1);
            cell.setCellValue(link.getQuantity());
            cell = row.createCell(2);
            cell.setCellValue(link.getkFactor());
            cell = row.createCell(3);
            cell.setCellValue(link.getHoursSpent());
            cell = row.createCell(4);
            cell.setCellValue(link.getPayment());
            cell = row.createCell(5);
            cell.setCellValue(link.getPersonName());
            cell = row.createCell(6);
            cell.setCellValue(link.getLogin());
            cell = row.createCell(7);
            cell.setCellValue(link.getTag());
            cell = row.createCell(8);
            cell.setCellValue(link.getType());
            CellStyle cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            cell = row.createCell(9);
            cell.setCellValue(link.getStartDate());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(10);
            cellStyle = wb.createCellStyle();
            createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            cell.setCellValue(link.getEndDate());
            cell.setCellStyle(cellStyle);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        InputStream stream = new ByteArrayInputStream(out.toByteArray());
        file = new DefaultStreamedContent(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "exported_results.xlsx");

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
