package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.ViewDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewMapper implements RowMapper<ViewDetails> {
    @Override
    public ViewDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

        ViewDetails viewDetails = new ViewDetails();
        viewDetails.setMillId(rs.getString("mill"));
        viewDetails.setRotorId(rs.getString("rotor"));
        viewDetails.setFdpHours(rs.getInt("fdp"));
        viewDetails.setFdpDate(rs.getTimestamp("fdpdate"));
        viewDetails.setVanePadHours(rs.getInt("vanepad"));
        viewDetails.setVanePadDate(rs.getTimestamp("vanepadDate"));
        viewDetails.setStatus(rs.getString("status"));
        viewDetails.setRemarks(rs.getString("remarks"));
        viewDetails.setBoilerRemarks(rs.getString("boilerRemarks"));

        return viewDetails;
    }
}
