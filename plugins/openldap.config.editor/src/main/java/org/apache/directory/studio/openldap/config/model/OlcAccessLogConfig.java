package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;


/**
 * Java bean for the 'olcAccessLogConfig' object class.
 */
public class OlcAccessLogConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcAccessLogDB' attribute.
     */
    @ConfigurationElement(attributeType = "olcAccessLogDB", isOptional = false)
    private Dn olcAccessLogDB;

    /**
     * Field for the 'olcAccessLogOld' attribute.
     */
    @ConfigurationElement(attributeType = "olcAccessLogOld")
    private String olcAccessLogOld;

    /**
     * Field for the 'olcAccessLogOldAttr' attribute.
     */
    @ConfigurationElement(attributeType = "olcAccessLogOldAttr")
    private List<String> olcAccessLogOldAttr = new ArrayList<String>();

    /**
     * Field for the 'olcAccessLogOps' attribute.
     */
    @ConfigurationElement(attributeType = "olcAccessLogOps")
    private List<String> olcAccessLogOps = new ArrayList<String>();

    /**
     * Field for the 'olcAccessLogPurge' attribute.
     */
    @ConfigurationElement(attributeType = "olcAccessLogPurge")
    private String olcAccessLogPurge;

    /**
     * Field for the 'olcAccessLogSuccess' attribute.
     */
    @ConfigurationElement(attributeType = "olcAccessLogSuccess")
    private Boolean olcAccessLogSuccess;


    /**
     * @param strings
     */
    public void addOlcAccessLogOldAttr( String... strings )
    {
        for ( String string : strings )
        {
            olcAccessLogOldAttr.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcAccessLogOps( String... strings )
    {
        for ( String string : strings )
        {
            olcAccessLogOps.add( string );
        }
    }


    public void clearOlcAccessLogOldAttr()
    {
        olcAccessLogOldAttr.clear();
    }


    public void clearOlcAccessLogOps()
    {
        olcAccessLogOps.clear();
    }


    /**
     * @return the olcAccessLogDB
     */
    public Dn getOlcAccessLogDB()
    {
        return olcAccessLogDB;
    }


    /**
     * @return the olcAccessLogOld
     */
    public String getOlcAccessLogOld()
    {
        return olcAccessLogOld;
    }


    /**
     * @return the olcAccessLogOldAttr
     */
    public List<String> getOlcAccessLogOldAttr()
    {
        return olcAccessLogOldAttr;
    }


    /**
     * @return the olcAccessLogOps
     */
    public List<String> getOlcAccessLogOps()
    {
        return olcAccessLogOps;
    }


    /**
     * @return the olcAccessLogPurge
     */
    public String getOlcAccessLogPurge()
    {
        return olcAccessLogPurge;
    }


    /**
     * @return the olcAccessLogSuccess
     */
    public Boolean getOlcAccessLogSuccess()
    {
        return olcAccessLogSuccess;
    }


    /**
     * @param olcAccessLogDB the olcAccessLogDB to set
     */
    public void setOlcAccessLogDB( Dn olcAccessLogDB )
    {
        this.olcAccessLogDB = olcAccessLogDB;
    }


    /**
     * @param olcAccessLogOld the olcAccessLogOld to set
     */
    public void setOlcAccessLogOld( String olcAccessLogOld )
    {
        this.olcAccessLogOld = olcAccessLogOld;
    }


    /**
     * @param olcAccessLogOldAttr the olcAccessLogOldAttr to set
     */
    public void setOlcAccessLogOldAttr( List<String> olcAccessLogOldAttr )
    {
        this.olcAccessLogOldAttr = olcAccessLogOldAttr;
    }


    /**
     * @param olcAccessLogOps the olcAccessLogOps to set
     */
    public void setOlcAccessLogOps( List<String> olcAccessLogOps )
    {
        this.olcAccessLogOps = olcAccessLogOps;
    }


    /**
     * @param olcAccessLogPurge the olcAccessLogPurge to set
     */
    public void setOlcAccessLogPurge( String olcAccessLogPurge )
    {
        this.olcAccessLogPurge = olcAccessLogPurge;
    }


    /**
     * @param olcAccessLogSuccess the olcAccessLogSuccess to set
     */
    public void setOlcAccessLogSuccess( Boolean olcAccessLogSuccess )
    {
        this.olcAccessLogSuccess = olcAccessLogSuccess;
    }
}
