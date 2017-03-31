package io.github.gogleowner.container;

/**
 * Created by seunghyolee on 2017. 3. 31..
 */
public interface FileInfoContainable {
    /**
     * @return csv 파일의 구분자
     */
    String getDelimeter();

    /**
     * @return 테이블 명(* 파일의 이름과 같아야함, .csv 확장자는 제외해야함)
     */
    String getTableName();

    /**
     * @return csv 파일의 위치
     */
    String getFileDirectoryPath();

    default String getCsvFilePath() {
        return getFileDirectoryPath() + getTableName() + ".csv";
    }

    default String getDmlFilePath() {
        return getFileDirectoryPath() + getTableName() + ".dml";
    }
}