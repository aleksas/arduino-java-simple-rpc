package io.github.aleksas.arduino.simplerpc;

/**
 * Method return class.
 */
public class Return {
    public String doc;
    public Object fmt;
    public String typename = "";

    @Override  
    public boolean equals(Object obj)   
    {  
        if (obj == null)   
            return false;  
        if (obj == this)  
            return true;  
        if (obj instanceof Return) {
            Return ret = (Return) obj;
            return (doc == ret.doc || doc.equals(ret.doc)) &&
                (fmt == ret.fmt || fmt.equals(ret.fmt)) &&
                (typename == ret.typename || typename.equals(ret.typename));
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = (this.doc != null ? this.doc.hashCode() : 0);
        hash = 100 * hash + (this.fmt != null ? this.fmt.hashCode() : 0);
        hash = 100 * hash + (this.typename != null ? this.typename.hashCode() : 0);
        return hash;
    }
}
