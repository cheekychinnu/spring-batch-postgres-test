package com.foo.config;

import com.foo.dao.DatasetWatermarkDao;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JobConfiguration {

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    protected JobExplorer jobExplorer;

    @Autowired
    private DatasetWatermarkDao datasetWatermarkDao;

    public static final String TIMEZONE_TEST_JOB_KT_PARAM = "kt";
    public static final String TIMEZONE_TEST_DATASET = "dummy";
    public static final String TIMEZONE_TEST_STEP_NAME = "timezoneTestStep";
    public static final String TIMEZONE_TEST_JOB_NAME= "timezoneTestJob";

    @Bean("timezoneTestJob")
    public Job timezoneTestJob() {
        return jobBuilderFactory
                        .get(TIMEZONE_TEST_JOB_NAME)
                        .start(stepBuilderFactory
                                        .get(TIMEZONE_TEST_STEP_NAME)
                                        .tasklet(
                                                (contribution, chunkContext) -> {
                                                    Date kt =
                                                            (Date)
                                                                    chunkContext
                                                                            .getStepContext()
                                                                            .getJobParameters()
                                                                            .get(TIMEZONE_TEST_JOB_KT_PARAM);
                                                    Long jobExecutionId =
                                                            chunkContext.getStepContext().getStepExecution().getJobExecutionId();
                                                    datasetWatermarkDao.updateWatermark(
                                                            TIMEZONE_TEST_JOB_NAME, TIMEZONE_TEST_DATASET, kt, jobExecutionId);
                                                    return RepeatStatus.FINISHED;
                                                })
                                        .build())
                        .build();
    }
}
