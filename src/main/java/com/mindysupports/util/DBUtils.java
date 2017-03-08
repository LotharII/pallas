package com.mindysupports.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by lotha_000 on 3/8/2017.
 */
public class DBUtils {

    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/pallas","root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
