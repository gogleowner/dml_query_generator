package io.github.gogleowner.configuration;

import io.github.gogleowner.container.FileInfoContainable;
import io.github.gogleowner.item.CsvFileProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Job csvToDmlGenerator() {
        return jobBuilderFactory.get("csvToDmlGenerator")
                .start(csvToDmlGenerateStep())
                .build();
    }

    @Bean
    public Step csvToDmlGenerateStep() {
        return stepBuilderFactory.get("csvToDmlGenerateStep")
                .<Map<String, String>, String>chunk(100)
                .reader(csvFileReader())
                .processor(csvFileProcessor())
                .writer(dmlFileWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Map<String, String>> csvFileReader() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(fileInfoContainer.getDelimeter());
        lineTokenizer.setNames(readColumnNames());

        DefaultLineMapper<Map<String, String>> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(fieldSetMapper ->
                Arrays.stream(fieldSetMapper.getNames())
                        .collect(Collectors.toMap(
                                fieldName -> fieldName,
                                fieldName -> {
                                    String field = fieldSetMapper.readString(fieldName);
                                    return StringUtils.isNotBlank(field) ? field : "";
                                }))
        );

        lineMapper.setLineTokenizer(lineTokenizer);

        return new FlatFileItemReaderBuilder<Map<String, String>>()
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .resource(fileInfoContainer.getCsvFilePathResource())
                .build();
    }

    @Bean
    @StepScope
    public CsvFileProcessor csvFileProcessor() {
        return new CsvFileProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<String> dmlFileWriter() {
        return new FlatFileItemWriterBuilder<String>()
                .resource(fileInfoContainer.getDmlFilePathResource())
                .lineAggregator(new PassThroughLineAggregator<>())
                .lineSeparator(System.lineSeparator())
                .build();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private String[] readColumnNames() {
        String columnLines;
        try (Stream<String> columnStream = Files.lines(Paths.get(fileInfoContainer.getCsvFilePath()))) {
            columnLines = columnStream.findFirst().get();
        } catch (IOException e) {
            throw new RuntimeException("파일 오픈 중 에러 발생", e);
        }

        StringTokenizer columnLineTokenizer = new StringTokenizer(columnLines, fileInfoContainer.getDelimeter());
        String[] columnNames = new String[columnLineTokenizer.countTokens()];
        for (int idx = 0; idx < columnLineTokenizer.countTokens(); idx++) {
            columnNames[idx] = columnLineTokenizer.nextToken();
        }

        return columnNames;
    }

    @Autowired
    private FileInfoContainable fileInfoContainer;
}
