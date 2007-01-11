package org.apache.directory.ldapstudio.dsmlv2.request;


import java.util.List;

import org.apache.directory.ldapstudio.dsmlv2.DsmlDecorator;
import org.apache.directory.ldapstudio.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


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
        // TODO Auto-generated constructor stub
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
        List<Control> controls = instance.getControls();
        
        if ( controls != null )
        {
            for ( int i = 0; i < controls.size(); i++ )
            {
                Control control = controls.get( i );
                
                Element controlElement = element.addElement( "control" );
                
                if ( control.getControlType() != null )
                {
                    controlElement.addAttribute( "type", control.getControlType() );
                }
                
                if ( control.getCriticality() )
                {
                    controlElement.addAttribute( "criticality", "true" );
                }
                
                Object value = control.getControlValue();
                if ( value != null )
                {
                    if ( ParserUtils.needsBase64Encoding( value ) )
                    {
                        Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
                        Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
                        element.getDocument().getRootElement().add( xsdNamespace );
                        element.getDocument().getRootElement().add( xsiNamespace );
    
                        Element valueElement = 
                            controlElement.addElement( "controlValue" ).addText(  ParserUtils.base64Encode( value ) );
                        valueElement
                            .addAttribute( new QName( "type", xsiNamespace ), "xsd:" + ParserUtils.BASE64BINARY );
                    }
                    else
                    {
                        controlElement.addElement( "controlValue" ).setText( (String)  value );
                    }
                }
            }
        }
        
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
