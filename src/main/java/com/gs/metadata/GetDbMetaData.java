package com.gs.metadata;


import com.gs.metadata.jdbc.ColumnInfo;
import com.gs.metadata.jdbc.IndexInfo;
import com.gs.metadata.jdbc.TableId;
import com.gs.metadata.jdbc.TableInfo;

import java.sql.*;
import java.util.*;

public class GetDbMetaData {

    private DatabaseMetaData metaData;
    private Connection connection;

    public static final char SEPARATOR = '^';

    public GetDbMetaData( String url, String driverClassName, String user, String password ){

        try {
            Class.forName(driverClassName);

            connection = DriverManager.getConnection(url, user, password);

            metaData = connection.getMetaData();

            List<TableId> tableIds = getTableIds( connection );

            for( TableId tableId : tableIds ){
                TableInfo tableInfo = getTableInfo(tableId);
                System.out.println( tableInfo );
            }
        }
        catch( Exception e ){
            e.printStackTrace();
        }
    }

    private List<TableId> getTableIds(Connection connection) throws Exception{

        List<TableId> tableIds = new ArrayList<>();
        try (ResultSet tables = metaData.getTables(connection.getCatalog(), connection.getSchema(), "%", new String[]{ "TABLE" } ) ) {
            while (tables.next()) {
                tableIds.add(new TableId(tables));
            }
        }

        return tableIds;
    }

    private TableInfo getTableInfo(TableId tableId) {
        try {
            List<String> tablePrimaryKey = getTablePrimaryKey(tableId);
            //get one row result set use use - beforeFirst /first
            Map<String,String> oneRowData = new HashMap<>();
            boolean isUIDColumnExist = false;
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery(getSqlQueryByDbType( tableId.getName()))) {
                    if (rs.next()) {
                        oneRowData = getOneDataRow(rs);
                        isUIDColumnExist = getIsUIDColumnExist(rs, tablePrimaryKey);
                    }
                } catch (SQLException e) {
                    //logger.warn("Could not perform sql query on "+tableId+" SQL state: "+e.getSQLState()+" "+e.getErrorCode(),e);
                    e.printStackTrace();
                }
            }

            return new TableInfo( tableId, getTableColumns( tableId, oneRowData ), tablePrimaryKey, getTableIndexes(tableId,tablePrimaryKey) , isUIDColumnExist );
        } catch (SQLException e) {
            //logger.warn("Table "+tableId+" SQL state: "+e.getSQLState()+" "+e.getErrorCode(),e);
            e.printStackTrace();
        } catch (Exception t) {
            t.printStackTrace();
            //logger.warn("Table "+tableId+" "+t.getMessage(),t);
        }
        return null;
    }

    private Map<String,String> getOneDataRow(ResultSet rs) {

        Map<String,String> sampleDataByColumn = new HashMap<>();
        try {

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {

                sampleDataByColumn.put(rs.getMetaData().getColumnName(i+1), String.valueOf(rs.getObject(i + 1)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sampleDataByColumn;
    }

    private boolean getIsUIDColumnExist(ResultSet rs, List<String> tablePrimaryKey) {
        boolean isUIDColumnExist = false;

        try {

            for (String primaryKey : tablePrimaryKey) {
                String value = String.valueOf(rs.getObject(primaryKey));

                if (value.indexOf(SEPARATOR) != -1) {
                    isUIDColumnExist = true;
                    break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isUIDColumnExist;
    }

    private List<ColumnInfo> getTableColumns(TableId table, Map<String,String> sampleDataMap) throws SQLException {
        List<ColumnInfo> result = new ArrayList<>();
        try (ResultSet rs = metaData.getColumns(table.getCatalog(), table.getSchema(), table.getName(), "%")) {
            while (rs.next()) {

                result.add(new ColumnInfo(rs,sampleDataMap));
            }
        }
        return result;
    }

    private List<String> getTablePrimaryKey(TableId tableId) throws SQLException {
        List<String> result = new ArrayList<>();
        try (ResultSet rs = metaData.getPrimaryKeys(tableId.getCatalog(), tableId.getSchema(), tableId.getName())) {
            while (rs.next()) {
                result.add(rs.getString("COLUMN_NAME"));
            }
        }
        return result;
    }

    private String getSqlQueryByDbType (String tableName) throws SQLException {
        switch (metaData.getDatabaseProductName()) {
            case "Microsoft SQL Server":
                return "SELECT TOP 1 * FROM " + tableName;
            case "Oracle":
                return "SELECT * FROM " + tableName+" where rowNum <= 1";
            default:
                return "SELECT * FROM " + tableName + " limit 1";
        }
    }

    private List<IndexInfo> getTableIndexes(TableId table, List<String> tablePrimaryKey) throws SQLException {
        Map<String, IndexInfo> result = new LinkedHashMap<>();
        try (ResultSet rs = metaData.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, false)) {
            while (rs.next()) {
                String indexName = rs.getString("INDEX_NAME");
                if (!result.containsKey(indexName)) {
                    boolean nonUnique = rs.getBoolean("NON_UNIQUE");
                    result.put(indexName, new IndexInfo(indexName, "EQUAL", nonUnique));
                }
                List<String> columns = result.get(indexName).getColumns();
                columns.add(rs.getString("COLUMN_NAME"));
                if( columns.size() > 1){
                    result.get(indexName).setIndexType("compound");

                } else if(columns.size() == 1 && isColumnNestedInPrimaryKey(columns,tablePrimaryKey)) {
                    result.get(indexName).setIndexType("nested");

                }

                if(isColumnsArePrimaryKey(columns,tablePrimaryKey)){
                    result.get(indexName).setIndexType("regular");

                }
            }
        }
        return new ArrayList<>(result.values());
    }

    private boolean isColumnsArePrimaryKey(List<String> columns, List<String> tablePrimaryKey) {
        HashSet<String> columnsSet = new HashSet<>(columns);
        HashSet<String> primaryColumnSet = new HashSet<>(tablePrimaryKey);
        return columnsSet.equals(primaryColumnSet);
    }

    private boolean isColumnNestedInPrimaryKey(List<String> columns, List<String> tablePrimaryKey) {
        String column = columns.get(0) ;
        HashSet<String> primaryColumnSet = new HashSet<>(tablePrimaryKey);

        return primaryColumnSet.contains(column);
    }
}