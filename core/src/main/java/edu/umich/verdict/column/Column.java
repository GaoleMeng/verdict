/*
 * Copyright (c) 2018 University of Michigan
 */

package edu.umich.verdict.column;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.PROPERTY, 
    property = "type")
@JsonSubTypes({
    @Type(value = RegularColumn.class, name = "regularColumn"),
    @Type(value = DerivedColumn.class, name = "derivedColumn"),
    @Type(value = AggregateColumn.class, name = "aggregateColumn"),
    @Type(value = Aggregation.class, name = "aggregation"),
    @Type(value = ConstantValue.class, name = "constantValue"),
    })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
})
@Deprecated
public abstract class Column implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4718956466127634573L;
    
    @JsonProperty("name")
    private String name;

    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
               .appendSuper(super.hashCode())
               .append(name)
               .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Column) == false) {
            return false;
        }
        return true;
    }

}
