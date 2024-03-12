package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.ComponentDetails;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ComponentDetailsMapper implements RowMapper<ComponentDetails> {
    @Override
    public ComponentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        ComponentDetails componentDetails = new ComponentDetails();
        componentDetails.setMillId(rs.getString("mill"));
        componentDetails.setRotorId(rs.getString("rotor"));
        componentDetails.setComponentName(rs.getString("cmptName"));
        componentDetails.setRunningHours(rs.getInt("hrs"));
        componentDetails.setPassed(rs.getBoolean("pass"));
        return componentDetails;
    }
}
