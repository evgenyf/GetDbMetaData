package com.gs.metadata.jdbc;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ColumnInfo {
    private final String name;
    private final JDBCType type;
    private final String sampleValue;

    public ColumnInfo(ResultSet rs, Map<String, String> sampleData) throws SQLException {
        this.name = rs.getString("COLUMN_NAME");
        this.type = JDBCType.valueOf(rs.getInt("DATA_TYPE"));
        this.sampleValue = sampleData.get(name);
    }

    public String getName() {
        return name;
    }

    public JDBCType getType() {
        return type;
    }

    public String getSampleValue() {
        return sampleValue;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", sampleValue='" + sampleValue + '\'' +
                '}';
    }
}
