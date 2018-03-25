package edu.umich.verdict.column;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "field"
})
@Deprecated
public class ConstantValue extends Column {

    /**
     * 
     */
    private static final long serialVersionUID = -8424039172404866346L;
    
    @JsonProperty("field")
    private Object field;

    @JsonProperty("field")
    public Object getField() {
        return field;
    }

    @JsonProperty("field")
    public void setField(Object field) {
        this.field = field;
    }
    
    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
               .appendSuper(super.hashCode())
               .append(field)
               .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ConstantValue) == false) {
            return false;
        }
        ConstantValue rhs = ((ConstantValue) other);
        return new EqualsBuilder()
               .appendSuper(super.equals(other))
               .append(field, rhs.field)
               .isEquals();
    }
}
