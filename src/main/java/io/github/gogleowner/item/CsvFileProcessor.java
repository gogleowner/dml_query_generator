package io.github.gogleowner.item;

import com.google.common.collect.ImmutableMap;
import io.github.gogleowner.container.FileInfoContainable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by seunghyolee on 2017. 3. 31..
 */
public class CsvFileProcessor implements ItemProcessor<Map<String, String>, String> {
    @Autowired
    private FileInfoContainable container;

    @Override
    public String process(Map<String, String> mappedData) throws Exception {
        List<String> columns = new ArrayList<>(), values = new ArrayList<>();
        mappedData.entrySet().stream().forEach(entry -> {
            columns.add(entry.getKey());
            values.add(getValueForQueryStatement(entry.getValue()));
        });
        return new StrSubstitutor(
                ImmutableMap.<String, String>builder()
                        .put("tableName", container.getTableName())
                        .put("columns", String.join(",", columns))
                        .put("values", String.join(",", values))
                        .build()
        ).replace("INSERT INTO ${tableName}(${columns}) VALUES (${values});");
    }

    private String getValueForQueryStatement(String column) {
        String value;
        if (StringUtils.isBlank(column)) {
            value = "''";
        } else if (column.matches("^[0-9.]*$")) {
            value = column;
        } else {
            value = "'" + column + "'";
        }
        return value;
    }

}
