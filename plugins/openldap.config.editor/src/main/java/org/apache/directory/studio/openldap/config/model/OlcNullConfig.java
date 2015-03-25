package org.apache.directory.studio.openldap.config.model;


/**
 * Java bean for the 'olcNullConfig' object class.
 */
public class OlcNullConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbBindAllowed' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbBindAllowed")
    private Boolean olcDbBindAllowed;


    /**
     * @return the olcDbBindAllowed
     */
    public Boolean getOlcDbBindAllowed()
    {
        return olcDbBindAllowed;
    }


    /**
     * @param olcDbBindAllowed the olcDbBindAllowed to set
     */
    public void setOlcDbBindAllowed( Boolean olcDbBindAllowed )
    {
        this.olcDbBindAllowed = olcDbBindAllowed;
    }
}
