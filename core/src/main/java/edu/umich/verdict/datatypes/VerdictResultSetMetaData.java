package edu.umich.verdict.datatypes;

import javax.management.Query;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import edu.umich.verdict.datatypes.QueryResult;

public class VerdictResultSetMetaData implements ResultSetMetaData {
    private QueryResult qs;
    private ResultSetMetaData rsmd;

    public VerdictResultSetMetaData(QueryResult _qs, ResultSetMetaData _rsmd) {
        qs = _qs;
        rsmd = _rsmd;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return qs.getColumnMetaData().size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isAutoIncrement(column);
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isCaseSensitive(column);
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isSearchable(column);
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isCurrency(column);
    }

    @Override
    public int isNullable(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isNullable(column);
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isSigned(column);
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getColumnDisplaySize(column);
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getColumnLabel(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getColumnName(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getSchemaName(column);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getPrecision(column);
    }

    @Override
    public int getScale(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getScale(column);
    }

    @Override
    public String getTableName(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getTableName(column);
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getCatalogName(column);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getColumnType(column);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getColumnTypeName(column);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isReadOnly(column);
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isWritable(column);
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.isDefinitelyWritable(column);
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        if (column > qs.getColumnMetaData().size()) throw new SQLException();
        return rsmd.getColumnClassName(column);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return rsmd.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return rsmd.isWrapperFor(iface);
    }
}
