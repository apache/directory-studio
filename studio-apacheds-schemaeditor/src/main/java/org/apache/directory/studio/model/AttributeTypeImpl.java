package org.apache.directory.studio.model;


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractAttributeType;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.schema.Syntax;
import org.apache.directory.shared.ldap.schema.UsageEnum;


public class AttributeTypeImpl extends AbstractAttributeType implements MutableSchemaObject
{
    /** The name of the superior */
    private String superiorName;
    
    /** The OID of the Syntax */
    private String syntaxOid;
    
    /** The name of the Equality MatchingRule */
    private String equalityName;
    
    /** The name of the Ordering MatchingRule */
    private String orderingName;
    
    /** The name of the Substr MatchingRule */
    private String substrName;


    public String getEqualityName()
    {
        return equalityName;
    }


    public void setEqualityName( String equalityName )
    {
        this.equalityName = equalityName;
    }


    public String getOrderingName()
    {
        return orderingName;
    }


    public void setOrderingName( String orderingName )
    {
        this.orderingName = orderingName;
    }


    public String getSubstrName()
    {
        return substrName;
    }


    public void setSubstrName( String substrName )
    {
        this.substrName = substrName;
    }


    public String getSuperiorName()
    {
        return superiorName;
    }


    public void setSuperiorName( String superiorName )
    {
        this.superiorName = superiorName;
    }


    public String getSyntaxOid()
    {
        return syntaxOid;
    }


    public void setSyntaxOid( String syntaxOid )
    {
        this.syntaxOid = syntaxOid;
    }


    public AttributeTypeImpl( String oid )
    {
        super( oid );
        
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public MatchingRule getEquality() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    public MatchingRule getOrdering() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    public MatchingRule getSubstr() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    public AttributeType getSuperior() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Syntax getSyntax() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setDescription( String description )
    {
        super.setDescription( description );
    }


    @Override
    public void setNames( String[] names )
    {
        super.setNames( names );
    }


    @Override
    public void setObsolete( boolean obsolete )
    {
        super.setObsolete( obsolete );
    }


    @Override
    public void setCanUserModify( boolean canUserModify )
    {
        // TODO Auto-generated method stub
        super.setCanUserModify( canUserModify );
    }


    @Override
    public void setCollective( boolean collective )
    {
        // TODO Auto-generated method stub
        super.setCollective( collective );
    }


    @Override
    public void setLength( int length )
    {
        // TODO Auto-generated method stub
        super.setLength( length );
    }


    @Override
    public void setSingleValue( boolean singleValue )
    {
        // TODO Auto-generated method stub
        super.setSingleValue( singleValue );
    }


    @Override
    public void setUsage( UsageEnum usage )
    {
        // TODO Auto-generated method stub
        super.setUsage( usage );
    }
}
