package com.fcgo.eft.sutra.util;


import com.fcgo.eft.sutra.dto.EftStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DB2nd {
    private Connection con = null;

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        log.info("Connecting to database...");
        con = DriverManager.getConnection("jdbc:sqlserver://10.100.199.149:1433;databaseName=SuTRA5;encrypt=false", "SuTRA_FCGO_SEC", "caPsKJSkD2-k38lE2082");
        log.info("Connected to database");
    }

    public EftStatus getRecord(String eftNo) throws SQLException, ClassNotFoundException {
        if (con == null || con.isClosed()) init();
        System.out.println("select transtatus,pstatus from acc_epayment where eftno=" + eftNo);
        try {
            ResultSet rs = con.prepareStatement("select transtatus,pstatus from acc_epayment where eftno=" + eftNo).executeQuery();
            if (rs.next()) {
                return EftStatus.builder().pstatus(rs.getInt("pstatus")).transtatus(rs.getInt("transtatus")).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
