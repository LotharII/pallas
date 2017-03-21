package com.mindysupports.dto;


import java.util.Date;

public class LinkDTO {

    private Long id;
    private String name;
    private String link;
    private Long kFactor;
    private Long pictureQuantity;
    private String tag;
    private Date startDate;
    private Date endDate;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getkFactor() {
        return kFactor;
    }

    public void setkFactor(Long kFactor) {
        this.kFactor = kFactor;
    }

    public Long getPictureQuantity() {
        return pictureQuantity;
    }

    public void setPictureQuantity(Long pictureQuantity) {
        this.pictureQuantity = pictureQuantity;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
