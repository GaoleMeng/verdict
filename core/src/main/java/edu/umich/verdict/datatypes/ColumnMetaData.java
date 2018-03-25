package edu.umich.verdict.datatypes;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "columnName",
        "columnType",
        "aggregateType",
})
public class ColumnMetaData {

    @NotNull
    @JsonProperty("columnName")
    private String columnName;

    @NotNull
    @JsonProperty("columnType")
    private String columnType;

    @JsonProperty("aggregateType")
    private String aggregateType;    // one of "sum", "avg", "count"

    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonProperty("columnType")
    public String getColumnType() {
        return columnType;
    }

    @JsonProperty("columnType")
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    @JsonProperty("aggregateType")
    public String getAggregateType() {
        return aggregateType;
    }

    @JsonProperty("aggregateType")
    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(columnName)
                .append(columnType)
                .append(aggregateType)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ColumnMetaData) == false) {
            return false;
        }
        ColumnMetaData rhs = ((ColumnMetaData) other);
        return new EqualsBuilder()
                .append(columnName, rhs.columnName)
                .append(columnType, rhs.columnType)
                .append(aggregateType, rhs.aggregateType)
                .isEquals();
    }

}
