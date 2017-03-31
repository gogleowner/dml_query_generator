package io.github.gogleowner;

import io.github.gogleowner.configuration.CsvToDmlGenerateJobConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by seunghyolee on 2017. 3. 31..
 */
@ContextConfiguration(classes = CsvToDmlGenerateJobConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class CsvToDmlGenerateApplicationTest {
    @Autowired
    private Job dmlQueryGenerator;

    @Autowired
    private JobLauncher jobLauncher;

    @Test
    public void jobLaunchTest() throws Exception {
        JobExecution jobExecution = jobLauncher.run(dmlQueryGenerator, new JobParameters());
        assertThat(jobExecution.getExitStatus(), is(equalTo(ExitStatus.COMPLETED)));
    }

}