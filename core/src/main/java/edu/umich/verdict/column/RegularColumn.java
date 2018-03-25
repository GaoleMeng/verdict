package edu.umich.verdict.column;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "source",
    "columnName"
})
@Deprecated
public class RegularColumn extends Column {

    /**
     * 
     */
    private static final long serialVersionUID = -6624800169997530169L;

    @NotNull
    @JsonProperty("source")
    private String source;
    
    @NotNull
    @JsonProperty("columnName")
    private String columnName;
    
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
               .appendSuper(super.hashCode())
               .append(source)
               .append(columnName)
               .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RegularColumn) == false) {
            return false;
        }
        RegularColumn rhs = ((RegularColumn) other);
        return new EqualsBuilder()
               .appendSuper(super.equals(other))
               .append(source, rhs.source)
               .append(columnName, rhs.columnName)
               .isEquals();
    }

}
