package com.vaishali.mills.controllers;

import com.vaishali.mills.requests.MillDetails;
import com.vaishali.mills.requests.MillRotorMap;
import com.vaishali.mills.responses.*;
import com.vaishali.mills.responses.mappers.*;
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
    @PostMapping("/millOpt")
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
    @GetMapping("/resetcomponent")
    public List<ComponentDetails> getCompDetails(@RequestParam(name = "millid") String millid) {
        if(millid == null || millid.equals("")) {
            millid = "%%";
        }
        String sql = "select distinct A.millid,A.rotorid,A.componentname,(select sum(tothrs) from (select case when B.status = 'Stop' " +
                "then B.runninghours else TIMESTAMPDIFF(hour,B.startPeriod,(select now())) end as tothrs from rotor_transactions B" +
                " where B.rotorid=A.rotorid and B.componentname=A.componentname) as Hours) as compthours, " +
                "(select distinct overallDate from rotor_transactions_history where rotorId=A.rotorid and componentname=A.componentName " +
                "order by overallDate desc limit 1) as overallDate from rotor_transactions A  where A.status='Stop' and " +
                "(select count(*) from rotor_transactions C where C.rotorid=A.rotorid and C.componentname=A.componentname and " +
                "C.status ='Running')=0 and  A.millid like '" + millid + "'" ;

        return jdbcTemplate.query(sql,new ComponentDetailsMapper());
    }

    @GetMapping("/mapData")
    public List<RunningDetails> getMappingData() {
        String sql = "select distinct millid , rotorid from mill_rotor_map where status = 'Active' and " +
                "millid not in (select distinct millid from rotor_transactions where status='Running') " +
                "and rotorid not in (select distinct rotorid from rotor_transactions where status='Running')";
        return jdbcTemplate.query(sql,new RunningDetailsMapper());
    }

    @PostMapping("/maprotor")
    public void createMappping(@RequestBody MillRotorMap millRotorMap) {
        System.out.println(millRotorMap.toString());

        String sqlStr = "call insert_mapping('"+ millRotorMap.getMillId()+"','" + millRotorMap.getRotorId() + "')";
        System.out.println(sqlStr);
        jdbcTemplate.execute(sqlStr);
    }

    @GetMapping("/viewdetails")
    public List<ViewDetails> getView() {
        String sql = "select '' as remarks,mill, rotor, (select sum(tothrs) from (select case when C.status = 'Stop' then C.runninghours else TIMESTAMPDIFF(hour,C.startPeriod,(select now())) end as tothrs from rotor_transactions C where C.componentname='FDP' and C.rotorId=rotor) as fdp1) as fdp, (select sum(tothrs) from (select case when C.status = 'Stop' then C.runninghours else TIMESTAMPDIFF(hour,C.startPeriod,(select now())) end as tothrs from rotor_transactions C where C.componentname='VANEPAD' and C.rotorId=rotor) as vanepad1) as vanepad,(select distinct status from rotor_transactions where rotorId=rotor and MillId=mill order by status limit 1) as status,(select distinct overallDate from rotor_transactions_history where rotorId=rotor and componentname='FDP' order by overallDate desc limit 1) as fdpdate, (select distinct overallDate from rotor_transactions_history where rotorId=rotor and componentname='VANEPAD' order by overallDate desc limit 1) as vanepadDate from (select A.mill_rotor_id as mill, coalesce((select B.rotorid from mill_rotor_map B where B.status = 'Active' and B.millid= A.mill_rotor_id),'No Mill Attached') as rotor,'' as f,'' as f1 from mill_rotor A where A.typeof='Mill') as tbl";

        return jdbcTemplate.query(sql,new ViewMapper());
    }

}
