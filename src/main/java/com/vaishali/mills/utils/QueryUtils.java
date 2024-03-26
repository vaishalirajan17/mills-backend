package com.vaishali.mills.utils;

import com.vaishali.mills.responses.QueryWrapper;
import com.vaishali.mills.responses.mappers.PermissionsMapper;
import com.vaishali.mills.responses.users.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class QueryUtils {

    private static final Logger logger = LoggerFactory.getLogger(QueryUtils.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Executes a SQL query to retrieve a single result as a String value using the provided SQL statement.
     * If the query returns a result, it creates and returns a QueryWrapper object with the result and a status code of 1.
     * If the query does not return any result (empty result set), it returns a QueryWrapper object with an empty string result and a status code of 0.
     * If an exception occurs during the query execution, it returns a QueryWrapper object with an empty string result and a status code of -1.
     *
     * @param sql the SQL statement to execute
     * @return a QueryWrapper object containing the result and status code
     */
    public QueryWrapper queryObjectStringWrapper(String sql) {
        logger.info("Query is : {}", sql);
        try {
            return new QueryWrapper(jdbcTemplate.queryForObject(sql,String.class), 1);
        } catch(EmptyResultDataAccessException e) {
            // valid scenario
            return new QueryWrapper("", 0);
        } catch(Exception e) {
            logger.error("An error occurred while executing SQL query: {}", sql, e);
            return new QueryWrapper("", -1);
        }
    }

    /**
     * Executes a SQL query against the database and retrieves the result as a list of objects of type T.
     *
     * @param sql the SQL query to execute
     * @param mapper the RowMapper implementation to map rows of the result set to objects of type T
     * @param <T> the type of objects to be returned
     * @return a List containing objects of type T representing the result of the query, or null if an exception occurs
     */
    public <T> List<T> getDataFromDB(String sql, RowMapper<T> mapper) {
        logger.info("Query is : {}", sql);
        try {
            return jdbcTemplate.query(sql, mapper);
        } catch (Exception e) {
            logger.error("An error occurred while executing SQL query: {}", sql, e);
            return null;
        }
    }


    public int insert(String sql, String... params) {
        logger.info("Query is : {}", sql);
        try {
            sql += "(";
            for(int i=0; i < params.length; i++) {
                sql += "'" + params[i] + "'";
                if(i < params.length -1) {
                    sql += ",";
                }
            }
            sql += ")";
            logger.info("Updated Query is : {}", sql);
            jdbcTemplate.update(sql);
        } catch(Exception e) {
            logger.error("An error occurred while executing SQL query: {}", sql, e);
            return -1;
        }
        return 1;
    }


}
