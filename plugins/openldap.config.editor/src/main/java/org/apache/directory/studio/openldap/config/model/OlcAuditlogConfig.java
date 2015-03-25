package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;


/**
 * Java bean for the 'olcAuditlogConfig' object class.
 */
public class OlcAuditlogConfig extends OlcOverlayConfig
{
    /**
     * Field for the 'olcAuditlogFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcAuditlogFile")
    private List<String> olcAuditlogFile = new ArrayList<String>();


    /**
     * @param strings
     */
    public void addOlcAuditlogFile( String... strings )
    {
        for ( String string : strings )
        {
            olcAuditlogFile.add( string );
        }
    }


    public void clearOlcAuditlogFile()
    {
        olcAuditlogFile.clear();
    }


    /**
     * @return the olcAuditlogFile
     */
    public List<String> getOlcAuditlogFile()
    {
        return olcAuditlogFile;
    }


    /**
     * @param olcAuditlogFile the olcAuditlogFile to set
     */
    public void setOlcAuditlogFile( List<String> olcAuditlogFile )
    {
        this.olcAuditlogFile = olcAuditlogFile;
    }
}
