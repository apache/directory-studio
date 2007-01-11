package org.apache.directory.ldapstudio.dsmlv2.request;


import org.apache.directory.ldapstudio.dsmlv2.DsmlDecorator;
import org.apache.directory.ldapstudio.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.dom4j.Element;


public abstract class AbstractRequestDsml extends LdapRequestDecorator implements DsmlDecorator
{
    /**
     * Creates a new instance of AbstractRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public AbstractRequestDsml( LdapMessage ldapMessage )
    {
        super( ldapMessage );
    }


    /**
     * Creates the Request Element and adds RequestID and Controls.
     *
     * @param root
     *      the root element
     * @return
     *      the Request Element of the given name containing
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( getRequestName() );
        
        // Request ID
        int requestID = instance.getMessageId();
        if ( requestID != 0 )
        {
            element.addAttribute( "requestID", "" + requestID );
        }

        // Controls
        ParserUtils.addControls( element, instance.getControls() );
        
        return element;
    }
    
    /**
     * Gets the name of the request according to the type of the decorated element.
     *
     * @return
     *      the name of the request according to the type of the decorated element.
     */
    private String getRequestName()
    {
        switch ( instance.getMessageType() )
        {
            case LdapConstants.ABANDON_REQUEST:
                return "abandonRequest";
            case LdapConstants.ADD_REQUEST:
                return "addRequest";
            case LdapConstants.BIND_REQUEST:
                return "authRequest";
            case LdapConstants.COMPARE_REQUEST:
                return "compareRequest";
            case LdapConstants.DEL_REQUEST:
                return "delRequest";
            case LdapConstants.EXTENDED_REQUEST:
                return "extendedRequest";
            case LdapConstants.MODIFYDN_REQUEST:
                return "modDNRequest";
            case LdapConstants.MODIFY_REQUEST:
                return "modifyRequest";
            case LdapConstants.SEARCH_REQUEST:
                return "searchRequest";
            default:
                return "error";
        }
    }
}
