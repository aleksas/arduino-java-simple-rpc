package io.github.aleksas.arduino.simplerpc;

/**
 * Method parameter class.
 */
public class Parameter {
    public String doc;
    public String name;
    public Object fmt;
    public String tyme_name;

    /**
     * Method parameter constructor.
     * @param name Parameter name.
     * @param fmt Parameter format.
     * @param type_name Parameter type name.
     */
    public Parameter(String name, Object fmt, String type_name) {
        this.name = name;
        this.fmt = fmt;
        this.tyme_name = type_name;
    }

    @Override  
    public boolean equals(Object obj)   
    {  
        if (obj == null)   
            return false;  
        if (obj == this)  
            return true;  
        if (obj instanceof Parameter) {
            Parameter param = (Parameter) obj;
            return (doc == param.doc || doc.equals(param.doc)) && 
                (name == param.name || name.equals(param.name)) && 
                (fmt == param.fmt || fmt.equals(param.fmt)) && 
                (tyme_name == param.tyme_name || tyme_name.equals(param.tyme_name));
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = (this.doc != null ? this.doc.hashCode() : 0);
        hash = 100 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 100 * hash + (this.fmt != null ? this.fmt.hashCode() : 0);
        hash = 100 * hash + (this.tyme_name != null ? this.tyme_name.hashCode() : 0);
        return hash;
    }    
}
