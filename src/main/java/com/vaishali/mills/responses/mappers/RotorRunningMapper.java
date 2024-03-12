package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.RotorRunningDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RotorRunningMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        RotorRunningDetails rotorRunningDetails = new RotorRunningDetails();
        rotorRunningDetails.setRotor_id(rs.getString("rotorId"));
        rotorRunningDetails.setStatus(rs.getString("status"));
        return rotorRunningDetails;
    }
}
