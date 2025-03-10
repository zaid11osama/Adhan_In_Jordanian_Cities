package com.arabbank.hdf.uam.brain.orchestrator;

import com.arabbank.hdf.uam.brain.constants.SQL;
import com.arabbank.uam.commons.oo.dataaccess.DataAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JobDao {
    private final DataAccessService dao;

    public List<JobInfo> readActiveJobs() {
        log.debug("Reading active jobs' details");
        return dao.readRecords("select * from UAM_BRAIN_JOB where ACTIVE = cast(1 as bit)", JobInfo.class);
    }

    public void insertJobLog(String jobCode, CycleResult status, long duration, Date startTime, Date endTime, String errorMessage) {
        log.debug("Logging job result with code: {}", jobCode);
        dao.execute(SQL.File.INSERT_JOB_LOG, jobCode, status.name(), duration, startTime, endTime, errorMessage);
    }
}
