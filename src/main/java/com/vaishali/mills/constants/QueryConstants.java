package com.vaishali.mills.constants;

public class QueryConstants {


    public static String GET_ACTIVE_USER_BY_USERNAME_AND_PASSWORD = "select top 1 username from users where username = '%s' COLLATE Latin1_General_CS_AS and password = '%s' COLLATE Latin1_General_CS_AS and is_active = 'Y';";

    public static String GET_PERMISSIONS_BY_USERNAME = "select p.screen_id, s.screen_name, p.permission_type from permissions p " +
            "join screens s on s.screen_id = p.screen_id " +
            "where role_id = (select role_id from users where username='%s');";

    public static String GET_MILLS = "select mill_rotor_id from mill_rotor1 where typeof='Mill'";

    public static String GET_USERNAME = "select username from users where username='%s'";

    public static String INSERT_NEW_USER = "insert into users (username, password, role_id, is_active) values ";

    public static  String INSERT_REMARKS = "insert into maintenance_remarks (remarks, remarkLoginId, millid, remarkType) values ";

    public static String GET_EDIT_DETAILS="select distinct 'Start' as actionPerformed,startPeriod as editDate, millid, rotorid " +
            "from rotor_transactions where datediff(day,CONVERT(date, startEntryDate), " +
            "CONVERT(date, getdate())) <= 2 and datediff(day,CONVERT(date, startEntryDate), CONVERT(date, getdate()))  >= 0 " +
            "union select distinct 'Stop' as action,endPeriod as editDate, millid, rotorid  from rotor_transactions " +
            "where datediff(day,CONVERT(date, stopEntryDate), CONVERT(date, getdate())) <= 2 and " +
            "datediff(day,CONVERT(date, stopEntryDate), CONVERT(date, getdate()))  >= 0";

}
