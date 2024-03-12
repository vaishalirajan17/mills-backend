package com.vaishali.mills.controllers;

import com.vaishali.mills.requests.MillDetails;
import com.vaishali.mills.responses.*;
import com.vaishali.mills.responses.mappers.ComponentDetailsMapper;
import com.vaishali.mills.responses.mappers.RotorRunningMapper;
import com.vaishali.mills.responses.mappers.RunningDetailsMapper;
import com.vaishali.mills.responses.mappers.StartDetailsMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin("*")
public class MillController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Operation(summary = "Start mill")
    @PostMapping("/mill")
    public void insertData(@RequestBody MillDetails millDetails) {
        System.out.println(millDetails.toString());

        String sqlStr = "call insert_data('"+ millDetails.getMillId()+"','" + millDetails.getRotorId()+ "','" +
                millDetails.getAction() + "','" + millDetails.getComponent() +"','" + millDetails.getStartTime() +
                "')";
        System.out.println(sqlStr);
        jdbcTemplate.execute(sqlStr);
    }

    @Operation(summary = "Get details for available Mill and Rotor")
    @GetMapping("/start")
    public List<StartDetails> getMillRotor() {
    String sql = "(select A.mill_rotor_id,A.typeof from mill_rotor A where A.typeOf = 'Mill' and A.mill_rotor_id " +
            "not in (select distinct MillId from rotor_transactions where status = 'Running' )) union " +
            "(select A.mill_rotor_id,A.typeOf from mill_rotor A where A.typeOf = 'Rotor' and A.mill_rotor_id " +
            "   not in (select distinct RotorId from rotor_transactions where status = 'Running' ))";
    return jdbcTemplate.query(sql,new StartDetailsMapper());
    }

    @Operation(summary = "Get details for running Mills")
    @GetMapping("/running")
    public List<RunningDetails> getMillRunning() {
        String sql = "select distinct millid , rotorid from rotor_transactions where status = 'Running'";
        return jdbcTemplate.query(sql,new RunningDetailsMapper());
    }

    @Operation(summary = "Get complete details for Mills")
    @GetMapping("/components")
    public WrapperDetails getCompDetails() {
        String sql = "select (select distinct A.millid from rotor_transactions A where  A.RotorId=rotor limit 1) as mill," +
                "rotor, cmptName,sum(tothrs) as hrs,case when sum(tothrs) >= (select lifePeriod from component_lifecycle " +
                "where componentname = cmptName) then false else true end as pass from ( select RotorId as rotor, componentName as cmptName, " +
                "case when status = 'Stop' then runninghours else round(hour(timediff((select now()),startPeriod)) * 60 " +
                "+ minute(timediff((select now()),startPeriod)) + second(timediff((select now()),startPeriod))/60)  end as " +
                "tothrs from rotor_transactions) as tbl_rotor GROUP by rotor, cmptName  ";

        String sql1 = "select distinct status,RotorId from rotor_transactions";

        return new WrapperDetails(jdbcTemplate.query(sql,new ComponentDetailsMapper()),jdbcTemplate.query(sql1,new RotorRunningMapper()));
    }

    @GetMapping("/view")
    public void getView() {
        String sql = "";
    }



}
