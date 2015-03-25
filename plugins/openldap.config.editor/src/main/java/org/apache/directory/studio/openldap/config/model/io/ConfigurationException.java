package org.apache.directory.studio.openldap.config.model.io;


import org.apache.directory.api.ldap.model.exception.LdapException;


/**
 * An exception used when we cannot read the configuration, or when an error
 * occured while reading it from the DIT.
 */
public class ConfigurationException extends LdapException
{
    /** The serial version UUID */
    private static final long serialVersionUID = 1L;


    /**
     * Creates a new instance of ConfigurationException.
     *
     * @param message The exception message
     */
    public ConfigurationException( String message )
    {
        super( message );
    }


    /**
     * Creates a new instance of ConfigurationException.
     *
     * @param cause the original cause
     */
    public ConfigurationException( Throwable cause )
    {
        super( cause );
    }


    /**
     * Creates a new instance of ConfigurationException.
     *
     * @param message The exception message
     * @param cause the original cause
     */
    public ConfigurationException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
