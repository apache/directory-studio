package org.apache.directory.studio.openldap.config.model;


/**
 * Java bean for the 'olcPBindConfig' object class.
 */
public class OlcPBindConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcDbURI' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbURI", isOptional = false)
    private String olcDbURI;

    /**
     * Field for the 'olcDbNetworkTimeout' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbNetworkTimeout")
    private String olcDbNetworkTimeout;

    /**
     * Field for the 'olcDbQuarantine' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbQuarantine")
    private String olcDbQuarantine;

    /**
     * Field for the 'olcStartTLS' attribute.
     */
    @ConfigurationElement(attributeType = "olcStartTLS")
    private String olcStartTLS;


    /**
     * @return the olcDbNetworkTimeout
     */
    public String getOlcDbNetworkTimeout()
    {
        return olcDbNetworkTimeout;
    }


    /**
     * @return the olcDbQuarantine
     */
    public String getOlcDbQuarantine()
    {
        return olcDbQuarantine;
    }


    /**
     * @return the olcDbURI
     */
    public String getOlcDbURI()
    {
        return olcDbURI;
    }


    /**
     * @return the olcStartTLS
     */
    public String getOlcStartTLS()
    {
        return olcStartTLS;
    }


    /**
     * @param olcDbNetworkTimeout the olcDbNetworkTimeout to set
     */
    public void setOlcDbNetworkTimeout( String olcDbNetworkTimeout )
    {
        this.olcDbNetworkTimeout = olcDbNetworkTimeout;
    }


    /**
     * @param olcDbQuarantine the olcDbQuarantine to set
     */
    public void setOlcDbQuarantine( String olcDbQuarantine )
    {
        this.olcDbQuarantine = olcDbQuarantine;
    }


    /**
     * @param olcDbURI the olcDbURI to set
     */
    public void setOlcDbURI( String olcDbURI )
    {
        this.olcDbURI = olcDbURI;
    }


    /**
     * @param olcStartTLS the olcStartTLS to set
     */
    public void setOlcStartTLS( String olcStartTLS )
    {
        this.olcStartTLS = olcStartTLS;
    }
}
