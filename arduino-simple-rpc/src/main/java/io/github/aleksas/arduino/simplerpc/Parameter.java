package io.github.aleksas.arduino.simplerpc;

/**
 * Method parameter class.
 */
public class Parameter {
    public String doc;
    public String name;
    public Object fmt;
    public String typename;


    /**
     * Parameter constructor. Do not use. Only for deserialization.
     */
    public Parameter() {
    }

    /**
     * Method parameter constructor.
     * @param name Parameter name.
     * @param fmt Parameter format.
     * @param typename Parameter type name.
     */
    public Parameter(String name, Object fmt, String typename) {
        this.name = name;
        this.fmt = fmt;
        this.typename = typename;
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
                (typename == param.typename || typename.equals(param.typename));
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = (this.doc != null ? this.doc.hashCode() : 0);
        hash = 100 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 100 * hash + (this.fmt != null ? this.fmt.hashCode() : 0);
        hash = 100 * hash + (this.typename != null ? this.typename.hashCode() : 0);
        return hash;
    }    
}
