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
    /**
     * {@inheritDoc}
     */
    protected boolean openDialog( Shell shell )
    {
        Object value = getValue();

        if ( value instanceof OpenLdapAclValueWithContext )
        {
            OpenLdapAclValueWithContext context = ( OpenLdapAclValueWithContext ) value;

            OpenLdapAclDialog dialog = new OpenLdapAclDialog( shell, context );
            
            if ( ( dialog.open() == OpenLdapAclDialog.OK ) && !"".equals( dialog.getAclValue() ) ) //$NON-NLS-1$
            {
                if ( dialog.hasPrecedence() )
                {
                    String aclValue = "{" + dialog.getPrecedence() + "}" + dialog.getAclValue();
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
        
        if ( ( attributeHierarchy.size() == 1 ) && ( attributeHierarchy.getAttribute().getValueSize() == 0 ) )
        {
            IEntry entry = attributeHierarchy.getAttribute().getEntry();
            IBrowserConnection connection = entry.getBrowserConnection();

            if ( attributeHierarchy.getAttribute().getValueSize() == 0 )
            {
                return new OpenLdapAclValueWithContext( connection, entry, -1, "" ); //$NON-NLS-1$
            }
            else if ( attributeHierarchy.getAttribute().getValueSize() == 1 )
            {
                String valueStr = getDisplayValue( attributeHierarchy );
                int precedence = getPrecedence( valueStr );
                String aclValue = valueStr;
                
                if ( precedence != -1 )
                {
                    aclValue = removePrecedence( valueStr );
                }

                return new OpenLdapAclValueWithContext( connection, entry, precedence, aclValue );
            }
        }

        return null;
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
        Object object = super.getRawValue( value );
        
        if ( object instanceof String )
        {
            IEntry entry = value.getAttribute().getEntry();
            IBrowserConnection connection = entry.getBrowserConnection();
            String valueStr = ( String ) object;
            int precedence = getPrecedence( valueStr );
            String aclValue = valueStr;
            
            if ( precedence != -1 )
            {
                aclValue = removePrecedence( valueStr );
            }
            
            return new OpenLdapAclValueWithContext( connection, entry, precedence, aclValue );
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
                else
                {
                    // Not a precedence
                    return -1;
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
     * Return the ACII value withoiut the precedence part
     * 
     * @param str The original ACII, with or without precedence
     * @return The ACII string minus the precedence part
     */
    public String removePrecedence( String str )
    {
        if ( Strings.isCharASCII( str, 0, '{' ) )
        {
            int pos = 1;
            
            while ( pos < str.length() )
            {
                char c = str.charAt( pos );
                
                if ( c == '}' )
                {
                    if ( pos == 1 )
                    {
                        // We just have {}, return the string
                        return str;
                    }
                    else
                    {
                        return str.substring( pos + 1 );
                    }
                }
                
                if ( ( c < '0' ) && ( c > '9' ) )
                {
                    // Not a number, get out
                    return str;
                }
                
                pos++;
            }
            
            return str;
        }
        else
        {
            return str;
        }
    }
}
