package com.gs.metadata.jdbc;

import java.util.ArrayList;
import java.util.List;


public class IndexInfo {
    private final String name;
    private String orderType;
    private String indexType;
    private final List<String> columns = new ArrayList<>();
    private final boolean nonUnique;

    public IndexInfo(String name, String orderType, boolean nonUnique) {
        this.name = name;
        this.orderType = orderType;
        this.nonUnique = nonUnique;
        indexType="regular";
    }

    public IndexInfo(String name, String orderType, String indexType, boolean nonUnique) {
        this.name = name;
        this.orderType = orderType;
        this.indexType = indexType;
        this.nonUnique = nonUnique;
    }

    public String getName() {
        return name;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public List<String> getColumns() {
        return columns;
    }

    public boolean isNonUnique() {
        return nonUnique;
    }
}
