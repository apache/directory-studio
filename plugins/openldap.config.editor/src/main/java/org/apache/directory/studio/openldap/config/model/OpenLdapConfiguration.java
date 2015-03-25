package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;


/**
 * This class implements the basic class for an OpenLDAP configuration.
 * <p>
 * It contains all the configuration objects found under the "cn=config" branch. 
 */
public class OpenLdapConfiguration
{
    private List<OlcConfig> configurationElements = new ArrayList<OlcConfig>();


    /**
     * @return the configurationElements
     */
    public List<OlcConfig> getConfigurationElements()
    {
        return configurationElements;
    }


    /**
     * @param e
     * @return
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add( OlcConfig o )
    {
        return configurationElements.add( o );
    }


    /**
     * @param o
     * @return
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains( OlcConfig o )
    {
        return configurationElements.contains( o );
    }


    /**
     * @param o
     * @return
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove( OlcConfig o )
    {
        return configurationElements.remove( o );
    }


    /**
     * @return
     * @see java.util.List#size()
     */
    public int size()
    {
        return configurationElements.size();
    }
}
