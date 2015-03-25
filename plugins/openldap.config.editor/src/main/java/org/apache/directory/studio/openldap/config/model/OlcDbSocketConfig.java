package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;


/**
 * Java bean for the 'olcDbSocketConfig' object class.
 */
public class OlcDbSocketConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbSocketPath' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbSocketPath", isOptional = false)
    private String olcDbSocketPath;

    /**
     * Field for the 'olcDbSocketExtensions' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbSocketExtensions")
    private List<String> olcDbSocketExtensions = new ArrayList<String>();


    /**
     * @param strings
     */
    public void addOlcDbSocketExtensions( String... strings )
    {
        for ( String string : strings )
        {
            olcDbSocketExtensions.add( string );
        }
    }


    public void clearOlcDbSocketExtensions()
    {
        olcDbSocketExtensions.clear();
    }


    /**
     * @return the olcDbSocketExtensions
     */
    public List<String> getOlcDbSocketExtensions()
    {
        return olcDbSocketExtensions;
    }


    /**
     * @return the olcDbSocketPath
     */
    public String getOlcDbSocketPath()
    {
        return olcDbSocketPath;
    }


    /**
     * @param olcDbSocketExtensions the olcDbSocketExtensions to set
     */
    public void setOlcDbSocketExtensions( List<String> olcDbSocketExtensions )
    {
        this.olcDbSocketExtensions = olcDbSocketExtensions;
    }


    /**
     * @param olcDbSocketPath the olcDbSocketPath to set
     */
    public void setOlcDbSocketPath( String olcDbSocketPath )
    {
        this.olcDbSocketPath = olcDbSocketPath;
    }
}
