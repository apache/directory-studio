package org.apache.directory.studio.openldap.config.model;


/**
 * Java bean for the 'olcSyncProvConfig' object class.
 */
public class OlcSyncProvConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcSpCheckpoint' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpCheckpoint")
    private String olcSpCheckpoint;

    /**
     * Field for the 'olcSpNoPresent' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpNoPresent")
    private Boolean olcSpNoPresent;

    /**
     * Field for the 'olcSpReloadHint' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpReloadHint")
    private Boolean olcSpReloadHint;

    /**
     * Field for the 'olcSpSessionlog' attribute.
     */
    @ConfigurationElement(attributeType = "olcSpSessionlog")
    private Integer olcSpSessionlog;


    /**
     * @return the olcSpCheckpoint
     */
    public String getOlcSpCheckpoint()
    {
        return olcSpCheckpoint;
    }


    /**
     * @return the olcSpNoPresent
     */
    public Boolean getOlcSpNoPresent()
    {
        return olcSpNoPresent;
    }


    /**
     * @return the olcSpReloadHint
     */
    public Boolean getOlcSpReloadHint()
    {
        return olcSpReloadHint;
    }


    /**
     * @return the olcSpSessionlog
     */
    public Integer getOlcSpSessionlog()
    {
        return olcSpSessionlog;
    }


    /**
     * @param olcSpCheckpoint the olcSpCheckpoint to set
     */
    public void setOlcSpCheckpoint( String olcSpCheckpoint )
    {
        this.olcSpCheckpoint = olcSpCheckpoint;
    }


    /**
     * @param olcSpNoPresent the olcSpNoPresent to set
     */
    public void setOlcSpNoPresent( Boolean olcSpNoPresent )
    {
        this.olcSpNoPresent = olcSpNoPresent;
    }


    /**
     * @param olcSpReloadHint the olcSpReloadHint to set
     */
    public void setOlcSpReloadHint( Boolean olcSpReloadHint )
    {
        this.olcSpReloadHint = olcSpReloadHint;
    }


    /**
     * @param olcSpSessionlog the olcSpSessionlog to set
     */
    public void setOlcSpSessionlog( Integer olcSpSessionlog )
    {
        this.olcSpSessionlog = olcSpSessionlog;
    }
}
