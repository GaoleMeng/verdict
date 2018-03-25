package edu.umich.verdict.column;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.EXISTING_PROPERTY, 
        property = "type",
        visible = false,
        defaultImpl=Aggregation.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fn",
    "fields",
    "name",
})
@Deprecated
public class Aggregation extends AggregateColumn {

    /**
     * 
     */
    private static final long serialVersionUID = 1631058020577720910L;
    
    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
               .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Aggregation) == false) {
            return false;
        }
        return true;
    }

}
