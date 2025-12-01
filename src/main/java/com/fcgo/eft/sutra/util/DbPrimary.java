package com.fcgo.eft.sutra.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbPrimary {

    private Connection statusUpdate = null;
    private Statement statusUpdateStatement = null;


    public boolean initStatusUpdate() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            statusUpdate = DriverManager.getConnection("jdbc:sqlserver://10.100.199.148:1433;databaseName=SuTRA5;encrypt=false", "SuTRA_FCGO_LLG", "caPsKJSkD2-k38lEG4K");
            statusUpdateStatement = statusUpdate.createStatement();
            statusUpdate.setAutoCommit(true);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }


    public int statusUpdate(String sql) {
        try {
            return statusUpdateStatement.executeUpdate(sql);
        } catch (SQLException ignored) {
        }
        return 0;
    }

    public void closeStatusUpdate() {
        try {
            statusUpdate.close();
            statusUpdateStatement.close();
        } catch (Exception ignored) {
        }
    }



    public int update(String sql) {
        int row;
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://10.100.199.148:1433;databaseName=SuTRA5;encrypt=false", "SuTRA_FCGO_LLG", "caPsKJSkD2-k38lEG4K");
            con.setAutoCommit(true);
            PreparedStatement ps = con.prepareStatement(sql);
            row = ps.executeUpdate();
        } catch (Exception ignored) {
            row = 0;
        } finally {
            try {
                assert con != null;
                con.close();
            } catch (SQLException ignored) {
            }
        }
        return row;
    }


}
