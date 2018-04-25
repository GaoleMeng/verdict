package edu.umich.verdict.datatypes;

import edu.umich.verdict.column.Column;
import edu.umich.verdict.datatypes.QueryResult;
import edu.umich.verdict.datatypes.ColumnMetaData;
import edu.umich.verdict.parser.VerdictSQLParser;


import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class VerdictResultSet implements ResultSet {
    private QueryResult qr;
    private ResultSetMetaData rsmd;
    private VerdictResultSetMetaData verdictRsmd;
    private int curPosition;
    private boolean closeFlag;

    public VerdictResultSet(ResultSet rs) throws SQLException {
        try {
            qr = new QueryResult();
            rsmd = rs.getMetaData();
            int columnNum = rsmd.getColumnCount();
            ArrayList<ColumnMetaData> cmdList = new ArrayList<>();
            ArrayList<HashMap<String, Object>> rows = new ArrayList<>();

            // iterate through all the column meta data
            for (int i = 1; i <= columnNum; i++) {
                ColumnMetaData cmd = new ColumnMetaData();
                String columnType = rsmd.getColumnTypeName(i);
                String columnName = rsmd.getColumnName(i);
                cmd.setColumnName(columnName);
                cmd.setColumnType(columnType);
                cmdList.add(cmd);
            }

            while (rs.next()) {
                HashMap tmp = new HashMap<String, Object>();
                for (int i = 1; i <= columnNum; i++) {
                    String columnName = cmdList.get(i - 1).getColumnName();
                    Object content = rs.getObject(i);
                    tmp.put(columnName, content);
                }
                rows.add(tmp);
            }
            qr.setColumnMetaData(cmdList);
            qr.setRows(rows);
            this.curPosition = -1;
            closeFlag = false;
            verdictRsmd = new VerdictResultSetMetaData(qr, rsmd);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public VerdictResultSet(QueryResult re) {
        this.qr = re;
        this.curPosition = -1;
    }


    public QueryResult getQueryResult() {
        return qr;
    }

    public void deleteColumn(String columnLabel) throws SQLException {
        if (closeFlag) throw new SQLException();
        try {
            ArrayList<HashMap<String, Object>> rows = qr.getRows();
            if (rows.size() == 0) {
                throw new SQLException();
            }
            int columnNums = qr.getColumnMetaData().size();
            for (int i = 0; i < rows.size(); i++) {
                if (!rows.get(i).containsKey(columnLabel)) {
                    throw new SQLException();
                }
                rows.get(i).remove(columnLabel);
            }
            ArrayList<ColumnMetaData> metaData = qr.getColumnMetaData();
            for (int i = 0; i < columnNums; i++) {
                if (metaData.get(i).getColumnName().equals(columnLabel)) {
                    metaData.remove(i);
                    break;
                }
            }
            qr.setRows(rows);
            qr.setColumnMetaData(metaData);
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    // check the group count column, if the group count is smaller than threshold
    // we change the error bound to inf,
    public boolean checkAndRevise(int threshold, float trust_bound) throws SQLException {
        if (closeFlag) throw new SQLException();
        boolean res = false;

        ArrayList<ColumnMetaData> metaData = qr.getColumnMetaData();
        int columnNums = metaData.size();
        boolean check_exists = false;
        int which = 0;
        for (int i = 0; i < columnNums; i++) {
            if (metaData.get(i).getColumnName().equals("VERDICT_GROUP_COUNT")) {
                check_exists = true;
                which = 0;
                break;
            }
            else if (metaData.get(i).getColumnName().equals("verdict_group_count")) {
                which = 1;
                check_exists = true;
            }

        }

        if (!check_exists) {
            throw new SQLException();
        }

        // group count column exists, check every row of the verdictResultSet
        ArrayList<HashMap<String, Object>> rows = qr.getRows();
        String true_string_group_count = "";
        if (which == 1) {
            true_string_group_count = "verdict_group_count";
        }
        else {
            true_string_group_count = "VERDICT_GROUP_COUNT";
        }


        for (int i = 1; i < columnNums; i++) {
            if (metaData.get(i).getColumnName().endsWith("_err") &&
                    metaData.get(i - 1).getColumnName().concat("_err").equals(metaData.get(i).getColumnName())
                    || metaData.get(i).getColumnName().endsWith("_ERR") &&
                    metaData.get(i - 1).getColumnName().concat("_ERR").equals(metaData.get(i).getColumnName())) {

                for (int j = 0; j < rows.size(); j++) {
//                    Long tmp = (Long) rows.get(j).get("_verdict_group_count");
                    if (threshold >= (Long) rows.get(j).get("_verdict_group_count")) {
                        rows.get(j).put(metaData.get(i).getColumnName(), new Long(-1));
                        res = true;
                    }
                    else if (Float.parseFloat(rows.get(j).get(metaData.get(i).getColumnName()).toString()) >
                            trust_bound *
                                    Float.parseFloat(rows.get(j).get(metaData.get(i - 1).getColumnName()).toString())) {
                        rows.get(j).put(metaData.get(i).getColumnName(), new Long(-1));
                        res = true;
                    }
                }
            }
        }
        qr.setRows(rows);
        qr.setColumnMetaData(metaData);
        return res;
    }

    @Override
    public boolean next() throws SQLException {
        if (closeFlag) throw new SQLException();
        if (curPosition == qr.getRows().size() - 1) return false;
        curPosition++;
        return true;
    }

    // need discussion about what it can do:
    @Override
    public void close() throws SQLException{
        if (closeFlag) throw new SQLException();
        qr = null;
        rsmd = null;
        closeFlag = true;
    }

    @Override
    public boolean first() throws SQLException {
        if (closeFlag) throw new SQLException();
        try {
            if (qr.getRows().size() == 0) return false;
            curPosition = 0;
            return true;
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        if (closeFlag) throw new SQLException();
        try {
            String key = qr.getColumnMetaData().get(columnIndex - 1).getColumnName();
            Object result = qr.getRows().get(curPosition).get(key);
            return (Boolean) result;
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        if (closeFlag) throw new SQLException();
        try {
            Object result = qr.getRows().get(curPosition).get(columnLabel);
            return (Boolean) result;
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public java.sql.Date getDate(int columnIndex) throws SQLException {
        return getObject(columnIndex, java.sql.Date.class);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public java.sql.Date getDate(String columnLabel) throws SQLException {
        return getObject(columnLabel, java.sql.Date.class);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public String getCursorName() throws SQLException {
        return null;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return Double.parseDouble(getObject(columnIndex, String.class));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return null;
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return Double.parseDouble(getObject(columnLabel, String.class));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return null;
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return new byte[0];
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return Float.parseFloat(getObject(columnIndex, String.class));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return Float.parseFloat(getObject(columnLabel, String.class));
    }


    @Override
    public int getInt(int columnIndex) throws SQLException {
        return (int) Float.parseFloat(getObject(columnIndex, String.class));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return (int) Float.parseFloat(getObject(columnLabel, String.class));
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
//        System.out.println(getObject(columnIndex, String.class));
        return (long) Float.parseFloat(getObject(columnIndex, String.class));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return (long) Float.parseFloat(getObject(columnLabel, String.class));
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return (short) Float.parseFloat(getObject(columnIndex, String.class));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return (short) Float.parseFloat(getObject(columnLabel, String.class));
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return verdictRsmd;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        if (closeFlag) throw new SQLException();
        try {
            String key = qr.getColumnMetaData().get(columnIndex - 1).getColumnName();
            Object result = qr.getRows().get(curPosition).get(key);
            return result;
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return 0;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return false;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return false;
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        if (closeFlag) throw new SQLException();
        if (type == null) throw new SQLException();
        if (curPosition == -1) throw new SQLException();

        String key = qr.getColumnMetaData().get(columnIndex - 1).getColumnName();
        Object result = qr.getRows().get(curPosition).get(key);

        try {
            return (T) result.toString();
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        if (closeFlag) throw new SQLException();
        if (type == null) throw new SQLException();
        if (curPosition == -1) throw new SQLException();

        Object result = qr.getRows().get(curPosition).get(columnLabel);

        T tmp;
        try {
            tmp = (T)result;
        }
        catch (Exception e) {
            throw new SQLException();
        }
        return tmp;
    }

    @Override
    public int getRow() throws SQLException {
        if (closeFlag) throw new SQLException();
        return curPosition + 1;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return false;
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return false;
    }

    @Override
    public Statement getStatement() throws SQLException {
        if (closeFlag) throw new SQLException();
        return null;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return null;
    }


    @Override
    public String getString(int columnIndex) throws SQLException {
//        return String.valueOf(getObject(columnIndex, String.class));
        return String.valueOf(getObject(columnIndex, String.class));
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return String.valueOf(getObject(columnLabel, String.class));
    }

    @Override
    public java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getObject(columnIndex, java.sql.Time.class);
    }

    @Override
    public java.sql.Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getObject(columnLabel, java.sql.Time.class);
    }

    @Override
    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal)
            throws SQLException {
        return getObject(columnIndex, java.sql.Timestamp.class);
    }

    public java.sql.Timestamp getTimestamp(String columnLabel, Calendar cal)
            throws SQLException {
        return getObject(columnLabel, java.sql.Timestamp.class);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {

    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {

    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {

    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {

    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {

    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    // unclear what to gives out
    @Override
    public int getType() throws SQLException {
        return 2;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {

    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {

    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {

    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {

    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {

    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {

    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {

    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {

    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {

    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {

    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {

    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {

    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {

    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {

    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {

    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {

    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {

    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {

    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {

    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {

    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {

    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {

    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {

    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {

    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {

    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {

    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {

    }

    @Override
    public void insertRow() throws SQLException {

    }

    @Override
    public void updateRow() throws SQLException {

    }

    @Override
    public void deleteRow() throws SQLException {

    }

    @Override
    public void refreshRow() throws SQLException {

    }

    @Override
    public void cancelRowUpdates() throws SQLException {

    }

    @Override
    public void moveToInsertRow() throws SQLException {

    }

    @Override
    public void moveToCurrentRow() throws SQLException {

    }

    @Override
    public boolean isClosed() throws SQLException {
        return closeFlag;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {

    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {

    }


    @Override
    public boolean isFirst() throws SQLException {
        if (closeFlag) throw new SQLException();
        return curPosition == 0;
    }

    @Override
    public boolean isLast() throws SQLException {
        if (closeFlag) throw new SQLException();
        return curPosition == qr.getRows().size() - 1;
    }

    @Override
    public void beforeFirst() throws SQLException {

    }

    @Override
    public void afterLast() throws SQLException {

    }

    @Override
    public boolean last() throws SQLException {
        if (closeFlag) throw new SQLException();
        try {
            if (qr.getRows().size() == 0) return false;
            curPosition = qr.getRows().size() - 1;
            return true;
        }
        catch (Exception e) {
            throw new SQLException();
        }
    }

    public boolean previous() throws SQLException {
        if (closeFlag) throw new SQLException();
        if (curPosition == -1) return false;
        if (curPosition == 0) return false;
        curPosition--;
        return true;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    // not sure what it's doing
    public boolean wasNull() throws SQLException {
        return true;
    }

    public byte getByte(int columnIndex) throws SQLException {
        return getObject(columnIndex, byte.class);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        return getObject(columnIndex, byte[].class);
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        // deprecated in verdictResultSet
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        // deprecated in verdictResultSet
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        // deprecated in verdictResultSet
    }

    public void updateNClob(String columnLabel,  Reader reader, long length) throws SQLException {
        // deprecated in verdictResultSet
    }

    public void updateNClob(int columnIndex,  Reader reader) throws SQLException {
        // deprecated in verdictResultSet
    }

    public void updateNClob(String columnLabel,  Reader reader) throws SQLException {
        // deprecated in verdictResultSet
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        // deprecated in verdictResultSet
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return null;
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

    }


    public void updateNCharacterStream(int columnIndex,
                                       java.io.Reader x) throws SQLException {
        // deprecated in verdictResultSet
    }

    public void updateNCharacterStream(String columnLabel,
                                       java.io.Reader reader) throws SQLException {
        // deprecated in verdictResultSet
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {

    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {

    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
