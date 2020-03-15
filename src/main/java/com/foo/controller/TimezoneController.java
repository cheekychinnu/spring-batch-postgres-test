package com.foo.controller;

import com.foo.dao.DatasetWatermarkDao;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.foo.config.JobConfiguration.TIMEZONE_TEST_DATASET;
import static com.foo.config.JobConfiguration.TIMEZONE_TEST_JOB_NAME;
import static com.foo.config.ParamConverter.KT_STRING_IN_UTC_PARAM;

@RestController
@RequestMapping("/tz")
public class TimezoneController {
    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private DatasetWatermarkDao datasetWatermarkDao;

    @RequestMapping(value = "/launch", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long launch(@RequestParam("ktStringInUtc") String kt)
            throws JobInstanceAlreadyExistsException, JobParametersInvalidException, NoSuchJobException {
        return jobOperator.start(TIMEZONE_TEST_JOB_NAME, KT_STRING_IN_UTC_PARAM+"="+kt);
    }

    @RequestMapping(value = "/watermark", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Date getWatermark() {
        Date watermark = datasetWatermarkDao.getWatermark(TIMEZONE_TEST_JOB_NAME, TIMEZONE_TEST_DATASET);
        System.out.println(watermark);
        return watermark;
    }

}
