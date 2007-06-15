package org.apache.directory.studio.model;


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractSyntax;
import org.apache.directory.shared.ldap.schema.syntax.SyntaxChecker;


public class SyntaxImpl extends AbstractSyntax
{
    private static final long serialVersionUID = 1L;


    public SyntaxImpl( String oid )
    {
        super( oid );
        // TODO Auto-generated constructor stub
    }


    @Override
    public void setHumanReadible( boolean isHumanReadible )
    {
        // TODO Auto-generated method stub
        super.setHumanReadible( isHumanReadible );
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


    public SyntaxChecker getSyntaxChecker() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
