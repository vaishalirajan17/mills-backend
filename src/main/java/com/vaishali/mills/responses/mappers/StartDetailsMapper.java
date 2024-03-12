package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.StartDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class StartDetailsMapper implements RowMapper<StartDetails> {

    @Override
    public StartDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        StartDetails startDetails = new StartDetails();
        startDetails.setMill_rotor_id(rs.getString("mill_rotor_id"));
        startDetails.setType(rs.getString("typeof"));
        return startDetails;
    }
}
