package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.ComponentDetails;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ComponentDetailsMapper implements RowMapper<ComponentDetails> {
    @Override
    public ComponentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        ComponentDetails componentDetails = new ComponentDetails();
        componentDetails.setMillId(rs.getString("millid"));
        componentDetails.setRotorId(rs.getString("rotorid"));
        componentDetails.setComponentName(rs.getString("componentname"));
        componentDetails.setRunningHours(rs.getInt("compthours"));
        componentDetails.setOverAllDate(rs.getTimestamp("overallDate"));
        return componentDetails;
    }
}
