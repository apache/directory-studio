package org.apache.directory.studio.ldapbrowser.core.utils;


import java.util.Iterator;

import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.AttributeTypeAndValue;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;


/**
 * Utility class for LdapDN specific stuff.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DnUtils
{

    /**
     * Transforms the given DN into a normalized String, usable by the schema cache.
     * The following transformations are permformed:
     * <ul>
     *   <li>The attribute type is replaced by the OID
     *   <li>The attribute value is trimmed and lowercased
     * </ul> 
     * Example: the surname=Bar will be transformed to
     * 2.5.4.4=bar
     * 
     * 
     * @param dn the DN
     * @param schema the schema
     * 
     * @return the oid string
     */
    public static String getNormalizedOidString( LdapDN dn, Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        Iterator<Rdn> it = dn.getRdns().iterator();
        while ( it.hasNext() )
        {
            Rdn rdn = it.next();
            sb.append( getOidString( rdn, schema ) );
            if ( it.hasNext() )
            {
                sb.append( ',' );
            }
        }

        return sb.toString();
    }


    private static String getOidString( Rdn rdn, Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        Iterator<AttributeTypeAndValue> it = rdn.iterator();
        while ( it.hasNext() )
        {
            AttributeTypeAndValue atav = it.next();
            sb.append( getOidString( atav, schema ) );
            if ( it.hasNext() )
            {
                sb.append( '+' );
            }
        }

        return sb.toString();
    }


    private static String getOidString( AttributeTypeAndValue atav, Schema schema )
    {
        String oid = schema != null ? schema.getAttributeTypeDescription( atav.getNormType() ).getNumericOID() : atav
            .getNormType();
        return oid.trim().toLowerCase() + "=" + ( ( String ) atav.getUpValue() ).trim().toLowerCase(); //$NON-NLS-1$
    }


    /**
     * Composes an DN based on the given RDN and DN.
     * 
     * @param rdn the RDN
     * @param parent the parent DN
     * 
     * @return the composed DN
     */
    public static LdapDN composeDn( Rdn rdn, LdapDN parent )
    {
        LdapDN ldapDn = ( LdapDN ) parent.clone();
        ldapDn.add( ( Rdn ) rdn.clone() );
        return ldapDn;
    }


    /**
     * Gets the parent DN of the given DN or null if the given 
     * DN hasn't a parent.
     * 
     * @param dn the DN
     * 
     * @return the parent DN, null if the given DN hasn't a parent
     */
    public static LdapDN getParent( LdapDN dn )
    {
        if ( dn.size() < 1 )
        {
            return null;
        }
        else
        {
            LdapDN parent = ( LdapDN ) dn.getPrefix( dn.size() - 1 );
            return parent;
        }
    }


    /**
     * Compose an DN based on the given RDN and DN.
     * 
     * @param rdn the RDN
     * @param parent the parent DN
     * 
     * @return the composed RDN
     * 
     * @throws InvalidNameException the invalid name exception
     */
    public static LdapDN composeDn( String rdn, String parent ) throws InvalidNameException
    {
        return composeDn( new Rdn( rdn ), new LdapDN( parent ) );
    }


    /**
     * Composes an DN based on the given prefix and suffix.
     * 
     * @param prefix the prefix
     * @param suffix the suffix
     * 
     * @return the composed DN
     */
    public static LdapDN composeDn( LdapDN prefix, LdapDN suffix )
    {
        LdapDN ldapDn = ( LdapDN ) suffix.clone();
        
        for ( Rdn rdn : prefix.getRdns() )
        {
            ldapDn.add( ( Rdn ) rdn.clone() );
        }
        
        return ldapDn;
    }
    
    /**
     * Gets the prefix, cuts the suffix from the given DN.
     * 
     * @param dn the DN
     * @param suffix the suffix
     * 
     * @return the prefix
     */
    public static LdapDN getPrefixName( LdapDN dn, LdapDN suffix )
    {
        if ( suffix.size() < 1 )
        {
            return null;
        }
        else
        {
            LdapDN parent = ( LdapDN ) dn.getSuffix( suffix.size() - 1 );
            return parent;
        }
    }


    /**
     * Composes an RDN based on the given types and values.
     * 
     * @param rdnTypes the types
     * @param rdnValues the values
     * 
     * @return the RDN
     * 
     * @throws InvalidNameException the invalid name exception
     */
    public static Rdn composeRdn( String[] rdnTypes, String[] rdnValues ) throws InvalidNameException
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < rdnTypes.length; i++ )
        {
            if( i > 0 )
            {
                sb.append( '+' );
            }
            
            sb.append( rdnTypes[i] );
            sb.append( '=' );
            sb.append( Rdn.escapeValue( rdnValues[i] ) );
        }
        Rdn rdn = new Rdn( sb.toString() );
        return rdn;
    }

}
