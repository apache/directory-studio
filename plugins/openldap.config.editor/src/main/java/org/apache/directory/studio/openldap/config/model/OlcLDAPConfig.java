package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.name.Dn;


/**
 * Java bean for the 'olcLDAPConfig' object class.
 */
public class OlcLDAPConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbACLAuthcDn' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbACLAuthcDn")
    private Dn olcDbACLAuthcDn;

    /**
     * Field for the 'olcDbACLBind' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbACLBind")
    private String olcDbACLBind;

    /**
     * Field for the 'olcDbACLPasswd' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbACLPasswd")
    private String olcDbACLPasswd;

    /**
     * Field for the 'olcDbCancel' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbCancel")
    private String olcDbCancel;

    /**
     * Field for the 'olcDbChaseReferrals' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbChaseReferrals")
    private Boolean olcDbChaseReferrals;

    /**
     * Field for the 'olcDbConnectionPoolMax' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbConnectionPoolMax")
    private Integer olcDbConnectionPoolMax;

    /**
     * Field for the 'olcDbConnTtl' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbConnTtl")
    private String olcDbConnTtl;

    /**
     * Field for the 'olcDbIDAssertAuthcDn' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIDAssertAuthcDn")
    private Dn olcDbIDAssertAuthcDn;

    /**
     * Field for the 'olcDbIDAssertAuthzFrom' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIDAssertAuthzFrom")
    private List<String> olcDbIDAssertAuthzFrom = new ArrayList<String>();

    /**
     * Field for the 'olcDbIDAssertBind' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIDAssertBind")
    private String olcDbIDAssertBind;

    /**
     * Field for the 'olcDbIDAssertMode' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIDAssertMode")
    private String olcDbIDAssertMode;

    /**
     * Field for the 'olcDbIDAssertPassThru' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIDAssertPassThru")
    private List<String> olcDbIDAssertPassThru = new ArrayList<String>();

    /**
     * Field for the 'olcDbIDAssertPasswd' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIDAssertPasswd")
    private String olcDbIDAssertPasswd;

    /**
     * Field for the 'olcDbIdleTimeout' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIdleTimeout")
    private String olcDbIdleTimeout;

    /**
     * Field for the 'olcDbNetworkTimeout' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbNetworkTimeout")
    private String olcDbNetworkTimeout;

    /**
     * Field for the 'olcDbNoRefs' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbNoRefs")
    private Boolean olcDbNoRefs;

    /**
     * Field for the 'olcDbNoUndefFilter' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbNoUndefFilter")
    private Boolean olcDbNoUndefFilter;

    /**
     * Field for the 'olcDbProtocolVersion' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbProtocolVersion")
    private Integer olcDbProtocolVersion;

    /**
     * Field for the 'olcDbProxyWhoAmI' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbProxyWhoAmI")
    private Boolean olcDbProxyWhoAmI;

    /**
     * Field for the 'olcDbQuarantine' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbQuarantine")
    private String olcDbQuarantine;

    /**
     * Field for the 'olcDbRebindAsUser' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbRebindAsUser")
    private Boolean olcDbRebindAsUser;

    /**
     * Field for the 'olcDbSingleConn' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbSingleConn")
    private Boolean olcDbSingleConn;

    /**
     * Field for the 'olcDbStartTLS' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbStartTLS")
    private String olcDbStartTLS;

    /**
     * Field for the 'olcDbTFSupport' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbTFSupport")
    private String olcDbTFSupport;

    /**
     * Field for the 'olcDbTimeout' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbTimeout")
    private String olcDbTimeout;

    /**
     * Field for the 'olcDbURI' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbURI")
    private String olcDbURI;

    /**
     * Field for the 'olcDbUseTemporaryConn' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbUseTemporaryConn")
    private Boolean olcDbUseTemporaryConn;


    /**
     * @param strings
     */
    public void addOlcDbIDAssertAuthzFrom( String... strings )
    {
        for ( String string : strings )
        {
            olcDbIDAssertAuthzFrom.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcDbIDAssertPassThru( String... strings )
    {
        for ( String string : strings )
        {
            olcDbIDAssertPassThru.add( string );
        }
    }


    public void clearOlcDbIDAssertAuthzFrom()
    {
        olcDbIDAssertAuthzFrom.clear();
    }


    public void clearOlcDbIDAssertPassThru()
    {
        olcDbIDAssertPassThru.clear();
    }


    /**
     * @return the olcDbACLAuthcDn
     */
    public Dn getOlcDbACLAuthcDn()
    {
        return olcDbACLAuthcDn;
    }


    /**
     * @return the olcDbACLBind
     */
    public String getOlcDbACLBind()
    {
        return olcDbACLBind;
    }


    /**
     * @return the olcDbACLPasswd
     */
    public String getOlcDbACLPasswd()
    {
        return olcDbACLPasswd;
    }


    /**
     * @return the olcDbCancel
     */
    public String getOlcDbCancel()
    {
        return olcDbCancel;
    }


    /**
     * @return the olcDbChaseReferrals
     */
    public Boolean getOlcDbChaseReferrals()
    {
        return olcDbChaseReferrals;
    }


    /**
     * @return the olcDbConnectionPoolMax
     */
    public Integer getOlcDbConnectionPoolMax()
    {
        return olcDbConnectionPoolMax;
    }


    /**
     * @return the olcDbConnTtl
     */
    public String getOlcDbConnTtl()
    {
        return olcDbConnTtl;
    }


    /**
     * @return the olcDbIDAssertAuthcDn
     */
    public Dn getOlcDbIDAssertAuthcDn()
    {
        return olcDbIDAssertAuthcDn;
    }


    /**
     * @return the olcDbIDAssertAuthzFrom
     */
    public List<String> getOlcDbIDAssertAuthzFrom()
    {
        return olcDbIDAssertAuthzFrom;
    }


    /**
     * @return the olcDbIDAssertBind
     */
    public String getOlcDbIDAssertBind()
    {
        return olcDbIDAssertBind;
    }


    /**
     * @return the olcDbIDAssertMode
     */
    public String getOlcDbIDAssertMode()
    {
        return olcDbIDAssertMode;
    }


    /**
     * @return the olcDbIDAssertPassThru
     */
    public List<String> getOlcDbIDAssertPassThru()
    {
        return olcDbIDAssertPassThru;
    }


    /**
     * @return the olcDbIDAssertPasswd
     */
    public String getOlcDbIDAssertPasswd()
    {
        return olcDbIDAssertPasswd;
    }


    /**
     * @return the olcDbIdleTimeout
     */
    public String getOlcDbIdleTimeout()
    {
        return olcDbIdleTimeout;
    }


    /**
     * @return the olcDbNetworkTimeout
     */
    public String getOlcDbNetworkTimeout()
    {
        return olcDbNetworkTimeout;
    }


    /**
     * @return the olcDbNoRefs
     */
    public Boolean getOlcDbNoRefs()
    {
        return olcDbNoRefs;
    }


    /**
     * @return the olcDbNoUndefFilter
     */
    public Boolean getOlcDbNoUndefFilter()
    {
        return olcDbNoUndefFilter;
    }


    /**
     * @return the olcDbProtocolVersion
     */
    public Integer getOlcDbProtocolVersion()
    {
        return olcDbProtocolVersion;
    }


    /**
     * @return the olcDbProxyWhoAmI
     */
    public Boolean getOlcDbProxyWhoAmI()
    {
        return olcDbProxyWhoAmI;
    }


    /**
     * @return the olcDbQuarantine
     */
    public String getOlcDbQuarantine()
    {
        return olcDbQuarantine;
    }


    /**
     * @return the olcDbRebindAsUser
     */
    public Boolean getOlcDbRebindAsUser()
    {
        return olcDbRebindAsUser;
    }


    /**
     * @return the olcDbSingleConn
     */
    public Boolean getOlcDbSingleConn()
    {
        return olcDbSingleConn;
    }


    /**
     * @return the olcDbStartTLS
     */
    public String getOlcDbStartTLS()
    {
        return olcDbStartTLS;
    }


    /**
     * @return the olcDbTFSupport
     */
    public String getOlcDbTFSupport()
    {
        return olcDbTFSupport;
    }


    /**
     * @return the olcDbTimeout
     */
    public String getOlcDbTimeout()
    {
        return olcDbTimeout;
    }


    /**
     * @return the olcDbURI
     */
    public String getOlcDbURI()
    {
        return olcDbURI;
    }


    /**
     * @return the olcDbUseTemporaryConn
     */
    public Boolean getOlcDbUseTemporaryConn()
    {
        return olcDbUseTemporaryConn;
    }


    /**
     * @param olcDbACLAuthcDn the olcDbACLAuthcDn to set
     */
    public void setOlcDbACLAuthcDn( Dn olcDbACLAuthcDn )
    {
        this.olcDbACLAuthcDn = olcDbACLAuthcDn;
    }


    /**
     * @param olcDbACLBind the olcDbACLBind to set
     */
    public void setOlcDbACLBind( String olcDbACLBind )
    {
        this.olcDbACLBind = olcDbACLBind;
    }


    /**
     * @param olcDbACLPasswd the olcDbACLPasswd to set
     */
    public void setOlcDbACLPasswd( String olcDbACLPasswd )
    {
        this.olcDbACLPasswd = olcDbACLPasswd;
    }


    /**
     * @param olcDbCancel the olcDbCancel to set
     */
    public void setOlcDbCancel( String olcDbCancel )
    {
        this.olcDbCancel = olcDbCancel;
    }


    /**
     * @param olcDbChaseReferrals the olcDbChaseReferrals to set
     */
    public void setOlcDbChaseReferrals( Boolean olcDbChaseReferrals )
    {
        this.olcDbChaseReferrals = olcDbChaseReferrals;
    }


    /**
     * @param olcDbConnectionPoolMax the olcDbConnectionPoolMax to set
     */
    public void setOlcDbConnectionPoolMax( Integer olcDbConnectionPoolMax )
    {
        this.olcDbConnectionPoolMax = olcDbConnectionPoolMax;
    }


    /**
     * @param olcDbConnTtl the olcDbConnTtl to set
     */
    public void setOlcDbConnTtl( String olcDbConnTtl )
    {
        this.olcDbConnTtl = olcDbConnTtl;
    }


    /**
     * @param olcDbIDAssertAuthcDn the olcDbIDAssertAuthcDn to set
     */
    public void setOlcDbIDAssertAuthcDn( Dn olcDbIDAssertAuthcDn )
    {
        this.olcDbIDAssertAuthcDn = olcDbIDAssertAuthcDn;
    }


    /**
     * @param olcDbIDAssertAuthzFrom the olcDbIDAssertAuthzFrom to set
     */
    public void setOlcDbIDAssertAuthzFrom( List<String> olcDbIDAssertAuthzFrom )
    {
        this.olcDbIDAssertAuthzFrom = olcDbIDAssertAuthzFrom;
    }


    /**
     * @param olcDbIDAssertBind the olcDbIDAssertBind to set
     */
    public void setOlcDbIDAssertBind( String olcDbIDAssertBind )
    {
        this.olcDbIDAssertBind = olcDbIDAssertBind;
    }


    /**
     * @param olcDbIDAssertMode the olcDbIDAssertMode to set
     */
    public void setOlcDbIDAssertMode( String olcDbIDAssertMode )
    {
        this.olcDbIDAssertMode = olcDbIDAssertMode;
    }


    /**
     * @param olcDbIDAssertPassThru the olcDbIDAssertPassThru to set
     */
    public void setOlcDbIDAssertPassThru( List<String> olcDbIDAssertPassThru )
    {
        this.olcDbIDAssertPassThru = olcDbIDAssertPassThru;
    }


    /**
     * @param olcDbIDAssertPasswd the olcDbIDAssertPasswd to set
     */
    public void setOlcDbIDAssertPasswd( String olcDbIDAssertPasswd )
    {
        this.olcDbIDAssertPasswd = olcDbIDAssertPasswd;
    }


    /**
     * @param olcDbIdleTimeout the olcDbIdleTimeout to set
     */
    public void setOlcDbIdleTimeout( String olcDbIdleTimeout )
    {
        this.olcDbIdleTimeout = olcDbIdleTimeout;
    }


    /**
     * @param olcDbNetworkTimeout the olcDbNetworkTimeout to set
     */
    public void setOlcDbNetworkTimeout( String olcDbNetworkTimeout )
    {
        this.olcDbNetworkTimeout = olcDbNetworkTimeout;
    }


    /**
     * @param olcDbNoRefs the olcDbNoRefs to set
     */
    public void setOlcDbNoRefs( Boolean olcDbNoRefs )
    {
        this.olcDbNoRefs = olcDbNoRefs;
    }


    /**
     * @param olcDbNoUndefFilter the olcDbNoUndefFilter to set
     */
    public void setOlcDbNoUndefFilter( Boolean olcDbNoUndefFilter )
    {
        this.olcDbNoUndefFilter = olcDbNoUndefFilter;
    }


    /**
     * @param olcDbProtocolVersion the olcDbProtocolVersion to set
     */
    public void setOlcDbProtocolVersion( Integer olcDbProtocolVersion )
    {
        this.olcDbProtocolVersion = olcDbProtocolVersion;
    }


    /**
     * @param olcDbProxyWhoAmI the olcDbProxyWhoAmI to set
     */
    public void setOlcDbProxyWhoAmI( Boolean olcDbProxyWhoAmI )
    {
        this.olcDbProxyWhoAmI = olcDbProxyWhoAmI;
    }


    /**
     * @param olcDbQuarantine the olcDbQuarantine to set
     */
    public void setOlcDbQuarantine( String olcDbQuarantine )
    {
        this.olcDbQuarantine = olcDbQuarantine;
    }


    /**
     * @param olcDbRebindAsUser the olcDbRebindAsUser to set
     */
    public void setOlcDbRebindAsUser( Boolean olcDbRebindAsUser )
    {
        this.olcDbRebindAsUser = olcDbRebindAsUser;
    }


    /**
     * @param olcDbSingleConn the olcDbSingleConn to set
     */
    public void setOlcDbSingleConn( Boolean olcDbSingleConn )
    {
        this.olcDbSingleConn = olcDbSingleConn;
    }


    /**
     * @param olcDbStartTLS the olcDbStartTLS to set
     */
    public void setOlcDbStartTLS( String olcDbStartTLS )
    {
        this.olcDbStartTLS = olcDbStartTLS;
    }


    /**
     * @param olcDbTFSupport the olcDbTFSupport to set
     */
    public void setOlcDbTFSupport( String olcDbTFSupport )
    {
        this.olcDbTFSupport = olcDbTFSupport;
    }


    /**
     * @param olcDbTimeout the olcDbTimeout to set
     */
    public void setOlcDbTimeout( String olcDbTimeout )
    {
        this.olcDbTimeout = olcDbTimeout;
    }


    /**
     * @param olcDbURI the olcDbURI to set
     */
    public void setOlcDbURI( String olcDbURI )
    {
        this.olcDbURI = olcDbURI;
    }


    /**
     * @param olcDbUseTemporaryConn the olcDbUseTemporaryConn to set
     */
    public void setOlcDbUseTemporaryConn( Boolean olcDbUseTemporaryConn )
    {
        this.olcDbUseTemporaryConn = olcDbUseTemporaryConn;
    }
}
