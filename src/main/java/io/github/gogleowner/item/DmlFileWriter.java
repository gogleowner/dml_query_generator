package io.github.gogleowner.item;

import io.github.gogleowner.container.FileInfoContainable;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import javax.annotation.PostConstruct;

/**
 * Created by seunghyolee on 2017. 3. 31..
 */
public class DmlFileWriter extends FlatFileItemWriter<String> {
    @Autowired
    private FileInfoContainable container;

    @PostConstruct
    public void init() {
        setResource(new FileSystemResource(container.getDmlFilePath()));
        setLineAggregator(new PassThroughLineAggregator<>());
        setLineSeparator("\n");
    }

}
