package com.fcgo.eft.sutra.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbPrimary {
    private Connection con = null;

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        con = DriverManager.getConnection("jdbc:sqlserver://10.100.199.148:1433;databaseName=SuTRA5;encrypt=false", "SuTRA_FCGO_LLG", "caPsKJSkD2-k38lEG4K");
        con.setAutoCommit(true);
    }

    public int update(String sql) throws SQLException, ClassNotFoundException {
        if (con == null || con.isClosed()) init();
        try {
            return con.prepareStatement(sql).executeUpdate();

        } catch (SQLException ignored) {
            try {
                init();
            } catch (SQLException ignored1) {
            }
        }
        return 0;
    }

    public void updateStatusProcessing(String instructionId) {
        try {
            if (con == null || con.isClosed()) init();
            con.prepareStatement("update acc_epayment set transtatus=2,pstatus=2,paymentdate=GETDATE() where eftno=" + instructionId).executeUpdate();
        } catch (Exception ignored1) {
        }
    }

    private Connection realTime = null;
    private Statement realTimeStatement = null;

    public void realTimeInit() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            realTime = DriverManager.getConnection("jdbc:sqlserver://10.100.199.148:1433;databaseName=SuTRA5;encrypt=false", "SuTRA_FCGO_LLG", "caPsKJSkD2-k38lEG4K");
            realTime.setAutoCommit(true);
            realTimeStatement = realTime.createStatement();
        } catch (Exception ignored) {
        }
    }

    public void realTimeClose() {
        try {
            realTime.isClosed();
            realTimeStatement.close();
        } catch (Exception ignored) {
        }
    }

    public int realTimeStatusUpdate(String sql) {
        try {
            return realTimeStatement.executeUpdate(sql);
        } catch (Exception ignored) {
        }
        return 0;
    }

}
