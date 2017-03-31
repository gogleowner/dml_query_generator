package io.github.gogleowner.configuration;

import io.github.gogleowner.container.DefaultFileInfoContainer;
import io.github.gogleowner.container.FileInfoContainable;
import io.github.gogleowner.item.CsvFileProcessor;
import io.github.gogleowner.item.CsvFileReader;
import io.github.gogleowner.item.DmlFileWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by seunghyolee on 2016. 12. 30..
 */
@Configuration
@EnableBatchProcessing
public class CsvToDmlGenerateJobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job dmlQueryGenerator() {
        return jobBuilderFactory.get("dmlQueryGenerator")
                .start(dmlQueryGenerateStep())
                .build();
    }

    @Bean
    public Step dmlQueryGenerateStep() {
        return stepBuilderFactory.get("dmlQueryGenerateStep")
                .<Map<String, String>, String>chunk(100)
                .reader(csvFileReader())
                .processor(csvFileProcessor())
                .writer(dmlFileWriter())
                .build();
    }

    @Bean
    @StepScope
    public CsvFileReader csvFileReader() {
        return new CsvFileReader();
    }

    @Bean
    @StepScope
    public CsvFileProcessor csvFileProcessor() {
        return new CsvFileProcessor();
    }

    @Bean
    @StepScope
    public DmlFileWriter dmlFileWriter() {
        return new DmlFileWriter();
    }

    @Bean
    public FileInfoContainable fileInfoContainer() {
        return new DefaultFileInfoContainer();
    }

}
