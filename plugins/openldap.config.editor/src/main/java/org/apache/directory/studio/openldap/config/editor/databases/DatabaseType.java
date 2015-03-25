package org.apache.directory.studio.openldap.config.editor.databases;


/**
 * This enum describes the various type of databases.
 */
public enum DatabaseType
{
    /** None */
    NONE,

    /** Oracle Berkerly DB */
    BDB,

    /** Oracle Berkerly DB */
    FRONTEND,
    
    /** Hybrid DB */
    HDB,
    
    /** LDAP DB*/
    LDAP,
    
    /** LDIF DB*/
    LDIF;
}