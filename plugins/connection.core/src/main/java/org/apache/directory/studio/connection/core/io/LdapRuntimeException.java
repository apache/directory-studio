package org.apache.directory.studio.connection.core.io;


import org.apache.directory.api.ldap.model.exception.LdapException;


public class LdapRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 3618077059423567243L;


    public LdapRuntimeException( LdapException exception )
    {
        super( exception );
    }
}
