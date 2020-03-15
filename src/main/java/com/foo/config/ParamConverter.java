package com.foo.config;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.JobParametersConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

import static com.foo.config.JobConfiguration.TIMEZONE_TEST_JOB_KT_PARAM;

public class ParamConverter implements JobParametersConverter {
    public static final String KT_STRING_IN_UTC_PARAM =  "ktStrInUtc";
    public static final String UNIQUE_ID_FOR_OVERRIDE_JOB = "UNIQUE_ID_FOR_OVERRIDE_JOB";

    @Override
    public JobParameters getJobParameters(Properties properties) {
        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put(
                UNIQUE_ID_FOR_OVERRIDE_JOB, new JobParameter(Instant.now().toEpochMilli()));
        String ktStrInUtc = (String) properties.get(KT_STRING_IN_UTC_PARAM);
        Date date = null;
        try {
            date = convertToUTCDate(ktStrInUtc);
            System.out.println("UTC DATE from input string ("+ktStrInUtc+"): "+date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unable to convert string to UTC date. "+e.getMessage());
        }
        parameterMap.put(TIMEZONE_TEST_JOB_KT_PARAM, new JobParameter(date));
        return new JobParameters(parameterMap);
    }


    private Date convertToUTCDate(String string) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        return simpleDateFormat.parse(string);
    }

    @Override
    public Properties getProperties(JobParameters params) {
        return null;
    }
}
