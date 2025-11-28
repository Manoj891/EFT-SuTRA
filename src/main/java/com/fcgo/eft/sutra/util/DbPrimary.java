package com.fcgo.eft.sutra.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            } catch (SQLException e) {
            }
        }
        return 0;
    }
}
