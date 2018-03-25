/*
 * Copyright (c) 2018 University of Michigan
 */

package edu.umich.verdict.column;

import java.util.ArrayList;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fn",
    "fields",
    "name",
})
@Deprecated
public class DerivedColumn extends Column {

    /**
     * 
     */
    private static final long serialVersionUID = 1341217597968487296L;

    @JsonProperty("fn")
    private String fn;
    
    @JsonProperty("fields")
    private ArrayList<Column> fields;

    @JsonProperty("fn")
    public String getFn() {
        return fn;
    }

    @JsonProperty("fn")
    public void setFn(String fn) {
        this.fn = fn;
    }

    @JsonProperty("fields")
    public ArrayList<Column> getFields() {
        return fields;
    }

    @JsonProperty("fields")
    public void setFields(ArrayList<Column> fields) {
        this.fields = fields;
    }
    
    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
               .appendSuper(super.hashCode())
               .append(fn)
               .append(fields)
               .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DerivedColumn) == false) {
            return false;
        }
        DerivedColumn rhs = ((DerivedColumn) other);
        return new EqualsBuilder()
               .appendSuper(super.equals(other))
               .append(fn, rhs.fn)
               .append(fields, rhs.fields)
               .isEquals();
    }
}
