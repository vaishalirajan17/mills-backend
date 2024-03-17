package com.vaishali.mills.controllers;

import com.vaishali.mills.requests.MillDetails;
import com.vaishali.mills.requests.MillRotorMap;
import com.vaishali.mills.responses.*;
import com.vaishali.mills.responses.mappers.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@RestController
@CrossOrigin("*")
public class MillController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Operation(summary = "Start mill")
    @PostMapping("/millOpt")
    public GenericResponse insertData(@RequestBody MillDetails millDetails) {
        System.out.println(millDetails.toString());

        String endDateRtr = "";
        int dateResult = 1;

        QueryWrapper qw;
        //Validation while starting the mill
        if(millDetails.getAction().equals("STRT")) {
            String sqlEndDateRtr = "select top 1 endPeriod from rotor_transactions where " +
                    "rotorid='"+millDetails.getRotorId()+"' order by endPeriod desc";

            qw = queryObjectStringWrapper(sqlEndDateRtr);
            String sqlEndDate;

            if(qw.getState() == -1) {
                // error
                return new GenericResponse("Error in starting mill. Please Try again later","","401.03");
            }
            int rtrEndDate = 1;
            if(qw.getState() == 1) {
                sqlEndDate = qw.getResult();
                rtrEndDate = millDetails.getStartTime().compareTo(Timestamp.valueOf(sqlEndDate));
            }
            if(rtrEndDate <= 0) {
                return new GenericResponse("Start Date Greater than last End Date","","401.02");
            }

            String sqlEndDateMill = "select top 1 endPeriod from rotor_transactions where " +
                    "millid='"+millDetails.getMillId()+"' order by endPeriod desc";
             qw = queryObjectStringWrapper(sqlEndDateMill);
            String endDateMill = qw.getResult();

            int millEndDate = 1;
            if(qw.getState() == 1) {
                 millEndDate = millDetails.getStartTime().compareTo(Timestamp.valueOf(endDateMill));
            }
            if(millEndDate <= 0) {
                return new GenericResponse("Start Date Greater than last End Date","","401.02");
            }

            String overallDateSql = "select top 1 overalldate from rotor_transactions_history where " +
                    "rotorid='"+millDetails.getRotorId()+"' order by overalldate desc";

            qw = queryObjectStringWrapper(overallDateSql);
            String overallDate = qw.getResult();
            int overallDateResult = 1;

            if(qw.getState() == 1) {
                 overallDateResult = millDetails.getStartTime().compareTo(Timestamp.valueOf(overallDate));
            }

            if(overallDateResult <= 0) {
                return new GenericResponse("Start Date lesser than last overall Date","","401.03");
            }

        }

        //Validation while stopping the mill
        if(millDetails.getAction().equals("STOP")) {
            String startDateSql = "select distinct startperiod from rotor_transactions where status='Running' and millid='"+millDetails.getMillId()
                    +"' and rotorid='"+millDetails.getRotorId()+"'";
            int dateResultEnd = 1;
            qw = queryObjectStringWrapper(startDateSql);
            String result = qw.getResult();

            if(qw.getState() == 1) {
                 dateResultEnd = millDetails.getStartTime().compareTo(Timestamp.valueOf(result));
            }
            if(dateResultEnd <= 0) {
                return new GenericResponse("End Date should be greater than Start Date","","401.01");
            }
        }

        String sqlStr = "exec insert_data @millNo='"+ millDetails.getMillId()+"',@rotorNo='" + millDetails.getRotorId()+ "',@action='" +
                millDetails.getAction() + "',@comp='" + millDetails.getComponent() +"',@startTime='" + millDetails.getStartTime() +
                "'";
        System.out.println(sqlStr);
        jdbcTemplate.execute(sqlStr);

      return new GenericResponse("","Success","");
    }

    public QueryWrapper queryObjectStringWrapper(String sql) {
        try {
            return new QueryWrapper(jdbcTemplate.queryForObject(sql,String.class), 1);
        } catch(EmptyResultDataAccessException e) {
            // valid scenario
            return new QueryWrapper("", 0);
        } catch(Exception e) {
            return new QueryWrapper("", -1);
        }
    }

    @Operation(summary = "Get details for available Mill and Rotor")
    @GetMapping("/start")
    public List<StartDetails> getMillRotor() {
    String sql = "(select A.mill_rotor_id,A.typeof from mill_rotor1 A where A.typeOf = 'Mill' and A.mill_rotor_id " +
            "not in (select distinct MillId from rotor_transactions where status = 'Running' )) union " +
            "(select A.mill_rotor_id,A.typeOf from mill_rotor1 A where A.typeOf = 'Rotor' and A.mill_rotor_id " +
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
        // TODO: sql migration
        String sql = "select distinct A.millid,A.rotorid,A.componentname,(select sum(tothrs) from (select case when B.status = 'Stop' then " +
                "B.runninghours else datediff(hour,B.startPeriod,getdate()) end as tothrs from rotor_transactions B where B.rotorid=A.rotorid and " +
                "B.componentname=A.componentname) as Hours) as compthours,(select distinct top 1 overallDate from rotor_transactions_history " +
                "where rotorId=A.rotorid and componentname=A.componentName order by overallDate desc) as overallDate from rotor_transactions A  " +
                "where A.status='Stop' and  (select count(*) from rotor_transactions C where C.rotorid=A.rotorid and C.componentname=A.componentname " +
                "and C.status ='Running')=0 and  A.millid like '" + millid + "'" ;

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

        String sqlStr = "exec insert_mapping @millNo='"+ millRotorMap.getMillId()+"',@rotorNo='" + millRotorMap.getRotorId() + "'";
        System.out.println(sqlStr);
        jdbcTemplate.execute(sqlStr);
    }

    @GetMapping("/viewdetails")
    public List<ViewDetails> getView() {
        // TODO: sql migration
        String sql = "select '' as remarks,mill, rotor, (select sum(tothrs) from (select case when C.status = 'Stop' then C.runninghours else datediff(hour,C.startPeriod,(getdate())) end as tothrs from rotor_transactions C where C.componentname='FDP' and C.rotorId=rotor) as fdp1) as fdp, (select sum(tothrs) from (select case when C.status = 'Stop' then C.runninghours else datediff(hour,C.startPeriod,(getdate())) end as tothrs from rotor_transactions C where C.componentname='VANEPAD' and C.rotorId=rotor) as vanepad1) as vanepad,(select distinct top 1 status from rotor_transactions where rotorId=rotor and MillId=mill order by status) as status,(select distinct top 1 overallDate from rotor_transactions_history where rotorId=rotor and componentname='FDP' order by overallDate desc) as fdpdate, (select distinct top 1   overallDate from rotor_transactions_history where rotorId=rotor and componentname='VANEPAD' order by overallDate desc) as vanepadDate from (select A.mill_rotor_id as mill, coalesce((select B.rotorid from mill_rotor_map B where B.status = 'Active' and B.millid= A.mill_rotor_id),'No Mill Attached') as rotor,'' as f,'' as f1 from mill_rotor1 A where A.typeof='Mill') as tbl";
        return jdbcTemplate.query(sql,new ViewMapper());
    }


}
