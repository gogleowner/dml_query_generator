package io.github.gogleowner.container;

/**
 * Created by seunghyolee on 2017. 3. 31..
 */
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
