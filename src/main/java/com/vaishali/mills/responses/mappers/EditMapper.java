package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.EditDetailResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EditMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        EditDetailResponse editDetailResponse = new EditDetailResponse();
        editDetailResponse.setMillId(rs.getString("millid"));
        editDetailResponse.setRotorId(rs.getString("rotorid"));
        editDetailResponse.setEditDate(rs.getTimestamp("editDate"));
        editDetailResponse.setActionPerformed(rs.getString("actionPerformed"));


        return editDetailResponse;
    }
}
