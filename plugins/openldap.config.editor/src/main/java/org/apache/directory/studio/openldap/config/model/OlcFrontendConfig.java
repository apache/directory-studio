package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;


/**
 * Java bean for the 'olcDbSocketConfig' object class.
 */
public class OlcFrontendConfig extends AuxiliaryObjectClass
{
    /**
     * Field for the 'olcDefaultSearchBase' attribute.
     */
    @ConfigurationElement(attributeType = "olcDefaultSearchBase")
    private String olcDefaultSearchBase;

    /**
     * Field for the 'olcPasswordHash' attribute.
     */
    @ConfigurationElement(attributeType = "olcPasswordHash")
    private List<String> olcPasswordHash = new ArrayList<String>();

    /**
     * Field for the 'olcSortVals' attribute.
     */
    @ConfigurationElement(attributeType = "olcSortVals")
    private List<String> olcSortVals = new ArrayList<String>();


    /**
     * @param strings
     */
    public void addOlcPasswordHash( String... strings )
    {
        for ( String string : strings )
        {
            olcPasswordHash.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcSortVals( String... strings )
    {
        for ( String string : strings )
        {
            olcSortVals.add( string );
        }
    }


    /**
     * @param strings
     */
    public void clearOlcPasswordHash()
    {
        olcPasswordHash.clear();
    }


    public void clearOlcSortVals()
    {
        olcSortVals.clear();
    }


    /**
     * @return the olcDefaultSearchBase
     */
    public String getOlcDefaultSearchBase()
    {
        return olcDefaultSearchBase;
    }


    /**
     * @return the olcPasswordHash
     */
    public List<String> getOlcPasswordHash()
    {
        return olcPasswordHash;
    }


    /**
     * @return the olcSortVals
     */
    public List<String> getOlcSortVals()
    {
        return olcSortVals;
    }


    /**
     * @param olcDefaultSearchBase the olcDefaultSearchBase to set
     */
    public void setOlcDefaultSearchBase( String olcDefaultSearchBase )
    {
        this.olcDefaultSearchBase = olcDefaultSearchBase;
    }


    /**
     * @param olcPasswordHash the setOlcPasswordHash to set
     */
    public void setOlcPasswordHash( List<String> olcPasswordHash )
    {
        this.olcPasswordHash = olcPasswordHash;
    }


    /**
     * @param olcSortVals the olcSortVals to set
     */
    public void setOlcSortVals( List<String> olcSortVals )
    {
        this.olcSortVals = olcSortVals;
    }
}
