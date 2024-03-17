package com.vaishali.mills.constants;

public class QueryConstants {


    public static String GET_ACTIVE_USER_BY_USERNAME_AND_PASSWORD = "select top 1 username from users where username = '%s' COLLATE Latin1_General_CS_AS and password = '%s' COLLATE Latin1_General_CS_AS and is_active = 'Y';";

    public static String GET_PERMISSIONS_BY_ROLL_ID = "select p.screen_id, s.screen_name, p.permission_type from permissions p " +
            "join screens s on s.screen_id = p.screen_id " +
            "where role_id = '%s';";

}
