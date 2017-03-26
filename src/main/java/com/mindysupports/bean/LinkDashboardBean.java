package com.mindysupports.bean;

import com.mindysupports.dto.LinkDTO;
import com.mindysupports.dto.LinkSearchCriteria;
import com.mindysupports.util.Constants;
import com.mindysupports.util.DBUtils;
import com.mindysupports.util.NavigationPath;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@ViewScoped
@ManagedBean(name = "linkDashboardBean")
public class LinkDashboardBean {

    private LinkSearchCriteria searchCriteria = new LinkSearchCriteria();
    private List<LinkDTO> links = new ArrayList<LinkDTO>();
    private LinkDTO editLink = new LinkDTO();
    private LinkDTO addedLink = new LinkDTO();
    private UploadedFile uploadedFile;
    private StreamedContent file;

    public LinkDashboardBean() {
        searchLinks();

    }

    public String showResults(LinkDTO link){
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put(Constants.LINK_DETAILS_KEY, link);
        return NavigationPath.goToLinkResults();
    }

    public void searchLinks() {
        Connection connection = DBUtils.getConnection();
        Statement stmt = null;
        try {
            links.clear();
            stmt = connection.createStatement();
            String sql = "SELECT * FROM pallas.link p WHERE 1=1";
            String name = searchCriteria.getName();
            if (name != null && !name.isEmpty()){
                sql = sql + " AND p.name LIKE '%" + name + "%' ";
            }
            String link = searchCriteria.getLink();
            if (link != null && !link.isEmpty()){
                sql = sql + " AND p.link = '" + link+ "' ";
            }
            Long kFactor = searchCriteria.getkFactor();
            if (kFactor != null && kFactor != 0l){
                sql = sql + " AND p.k_factor = " + kFactor+ " ";
            }
            Long pictureQuantity = searchCriteria.getPictureQuantity();
            if (pictureQuantity != null){
                sql = sql + " AND p.picture_quantity = " + pictureQuantity+ " ";
            }
            String tag = searchCriteria.getTag();
            if (tag != null && !tag.isEmpty()){
                sql = sql + " AND p.tag LIKE '%" + tag+ "%' ";
            }
            String type = searchCriteria.getType();
            if (type != null && !type.isEmpty()){
                sql = sql + " AND p.type LIKE '%" + type+ "%' ";
            }
            if (searchCriteria.getStartDate() != null){
                Timestamp startDate = new Timestamp(searchCriteria.getStartDate().getTime());
                sql = sql + " AND p.start_date = '" + startDate+ "' ";
            }

            if (searchCriteria.getEndDate() != null){
                Timestamp endDate = new Timestamp(searchCriteria.getEndDate().getTime());
                sql = sql + " AND p.end_date = '" + endDate+ "' ";
            }

            ResultSet rs = stmt.executeQuery(sql);


            while (rs.next()) {
                LinkDTO linkResult = new LinkDTO();
                linkResult.setId(rs.getLong("id"));
                linkResult.setName(rs.getString("name"));
                linkResult.setLink(rs.getString("link"));
                linkResult.setkFactor(rs.getLong("k_factor"));
                linkResult.setPictureQuantity(rs.getLong("picture_quantity"));
                linkResult.setTag(rs.getString("tag"));
                linkResult.setType(rs.getString("type"));
                linkResult.setStartDate(new Date(rs.getTimestamp("start_date").getTime()));
                linkResult.setEndDate(new Date(rs.getTimestamp("end_date").getTime()));
                links.add(linkResult);
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


    public void addLink(){
        Connection connection = DBUtils.getConnection();
        PreparedStatement stmt = null;
        try {
            String query = "INSERT INTO pallas.link (name, link, k_factor, " +
                    "tag, start_date, end_date, type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, addedLink.getName());
            stmt.setString(2, addedLink.getLink());
            stmt.setLong(3, addedLink.getkFactor());
            stmt.setString(4, addedLink.getTag());
            stmt.setTimestamp(5, new Timestamp(addedLink.getStartDate().getTime()));
            stmt.setTimestamp(6, new Timestamp(addedLink.getEndDate().getTime()));
            stmt.setString(7, addedLink.getType());
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
        searchLinks();
    }

    public void deleteLink(LinkDTO link){
        Connection connection = DBUtils.getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute("DELETE FROM pallas.link WHERE id = " + link.getId());
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
        searchLinks();
    }

    public void insertLinksBatch(List<LinkDTO> links) {
        Connection connection = DBUtils.getConnection();
        PreparedStatement ps = null;
        try {
            connection.setAutoCommit(false);
            String query = "INSERT INTO pallas.link (name, link, k_factor, " +
                    "tag, start_date, end_date, type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(query);
            for (LinkDTO link : links){
                ps.setString(1, link.getName());
                ps.setString(2, link.getLink());
                ps.setLong(3, link.getkFactor());
                ps.setString(4, link.getTag());
                ps.setTimestamp(5, new Timestamp(link.getStartDate().getTime()));
                ps.setTimestamp(6, new Timestamp(link.getEndDate().getTime()));
                ps.setString(7, link.getType());
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

    public void updateLink(){
        Connection connection = DBUtils.getConnection();
        PreparedStatement stmt = null;
        try {
            links.clear();

            String sql = "UPDATE pallas.link "
                    +" SET name = ?"
                    +",  link = ?"
                    +",  k_factor = ?"
                    +",  tag = ?"
                    +",  start_date = ?"
                    +",  end_date = ?"
                    +",  type = ?"
                    +" WHERE id = ?"
                    + ";";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, editLink.getName());
            stmt.setString(2,  editLink.getLink());
            stmt.setLong(3, editLink.getkFactor());
            stmt.setString(4, editLink.getTag());
            stmt.setTimestamp(5, editLink.getStartDate() != null ? new Timestamp(editLink.getStartDate().getTime()): null);
            stmt.setTimestamp(6, editLink.getEndDate() != null ? new Timestamp(editLink.getEndDate().getTime()) : null);
            stmt.setString(7, editLink.getType());
            stmt.setLong(8, editLink.getId());
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
        searchLinks();
    }

    public void exportToXls() throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Links");
        for (int i = 0;i< links.size(); i++){
            LinkDTO link = links.get(i);
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(link.getName());
            cell = row.createCell(1);
            cell.setCellValue(link.getLink());
            cell = row.createCell(2);
            cell.setCellValue(link.getkFactor());
            cell = row.createCell(3);
            cell.setCellValue(link.getPictureQuantity());
            cell = row.createCell(4);
            cell.setCellValue(link.getTag());
            cell = row.createCell(5);
            cell.setCellValue(link.getType());
            CellStyle cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            cell = row.createCell(6);
            cell.setCellValue(link.getStartDate());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(7);
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
        file = new DefaultStreamedContent(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "exported_links.xlsx");

    }

    public void upload() {
        try {
            InputStream contents = uploadedFile.getInputstream();
            Workbook wb = WorkbookFactory.create(contents);
            Sheet sheet = wb.getSheetAt(0);
            List<LinkDTO> newLinks = new ArrayList<LinkDTO>();
            for (Row row : sheet) {
                if (row.getPhysicalNumberOfCells() < 7){
                    break;
                }
                LinkDTO link = new LinkDTO();
                link.setName(row.getCell(0).getStringCellValue());
                link.setLink(row.getCell(1).getStringCellValue());
                link.setkFactor(Double.valueOf(row.getCell(2).getNumericCellValue()).longValue());
                link.setPictureQuantity(Double.valueOf(row.getCell(3).getNumericCellValue()).longValue());
                link.setTag(row.getCell(4).getStringCellValue());
                link.setType(row.getCell(5).getStringCellValue());
                java.util.Date date = row.getCell(6).getDateCellValue();
                link.setStartDate(date);
                date = row.getCell(7).getDateCellValue();
                link.setEndDate(date);
                newLinks.add(link);
            }
            insertLinksBatch(newLinks);
            searchLinks();
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage("Can not upload file",
                    new FacesMessage("Can not upload file", "Can not upload file"));
        }

    }


    public void clear(){
        searchCriteria = new LinkSearchCriteria();
        searchLinks();
    }

    public void showEditDialog(LinkDTO link){
        editLink = link;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('editWidget').show();");
    }

    public void hideEditDialog(){
        searchLinks();
    }

    public void showAddDialog(){
        addedLink = new LinkDTO();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('addWidget').show();");
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

    public LinkSearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(LinkSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }

    public LinkDTO getEditLink() {
        return editLink;
    }

    public void setEditLink(LinkDTO editLink) {
        this.editLink = editLink;
    }

    public LinkDTO getAddedLink() {
        return addedLink;
    }

    public void setAddedLink(LinkDTO addedLink) {
        this.addedLink = addedLink;
    }
}
