package edu.umich.verdict.column;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fn",
    "fields",
    "name"
})
public class AggregateColumn extends Column {

    /**
     * 
     */
    private static final long serialVersionUID = -3485612833403713147L;
    
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
}
