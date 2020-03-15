package com.foo;

import com.foo.dao.DatasetWatermarkDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import static com.foo.config.JobConfiguration.TIMEZONE_TEST_DATASET;
import static com.foo.config.JobConfiguration.TIMEZONE_TEST_JOB_KT_PARAM;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(
        classes = {FooApplication.class, TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TimezoneTest {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("EST5EDT"));
    }

    private static final long TIMEOUT_IN_SECONDS = 60L;
    @Autowired
    @Qualifier("timezoneTestJob")
    private Job timezoneTestJob;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private DatasetWatermarkDao datasetWatermarkDao;

    @BeforeEach
    public void setup() {
        jobLauncherTestUtils.setJob(timezoneTestJob);
    }

    @Test
    public void test() throws ParseException {
        /*
        UTC is 5 hrs ahead of EST and 4 hrs ahead of EDT
        For 2020,
         March 8 2020 02:00 we set clock one hr forward
         November 1 2020 02:00 we set clock one hr backward i.e. 1:00 to 2:00 will appear twice
        */

        assertKt(convertToUTCDate("20200308 06:05:00")); // is 20200308 01:05:00 EST
        assertKt(convertToUTCDate("20200308 07:05:00")); // is 20200308 03:05:00 EDT
        assertKt(convertToUTCDate("20201101 05:05:00")); // is 20201101 01:05:00 EDT
        assertKt(convertToUTCDate("20201101 06:05:00")); // is 20201101 01:05:00 EST
    }

    private Date convertToUTCDate(String string) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        return simpleDateFormat.parse(string);
    }

    private void assertKt(Date kt) {
        Assertions.assertTimeoutPreemptively(
                Duration.ofSeconds(TIMEOUT_IN_SECONDS)
                , () -> {
                    System.out.println("********checking for kt: " + kt);

                    JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                    jobParametersBuilder.addDate(TIMEZONE_TEST_JOB_KT_PARAM, kt);
                    jobParametersBuilder.addLong("unique", Instant.now().getEpochSecond());
                    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());


                    jobExecution = jobExplorer.getJobExecution(jobExecution.getId());
                    while (!jobExecution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
                        Thread.sleep(100);
                        jobExecution = jobExplorer.getJobExecution(jobExecution.getId());
                    }

                    // watermark dao stores as Timestamp
                    Date watermark = datasetWatermarkDao.getWatermark(timezoneTestJob.getName(), TIMEZONE_TEST_DATASET);
                    System.out.println("Watermark: "+watermark);
                    assertEquals(kt.getTime(),watermark.getTime());

                    JobParameter jobParameter = jobExecution.getJobParameters().getParameters().get(TIMEZONE_TEST_JOB_KT_PARAM);
                    // reading back from the table should also give correct results.
                    Date ktReadBackFromDB = (Date)jobParameter.getValue();
                    System.out.println(ktReadBackFromDB);
                    assertEquals(kt.getTime(), ktReadBackFromDB.getTime());
                });
    }
}
