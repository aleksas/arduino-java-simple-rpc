package com.simplerpc;

/**
 * Method return class.
 */
public class Return {
    public String doc;
    public Object fmt;
    public String tyme_name = "";

    @Override  
    public boolean equals(Object obj)   
    {  
        if (obj == null)   
            return false;  
        if (obj == this)  
            return true;  
        if (obj instanceof Return) {
            var ret = (Return) obj;
            return (doc == ret.doc || doc.equals(ret.doc)) &&
                (fmt == ret.fmt || fmt.equals(ret.fmt)) &&
                (tyme_name == ret.tyme_name || tyme_name.equals(ret.tyme_name));
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = (this.doc != null ? this.doc.hashCode() : 0);
        hash = 100 * hash + (this.fmt != null ? this.fmt.hashCode() : 0);
        hash = 100 * hash + (this.tyme_name != null ? this.tyme_name.hashCode() : 0);
        return hash;
    }
}
