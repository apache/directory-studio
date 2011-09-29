package org.apache.directory.studio.ldapservers.model;


/**
 * A {@link LdapServerAdapterConfigurationPageModifyListener} listens for modifications of the 
 * {@link LdapServerAdapterConfigurationPage}.
 */
public interface LdapServerAdapterConfigurationPageModifyListener
{
    /**
     * Indicates that the configuration page was modified.
     */
    public void configurationPageModified();
}