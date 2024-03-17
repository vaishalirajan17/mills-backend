package com.vaishali.mills.responses.mappers;

import com.vaishali.mills.responses.users.Permission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionsMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Permission permission = new Permission();
        permission.setScreenId(rs.getString("screen_id"));
        permission.setScreenName(rs.getString("screen_name"));
        permission.setPermissionType(rs.getString("permission_type"));
        return permission;
    }
}
