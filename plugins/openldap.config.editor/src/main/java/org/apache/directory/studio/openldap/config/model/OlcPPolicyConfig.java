package org.apache.directory.studio.openldap.config.model;

import org.apache.directory.api.ldap.model.name.Dn;


/**
 * Java bean for the 'olcPPolicyConfig' object class.
 */
public class OlcPPolicyConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcPPolicyDefault' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyDefault")
    private Dn olcPPolicyDefault;

    /**
     * Field for the 'olcPPolicyForwardUpdates' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyForwardUpdates")
    private boolean olcPPolicyForwardUpdates;

    /**
     * Field for the 'olcPPolicyHashCleartext' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyHashCleartext")
    private boolean olcPPolicyHashCleartext;

    /**
     * Field for the 'olcPPolicyUseLockout' attribute.
     */
    @ConfigurationElement(attributeType = "olcPPolicyUseLockout")
    private boolean olcPPolicyUseLockout;


    /**
     * @return the olcPPolicyDefault
     */
    public Dn getOlcPPolicyDefault()
    {
        return olcPPolicyDefault;
    }


    /**
     * @return the olcPPolicyForwardUpdates
     */
    public boolean getOlcPPolicyForwardUpdates()
    {
        return olcPPolicyForwardUpdates;
    }


    /**
     * @return the olcPPolicyHashCleartext
     */
    public boolean getOlcPPolicyHashCleartext()
    {
        return olcPPolicyHashCleartext;
    }


    /**
     * @return the olcPPolicyUseLockout
     */
    public boolean getOlcPPolicyUseLockout()
    {
        return olcPPolicyUseLockout;
    }


    /**
     * @param olcPPolicyDefault the olcPPolicyDefault to set
     */
    public void setOlcPPolicyDefault( Dn olcPPolicyDefault )
    {
        this.olcPPolicyDefault = olcPPolicyDefault;
    }


    /**
     * @param olcPPolicyForwardUpdates the olcPPolicyForwardUpdates to set
     */
    public void setOlcPPolicyForwardUpdates( boolean olcPPolicyForwardUpdates )
    {
        this.olcPPolicyForwardUpdates = olcPPolicyForwardUpdates;
    }


    /**
     * @param olcPPolicyHashCleartext the olcPPolicyHashCleartext to set
     */
    public void setOlcPPolicyHashCleartext( boolean olcPPolicyHashCleartext )
    {
        this.olcPPolicyHashCleartext = olcPPolicyHashCleartext;
    }


    /**
     * @param olcPPolicyUseLockout the olcPPolicyUseLockout to set
     */
    public void setOlcPPolicyUseLockout( boolean olcPPolicyUseLockout )
    {
        this.olcPPolicyUseLockout = olcPPolicyUseLockout;
    }
}
