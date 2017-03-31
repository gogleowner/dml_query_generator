package io.github.gogleowner.item;

import io.github.gogleowner.container.FileInfoContainable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by seunghyolee on 2017. 3. 31..
 */
public class CsvFileReader extends FlatFileItemReader<Map<String, String>> {
    @Autowired
    private FileInfoContainable container;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(container.getDelimeter());
        lineTokenizer.setNames(readColumnNames());
        DefaultLineMapper lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(fs -> Arrays.stream(fs.getNames())
                .collect(Collectors.toMap(
                        fieldName -> fieldName,
                        fieldName -> StringUtils.isNotBlank(fs.readString(fieldName)) ? fs.readString(fieldName) : ""))
        );
        lineMapper.setLineTokenizer(lineTokenizer);

        setLineMapper(lineMapper);
        setLinesToSkip(1);
        setResource(new FileSystemResource(container.getCsvFilePath()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private String[] readColumnNames() {
        String columnLines;
        try (Stream<String> columnStream = Files.lines(Paths.get(container.getCsvFilePath()))) {
            columnLines = columnStream.findFirst().get();
        } catch (IOException e) {
            throw new RuntimeException("파일 오픈 중 에러 발생", e);
        }
        String[] columnNames = StringUtils.split(columnLines, container.getDelimeter());
        if (ArrayUtils.isEmpty(columnNames)) {
            throw new RuntimeException("컬럼 파싱 과정 중 에러 발생");
        }
        return columnNames;
    }
}
