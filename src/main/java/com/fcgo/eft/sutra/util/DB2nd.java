package com.fcgo.eft.sutra.util;


import com.fcgo.eft.sutra.dto.EftStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DB2nd {
    private Connection con = null;

    private void init() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        con = DriverManager.getConnection("jdbc:sqlserver://10.100.199.149:1433;databaseName=SuTRA5;encrypt=false", "SuTRA_FCGO_SEC", "caPsKJSkD2-k38lE2082");
    }

    public EftStatus getRecord(String eftNo) throws SQLException, ClassNotFoundException {
        if (con == null || con.isClosed()) init();
        try {
            ResultSet rs = con.prepareStatement("select transtatus,pstatus from acc_epayment where eftno=" + eftNo).executeQuery();
            if (rs.next()) {
                return EftStatus.builder().pstatus(rs.getInt("pstatus")).transtatus(rs.getInt("transtatus")).build();
            }
        } catch (SQLException ignored) {
        }
        return null;
    }
    public List<Long> updateSuccessEPayment() {
        List<Long> list = new ArrayList<>();
        try {
            if (con == null || con.isClosed()) init();

            ResultSet rs = con.prepareStatement("select eftno from acc_epayment where transtatus =2 and pstatus=2  and paymentdate < dateadd(minute, -30, getdate())").executeQuery();
            while (rs.next()) {
                list.add(rs.getLong("eftno"));
            }
        } catch (Exception ignored1) {
        }
        return list;
    }
}
