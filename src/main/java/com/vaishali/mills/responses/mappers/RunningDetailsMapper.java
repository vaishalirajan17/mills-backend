package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.RunningDetails;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RunningDetailsMapper implements RowMapper<RunningDetails> {

    @Override
    public RunningDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        RunningDetails runningDetails = new RunningDetails();
        runningDetails.setMillId(rs.getString("MillId"));
        runningDetails.setRotorId(rs.getString("RotorId"));
        return runningDetails;
    }
}
