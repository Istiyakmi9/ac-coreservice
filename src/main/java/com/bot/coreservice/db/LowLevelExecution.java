package com.bot.coreservice.db;

import com.bot.coreservice.model.DbParameters;
import com.bot.coreservice.model.MasterDatabaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LowLevelExecution {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MasterDatabaseManager masterDatabaseManager;

    public <T> Map<String, Object> executeProcedure(String procedureName, List<DbParameters> sqlParams) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(getDatabaseSchemaName())
                .withProcedureName(procedureName);

        Map<String, Object> paramSet = new HashMap<>();
        for (DbParameters dbParameters : sqlParams) {
            paramSet.put(dbParameters.parameter, dbParameters.value);
            simpleJdbcCall.addDeclaredParameter(
                    new SqlParameter(
                            dbParameters.parameter,
                            dbParameters.type
                    ));
        }

        return simpleJdbcCall.execute(paramSet);
    }

    public <T> Map<String, Object> executeProcedure(String procedureName, List<DbParameters> sqlParams, boolean isOutParam) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(getDatabaseSchemaName())
                .withProcedureName(procedureName);

        Map<String, Object> paramSet = new HashMap<>();
        for (DbParameters dbParameters : sqlParams) {
            paramSet.put(dbParameters.parameter, dbParameters.value);
            simpleJdbcCall.addDeclaredParameter(
                    new SqlParameter(
                            dbParameters.parameter,
                            dbParameters.type
                    )
            );
        }

        if (isOutParam) {
            simpleJdbcCall.addDeclaredParameter(
                    new SqlOutParameter(
                            "_ProcessingResult",
                            Types.VARCHAR
                    )
            );
        }

        return simpleJdbcCall.execute(paramSet);
    }

    public String getDatabaseSchemaName() {
        String schemaName = null;
        try {
            var dbDetails = masterDatabaseManager.getUrl().split("/");
            if (dbDetails.length > 0) {
                schemaName = dbDetails[3];
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to find the database name ");
        }

        return schemaName;
    }
}
