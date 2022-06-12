package com.gs.metadata.jdbc;

import java.util.List;


public class TableInfo {
    private final TableId id;
    private final List<ColumnInfo> columns;
    private final List<String> primaryKey;
    private final List<IndexInfo> indexes;
    private final boolean isUIDColumnExist;

    public TableInfo(TableId id, List<ColumnInfo> columns, List<String> primaryKey, List<IndexInfo> indexes, boolean isUIDColumnExist) {
        this.id = id;
        this.columns = columns;
        this.primaryKey = primaryKey;
        this.indexes = indexes;
        this.isUIDColumnExist = isUIDColumnExist;
    }

    public TableId getId() {
        return id;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public List<String> getPrimaryKey() {
        return primaryKey;
    }

    public List<IndexInfo> getIndexes() {
        return indexes;
    }

    public boolean isUIDColumnExist() {
        return isUIDColumnExist;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "id=" + id +
                ", columns=" + columns +
                ", primaryKey=" + primaryKey +
                ", indexes=" + indexes +
                ", isUIDColumnExist=" + isUIDColumnExist +
                '}';
    }
}
