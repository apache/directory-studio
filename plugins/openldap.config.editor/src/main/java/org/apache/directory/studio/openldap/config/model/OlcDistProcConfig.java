package org.apache.directory.studio.openldap.config.model;


/**
 * Java bean for the 'olcDistProcConfig' object class.
 */
public class OlcDistProcConfig extends OlcOverlayConfig
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
}
