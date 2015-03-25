package org.apache.directory.studio.openldap.config.model;


/**
 * Java bean for the 'olcChainConfig' object class.
 */
public class OlcChainConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcChainCacheURI' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainCacheURI")
    private Boolean olcChainCacheURI;

    /**
     * Field for the 'olcChainingBehavior' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainingBehavior")
    private String olcChainingBehavior;

    /**
     * Field for the 'olcChainMaxReferralDepth' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainMaxReferralDepth")
    private Integer olcChainMaxReferralDepth;

    /**
     * Field for the 'olcChainReturnError' attribute.
     */
    @ConfigurationElement(attributeType = "olcChainReturnError")
    private Boolean olcChainReturnError;


    /**
     * @return the olcChainCacheURI
     */
    public Boolean getOlcChainCacheURI()
    {
        return olcChainCacheURI;
    }


    /**
     * @return the olcChainingBehavior
     */
    public String getOlcChainingBehavior()
    {
        return olcChainingBehavior;
    }


    /**
     * @return the olcChainMaxReferralDepth
     */
    public Integer getOlcChainMaxReferralDepth()
    {
        return olcChainMaxReferralDepth;
    }


    /**
     * @return the olcChainReturnError
     */
    public Boolean getOlcChainReturnError()
    {
        return olcChainReturnError;
    }


    /**
     * @param olcChainCacheURI the olcChainCacheURI to set
     */
    public void setOlcChainCacheURI( Boolean olcChainCacheURI )
    {
        this.olcChainCacheURI = olcChainCacheURI;
    }


    /**
     * @param olcChainingBehavior the olcChainingBehavior to set
     */
    public void setOlcChainingBehavior( String olcChainingBehavior )
    {
        this.olcChainingBehavior = olcChainingBehavior;
    }


    /**
     * @param olcChainMaxReferralDepth the olcChainMaxReferralDepth to set
     */
    public void setOlcChainMaxReferralDepth( Integer olcChainMaxReferralDepth )
    {
        this.olcChainMaxReferralDepth = olcChainMaxReferralDepth;
    }


    /**
     * @param olcChainReturnError the olcChainReturnError to set
     */
    public void setOlcChainReturnError( Boolean olcChainReturnError )
    {
        this.olcChainReturnError = olcChainReturnError;
    }
}
