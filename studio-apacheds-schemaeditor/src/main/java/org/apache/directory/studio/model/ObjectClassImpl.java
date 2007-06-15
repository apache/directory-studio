
package org.apache.directory.studio.model;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractSchemaObject;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;

public class ObjectClassImpl extends AbstractSchemaObject implements MutableSchemaObject, ObjectClass
{
    private ObjectClassTypeEnum objectClassTypeEnum;
    
    private String[] mayNamesList;
    
    private String[] mustNamesList;
    
    private String[] superClassesNames;
    
    public ObjectClassImpl( String oid )
    {
        super( oid );
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AttributeType[] getMayList() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public AttributeType[] getMustList() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectClass[] getSuperClasses() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDescription( String description )
    {
        // TODO Auto-generated method stub
        super.setDescription( description );
    }

    @Override
    public void setNames( String[] names )
    {
        // TODO Auto-generated method stub
        super.setNames( names );
    }

    @Override
    public void setObsolete( boolean obsolete )
    {
        // TODO Auto-generated method stub
        super.setObsolete( obsolete );
    }

    public String[] getMayNamesList()
    {
        return mayNamesList;
    }

    public void setMayNamesList( String[] mayNamesList )
    {
        this.mayNamesList = mayNamesList;
    }

    public String[] getMustNamesList()
    {
        return mustNamesList;
    }

    public void setMustNamesList( String[] mustNamesList )
    {
        this.mustNamesList = mustNamesList;
    }

    public String[] getSuperClassesNames()
    {
        return superClassesNames;
    }

    public void setSuperClassesNames( String[] superClassesNames )
    {
        this.superClassesNames = superClassesNames;
    }

    public ObjectClassTypeEnum getObjectClassTypeEnum()
    {
        return objectClassTypeEnum;
    }

    public void setObjectClassTypeEnum( ObjectClassTypeEnum objectClassTypeEnum )
    {
        this.objectClassTypeEnum = objectClassTypeEnum;
    }

    public ObjectClassTypeEnum getType()
    {
        return objectClassTypeEnum;
    }
    
    
    public void setType( ObjectClassTypeEnum objectClassTypeEnum )
    {
        this.objectClassTypeEnum = objectClassTypeEnum;
    }
}
