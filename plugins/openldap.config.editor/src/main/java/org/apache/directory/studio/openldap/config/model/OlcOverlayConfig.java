package org.apache.directory.studio.openldap.config.model;


/**
 * Java bean for the 'olcOverlayConfig' object class.
 */
public class OlcOverlayConfig extends OlcConfig
{
    /**
     * Field for the 'olcOverlay' attribute.
     */
    @ConfigurationElement(attributeType = "olcOverlay", isOptional = false, isRdn = true)
    private String olcOverlay;


    /**
     * @return the olcOverlay
     */
    public String getOlcOverlay()
    {
        return olcOverlay;
    }


    /**
     * @param olcOverlay the olcOverlay to set
     */
    public void setOlcOverlay( String olcOverlay )
    {
        this.olcOverlay = olcOverlay;
    }
}
