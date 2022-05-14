package io.github.aleksas.arduino.simplerpc;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Method class.
 */
public class Method {
    public String doc = "";
    public int index;
    public String name;
    public List<Parameter> parameters;
    @JsonProperty("return") 
    public Return ret;

    /**
     * Method constructor. Do not use. Only for deserialization.
     */
    public Method() {
    }

    /**
     * Method constructor.
     * @param index Method index.
     * @param name Method name.
     */
    public Method(int index, String name) {
        this.name = name;
        this.index = index;

        parameters = new ArrayList<Parameter>();
        ret = new Return();
    }

    @Override  
    public boolean equals(Object obj)   
    {  
        if (obj == null)   
            return false;  
        if (obj == this)  
            return true;  
        if (obj instanceof Method) {
            Method method = (Method) obj;

            return (doc == method.doc || doc.equals(method.doc)) && 
                index == method.index && 
                (name == method.name || name.equals(method.name)) && 
                (parameters == method.parameters || parameters.equals(method.parameters)) &&
                (ret == method.ret || ret.equals(method.ret));
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = (this.doc != null ? this.doc.hashCode() : 0);
        hash = 100 * index;
        hash = 100 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 100 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
        hash = 100 * hash + (this.ret != null ? this.ret.hashCode() : 0);
        return hash;
    }
}
