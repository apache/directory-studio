/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.editor;


import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractDialogStringValueEditor;
import org.eclipse.swt.widgets.Shell;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.dialogs.OpenLdapAclDialog;


/**
 * This class implements a value editor that handle OpenLDAP ACL string values
 * in a dialog. 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclValueEditor extends AbstractDialogStringValueEditor
{
    /** The pattern used to match a precedence prefix ("{int}") */
    private static final String PRECEDENCE_PATTERN = "^\\{[0-9]+\\}.*$";


    /**
     * {@inheritDoc}
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();

        if ( ( value != null ) && ( value instanceof OpenLdapAclValueWithContext ) )
        {
            OpenLdapAclValueWithContext context = ( OpenLdapAclValueWithContext ) value;

            OpenLdapAclDialog dialog = new OpenLdapAclDialog( shell, context );
            
            if ( ( dialog.open() == OpenLdapAclDialog.OK)  && !"".equals( dialog.getAclValue() ) ) //$NON-NLS-1$
            {
                if ( dialog.isHasPrecedence() )
                {
                    String aclValue = "{" + dialog.getPrecedenceValue() + "}" + dialog.getAclValue();
                    setValue( aclValue );
                }
                else
                {
                    String aclValue = dialog.getAclValue();
                    setValue( aclValue );
                }

                return true;
            }
        }
        
        return false;
    }


    /**
     * Returns a ACIItemValueContext with the connection
     * and entry of the attribute hierarchy and an empty value if there
     * are no values in attributeHierarchy.
     * 
     * Returns a ACIItemValueContext with the connection
     * and entry of the attribute hierarchy and a value if there is
     * one value in attributeHierarchy.
     * 
     * @param attributeHierarchy the attribute hierarchy
     * 
     * @return the raw value
     */
    public Object getRawValue( AttributeHierarchy attributeHierarchy )
    {
        if ( attributeHierarchy == null )
        {
            return null;
        }
        else if ( ( attributeHierarchy.size() == 1 ) && ( attributeHierarchy.getAttribute().getValueSize() == 0 ) )
        {
            IEntry entry = attributeHierarchy.getAttribute().getEntry();
            IBrowserConnection connection = entry.getBrowserConnection();
            return new OpenLdapAclValueWithContext( connection, entry, false, -1, "" ); //$NON-NLS-1$
        }
        else if ( ( attributeHierarchy.size() == 1 ) && ( attributeHierarchy.getAttribute().getValueSize() == 1 ) )
        {
            IEntry entry = attributeHierarchy.getAttribute().getEntry();
            IBrowserConnection connection = entry.getBrowserConnection();
            String value = getDisplayValue( attributeHierarchy );
            int precedence = getPrecedence( value );
            boolean hasPrecedence = ( precedence != -1 );
            String aclValue = getStrippedAclValue( value );
            return new OpenLdapAclValueWithContext( connection, entry, hasPrecedence, precedence, aclValue );
        }
        else
        {
            return null;
        }
    }


    /**
     * Returns a ACIItemValueContext with the connection,
     * entry and string value of the given value.
     * 
     * @param value the value
     * 
     * @return the raw value
     */
    public Object getRawValue( IValue value )
    {
        Object o = super.getRawValue( value );
        
        if ( o instanceof String )
        {
            IEntry entry = value.getAttribute().getEntry();
            IBrowserConnection connection = entry.getBrowserConnection();
            String v = ( String ) o;
            int precedence = getPrecedence( v );
            boolean hasPrecedence = ( precedence != -1 );
            String aclValue = getStrippedAclValue( v );
            
            return new OpenLdapAclValueWithContext( connection, entry, hasPrecedence, precedence, aclValue );
        }

        return null;
    }


    /**
     * Gets the precedence value (or -1 if none is found).
     *
     * @param s the string
     * @return the precedence value (or -1 if none is found).
     */
    public int getPrecedence( String s )
    {
        // Checking if the acl contains precedence information ("{int}")
        if ( Strings.isCharASCII( s, 0, '{' ) )
        {
            int precedence = 0;
            int pos = 1;
            
            while ( pos < s.length() )
            {
                char c = s.charAt( pos );
                
                if ( c == '}' )
                {
                    if ( pos == 1 )
                    {
                        return -1;
                    }
                    else
                    {
                        return precedence;
                    }
                }
                
                if ( ( c >= '0' ) && ( c <= '9' ) )
                {
                    precedence = precedence*10 + ( c - '0' );
                }
                
                pos++;
            }
            
            return -1;
        }
        else
        {
            return -1;
        }
    }


    /**
     * Gets the stripped ACL value (without precedence).
     *
     * @param s the string
     * @return the stripped ACL value (without precedence).
     */
    public String getStrippedAclValue( String s )
    {
        // Checking if the acl contains precedence information ("{int}")
        if ( s.matches( PRECEDENCE_PATTERN ) )
        {
            // Getting the index of the closing curly bracket
            int indexOfClosingCurlyBracket = s.indexOf( '}' );

            // Returning the ACL value
            return s.substring( indexOfClosingCurlyBracket + 1 );
        }

        return s;
    }
}
