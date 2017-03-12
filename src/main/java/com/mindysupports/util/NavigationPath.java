package com.mindysupports.util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

@SessionScoped
@ManagedBean(name = "navigation")
public class NavigationPath {
    public static String PERSON_DASHBOARD = "personDashboard";
    public static String OVERVIEW = "overview";
    public static String HOURS_TYPE = "hoursType";

    public static String goToOverview(){
        return NavigationPath.OVERVIEW;
    }


    public static String goToPersonDashboard(){
        return NavigationPath.PERSON_DASHBOARD;
    }

    public static String goToHoursType(){
        return NavigationPath.HOURS_TYPE;
    }
}
