package io.github.gogleowner.container;

import org.springframework.stereotype.Component;

/**
 * Created by seunghyolee on 2017. 3. 31..
 */
@Component
public class DefaultFileInfoContainer implements FileInfoContainable {
    @Override
    public String getDelimeter() {
        return ",";
    }

    @Override
    public String getTableName() {
        return "sample";
    }

    @Override
    public String getFileDirectoryPath() {
        return "sample_data/";
    }
}
