package org.apache.directory.studio.model;


import java.util.Comparator;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractMatchingRule;
import org.apache.directory.shared.ldap.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.Syntax;


public class MatchingRuleImpl extends AbstractMatchingRule implements MutableSchemaObject
{
    private static final long serialVersionUID = 1L;
    
    /** The OID of the syntax */
    private String syntaxtOid;


    public String getSyntaxtOid()
    {
        return syntaxtOid;
    }


    public void setSyntaxtOid( String syntaxtOid )
    {
        this.syntaxtOid = syntaxtOid;
    }


    public MatchingRuleImpl( String oid )
    {
        super( oid );
    }


    public Comparator getComparator() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Normalizer getNormalizer() throws NamingException
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
}