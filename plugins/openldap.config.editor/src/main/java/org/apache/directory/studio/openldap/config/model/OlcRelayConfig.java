package org.apache.directory.studio.openldap.config.model;

import org.apache.directory.api.ldap.model.name.Dn;


/**
 * Java bean for the 'olcRelayConfig' object class.
 */
public class OlcRelayConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcRelay' attribute.
     */
    @ConfigurationElement(attributeType = "olcRelay")
    private Dn olcRelay;


    /**
     * @return the olcRelay
     */
    public Dn getOlcRelay()
    {
        return olcRelay;
    }


    /**
     * @param olcRelay the olcRelay to set
     */
    public void setOlcRelay( Dn olcRelay )
    {
        this.olcRelay = olcRelay;
    }
}
