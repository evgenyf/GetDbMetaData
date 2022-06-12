package com.gs.metadata.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TableId {
    private final String name;
    private final String schema;
    private final String catalog;
    private final String type;

    public TableId(ResultSet rs) throws SQLException {
        this.name = rs.getString("TABLE_NAME");
        this.schema = rs.getString("TABLE_SCHEM");
        this.catalog = rs.getString("TABLE_CAT");
        this.type = rs.getString("TABLE_TYPE");
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TableId{" +
                "name='" + name + '\'' +
                ", schema='" + schema + '\'' +
                ", catalog='" + catalog + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
