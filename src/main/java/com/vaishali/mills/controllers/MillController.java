package com.vaishali.mills.controllers;

import com.vaishali.mills.constants.QueryConstants;
import com.vaishali.mills.requests.*;
import com.vaishali.mills.responses.*;
import com.vaishali.mills.responses.mappers.*;
import com.vaishali.mills.utils.QueryUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin("*")
public class MillController {

    private static final Logger logger = LoggerFactory.getLogger(MillController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    QueryUtils queryUtils;

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

        if(millDetails.getAction().equals("RSET")) {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));

            if(millDetails.getStartTime().compareTo(Timestamp.valueOf(now)) > 0) {
                System.out.println("Date 1 occurs after Date 2");
                return new GenericResponse("Overall Date cannot be greater than current date.","","401.01");

            }
        }

        String sqlStr = "exec insert_data @millNo='"+ millDetails.getMillId()+"',@rotorNo='" + millDetails.getRotorId()+ "',@action='" +
                millDetails.getAction() + "',@comp='" + millDetails.getComponent() +"',@startTime='" + millDetails.getStartTime() +
                "',@remarks='" + millDetails.getRemarks() + "',@loginId='" + millDetails.getLoginId() + "'";

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
        String sql = "select mill_rotor_id, typeof from (select A.mill_rotor_id,A.typeof from mill_rotor1 A where A.typeOf = 'Mill' and A.mill_rotor_id not in (select distinct MillId from rotor_transactions where status = 'Running' ) union select A.mill_rotor_id,A.typeOf from mill_rotor1 A where A.typeOf = 'Rotor' and A.mill_rotor_id not in (select distinct RotorId from rotor_transactions where status = 'Running' )) as tbl order by mill_rotor_id";
    return jdbcTemplate.query(sql,new StartDetailsMapper());
    }

    @Operation(summary = "Get details for running Mills")
    @GetMapping("/running")
    public List<RunningDetails> getMillRunning(@RequestParam(name = "millid") String millid) {
        String sql = "select distinct millid , rotorid from rotor_transactions where status = 'Running' and millid='"+millid+"'";
        return jdbcTemplate.query(sql,new RunningDetailsMapper());
    }

    @Operation(summary = "Get complete details for Mills")
    @GetMapping("/resetcomponent")
    public List<ComponentDetails> getCompDetails(@RequestParam(name = "millid") String millid) {
        // TODO: sql migration
        logger.info("rotorId = {}", millid);
        String sql = "select distinct '' as transactionid,A.millid,A.rotorid,A.componentname,(select sum(tothrs) from (select case when B.status = 'Stop' then " +
                "B.runninghours else datediff(hour,B.startPeriod,getdate()) end as tothrs from rotor_transactions B where B.rotorid=A.rotorid and " +
                "B.componentname=A.componentname) as Hours) as compthours,(select distinct top 1 overallDate from rotor_transactions_history " +
                "where rotorId=A.rotorid and componentname=A.componentName order by overallDate desc) as overallDate from rotor_transactions A  " +
                "where A.status='Stop' and  (select count(*) from rotor_transactions C where C.rotorid=A.rotorid and C.componentname=A.componentname " +
                "and C.status ='Running')=0 and  A.rotorid like '" + millid + "'" ;
        logger.info("query = {}", sql);

        return jdbcTemplate.query(sql,new ComponentDetailsMapper());
    }

    @GetMapping("/mapData")
    public List<RunningDetails> getMappingData(@RequestParam(name = "millid") String millid) {
        String sql = "select distinct millid , rotorid from mill_rotor_map where status = 'Active' and " +
                "millid not in (select distinct millid from rotor_transactions where status='Running') " +
                "and rotorid not in (select distinct rotorid from rotor_transactions where status='Running') and millid='"+millid + "'";
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
        String sql = "select mill, rotor, (select sum(tothrs) from (select case when C.status = 'Stop' then C.runninghours else datediff(hour,C.startPeriod,(getdate())) end as tothrs from rotor_transactions C where C.componentname='FDP' and C.rotorId=rotor) as fdp1) as fdp, (select sum(tothrs) from (select case when C.status = 'Stop' then C.runninghours else datediff(hour,C.startPeriod,(getdate())) end as tothrs from rotor_transactions C where C.componentname='VANEPAD' and C.rotorId=rotor) as vanepad1) as vanepad,(select distinct top 1 status from rotor_transactions where rotorId=rotor and MillId=mill order by status) as status,(select distinct top 1 overallDate from rotor_transactions_history where rotorId=rotor and componentname='FDP' order by overallDate desc) as fdpdate, (select distinct top 1 overallDate from rotor_transactions_history where rotorId=rotor and componentname='VANEPAD' order by overallDate desc) as vanepadDate,(select top 1 remarks from maintenance_remarks where millid=mill and remarkType = 'Operational' order by remarkDate desc) as remarks, (select top 1 remarks from maintenance_remarks where millid=mill and remarkType = 'Maintenance' order by remarkDate desc) as boilerRemarks from (select A.mill_rotor_id as mill, coalesce((select B.rotorid from mill_rotor_map B where B.status = 'Active' and B.millid= A.mill_rotor_id),'No Rotor Attached') as rotor,'' as f,'' as f1 from mill_rotor1 A where A.typeof='Mill') as tbl";
    return jdbcTemplate.query(sql,new ViewMapper());
    }


    @GetMapping("/mills")
    public MillResponse getMills() {

        logger.info("inside getMills()");
        MillResponse millResponse = new MillResponse("","Success","");
        List<String> mills  = queryUtils.getDataFromDB(QueryConstants.GET_MILLS, (rs, rowNum) -> rs.getString("mill_rotor_id"));
        millResponse.setMills(mills);
        if(mills == null) {
            millResponse = new MillResponse("Error in fetching data.","","401.04");
        }

    return millResponse;
    }

    @GetMapping("/rotors")
    public MillResponse getRotors() {

        logger.info("inside getRotors()");
        MillResponse millResponse = new MillResponse("","Success","");
        List<String> mills  = queryUtils.getDataFromDB(QueryConstants.GET_ROTOR, (rs, rowNum) -> rs.getString("mill_rotor_id"));
        millResponse.setMills(mills);
        if(mills == null) {
            millResponse = new MillResponse("Error in fetching data.","","401.04");
        }

        return millResponse;
    }

    @PostMapping("/remarks")
    public GenericResponse addRemarks(@RequestBody RemarkRequest remarkRequest) {
        logger.info(remarkRequest.toString());

        int result = queryUtils.insert(
                QueryConstants.INSERT_REMARKS,
                remarkRequest.getRemarks(),
                remarkRequest.getRemarkLoginId(),
                remarkRequest.getMillId(),
                remarkRequest.getRemarkType() );

        if(result == -1) {
            return new GenericResponse("Error in adding remarks. Please try again.","","401.07");
        }

    return new GenericResponse("","Success","");
    }

    @Operation(summary = "Get details for editting mill details")
    @GetMapping("/editmill")
    public List getEditDetails(@RequestParam(name = "millid") String millid) {

        String query = String.format(QueryConstants.GET_EDIT_DETAILS,
                millid);

        return queryUtils.getDataFromDB(query,new EditMapper());
    }

    @Operation(summary = "Delete the latest mill transaction")
    @PostMapping("/deletemill")
    public GenericResponse editDetail(@RequestBody EditDetailsRequest editDetailsRequest) {
        logger.info(editDetailsRequest.toString());

        String sqlStr = "exec edit_data @millid='"+ editDetailsRequest.getMillId()+
                "',@rotorid='" + editDetailsRequest.getRotorId()+
                "',@loginId='" + editDetailsRequest.getLoginId()+
                "',@editDate='" + editDetailsRequest.getEditDate()+
                "',@actionPerformed='" + editDetailsRequest.getActionPerformed() +  "'";

        logger.info(sqlStr);
        jdbcTemplate.execute(sqlStr);

        return new GenericResponse("","Success","");
    }

    @Operation(summary = "Get complete details for components to edit")
    @GetMapping("/editcomponent")
    public List<ComponentDetails> editCompDetails(@RequestParam(name = "millid") String millid) {
        System.out.println(millid);
        String sql = "select  rotorid, componentName, overallDate, sum(runninghours) as compthours from rotor_transactions_history where latestflag = 'Y' " +
                "and rotorid like '" + millid + "' group by rotorid, componentName, overallDate " ;

        return jdbcTemplate.query(sql,new ComponentDetailsMapper());
    }

    @PostMapping("/deletecomponent")
    public void deleteComponent(@RequestBody ComponentResetRequest componentResetRequest) {
        System.out.println(componentResetRequest.getRotorid());

        String sqlStr = "exec comp_edit_data @rotor='"+ componentResetRequest.getRotorid()+"',@loginId='" + componentResetRequest.getLoginid() + "',@cmptName='"
                +componentResetRequest.getComptName() +"'";

        System.out.println(sqlStr);
        jdbcTemplate.execute(sqlStr);
    }


}
