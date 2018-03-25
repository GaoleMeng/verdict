package edu.umich.verdict.datatypes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "columnMetaData",
        "rows",
})
public class QueryResult {

    @JsonProperty("columnMetaData")
    private ArrayList<ColumnMetaData> columnMetaData;

    @JsonProperty("rows")
    private ArrayList<HashMap<String, Object>> rows;

    @JsonProperty("columnMetaData")
    public ArrayList<ColumnMetaData> getColumnMetaData() {
        return columnMetaData;
    }

    @JsonProperty("columnMetaData")
    public void setColumnMetaData(ArrayList<ColumnMetaData> columnMetaData) {
        this.columnMetaData = columnMetaData;
    }

    @JsonProperty("rows")
    public ArrayList<HashMap<String, Object>> getRows() {
        return rows;
    }

    @JsonProperty("rows")
    public void setRows(ArrayList<HashMap<String, Object>> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(columnMetaData)
                .append(rows)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof QueryResult) == false) {
            return false;
        }
        QueryResult rhs = ((QueryResult) other);
        return new EqualsBuilder()
                .append(columnMetaData, rhs.columnMetaData)
                .append(rows, rhs.rows)
                .isEquals();
    }
}
