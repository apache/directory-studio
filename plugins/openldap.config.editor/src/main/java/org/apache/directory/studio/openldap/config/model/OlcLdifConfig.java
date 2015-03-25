package org.apache.directory.studio.openldap.config.model;


/**
 * Java bean for the 'olcLdifConfig' object class.
 */
public class OlcLdifConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbDirectory' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbDirectory", isOptional = false)
    private String olcDbDirectory;


    /**
     * @return the olcDbDirectory
     */
    public String getOlcDbDirectory()
    {
        return olcDbDirectory;
    }


    /**
     * @param olcDbDirectory the olcDbDirectory to set
     */
    public void setOlcDbDirectory( String olcDbDirectory )
    {
        this.olcDbDirectory = olcDbDirectory;
    }
}
