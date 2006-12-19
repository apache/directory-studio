/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.ldapstudio.browser.ui.valueproviders;


import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.jobs.CreateValuesJob;
import org.apache.directory.ldapstudio.browser.core.jobs.DeleteAttributesValueJob;
import org.apache.directory.ldapstudio.browser.core.jobs.ModifyValueJob;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierachie;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class TextValueProvider extends AbstractDialogCellEditor implements ValueProvider, ModelModifier
{

    public TextValueProvider( Composite parent )
    {
        super( parent );
    }


    public CellEditor getCellEditor()
    {
        return this;
    }


    public String getCellEditorName()
    {
        return "Text Editor";
    }


    public ImageDescriptor getCellEditorImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_TEXTEDITOR );
    }


    public Object openDialogBox( Control cellEditorWindow )
    {
        Object value = getValue();
        if ( value != null && value instanceof String )
        {
            TextDialog dialog = new TextDialog( cellEditorWindow.getShell(), ( String ) value );
            if ( dialog.open() == TextDialog.OK && !"".equals( dialog.getText() ) )
            {
                return dialog.getText();
            }
        }
        return null;
    }


    public String getDisplayValue( AttributeHierachie ah )
    {
        Object obj = this.getRawValue( ah );
        return obj == null ? "NULL" : obj.toString();
    }


    public String getDisplayValue( IValue value )
    {
        Object obj = this.getRawValue( value );
        return obj == null ? "NULL" : obj.toString();
    }


    public Object getEmptyRawValue( IEntry entry, String attributeDescription )
    {
        if ( entry == null || attributeDescription == null )
        {
            return null;
        }
        if ( entry.getConnection().getSchema().getAttributeTypeDescription( attributeDescription )
            .getSyntaxDescription().isString() )
        {
            return IValue.EMPTY_STRING_VALUE;
        }
        else
        {
            // return LdifUtils.utf8decode(IValue.EMPTY_BINARY_VALUE);
            return IValue.EMPTY_BINARY_VALUE;
        }
    }


    public Object getRawValue( AttributeHierachie ah )
    {
        if ( ah == null )
        {
            return null;
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 0 )
        {
            return getEmptyRawValue( ah.getAttribute().getEntry(), ah.getAttribute().getDescription() );
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {
            return getRawValue( ah.getAttribute().getValues()[0] );
        }
        else
        {
            return null;
        }
    }


    public Object getRawValue( IValue value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value.isString() )
        {
            return value.getStringValue();
        }
        else if ( value.isBinary() )
        {
            return isEditable( value.getBinaryValue() ) ? value.getStringValue() : null;
        }
        else
        {
            return null;
        }
    }


    public Object getRawValue( IConnection connection, Schema schema, Object value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value instanceof String )
        {
            return value;
        }
        else if ( value instanceof byte[] )
        {
            String s = LdifUtils.utf8decode( ( byte[] ) value );
            for ( int i = 0; i < s.length(); i++ )
            {
                if ( Character.isISOControl( s.charAt( i ) ) && s.charAt( i ) != '\n' && s.charAt( i ) != '\r' )
                {
                    return null;
                }
            }
            return s;
            // return isEditable((byte[])value) ?
            // LdifUtils.utf8decode((byte[])this.value) : null;
        }
        else
        {
            return null;
        }
    }


    private boolean isEditable( byte[] b )
    {

        if ( b == null )
        {
            return false;
        }

        for ( int i = 0; i < b.length; i++ )
        {
            if ( !( b[i] == '\n' || b[i] == '\r' || ( b[i] >= '\u0020' && b[i] <= '\u007F' ) ) )
            {
                return false;
            }
        }

        return true;
    }


    public void create( IEntry entry, String attributeDescription, Object newRawValue )
        throws ModelModificationException
    {
        if ( entry != null && attributeDescription != null && newRawValue != null && newRawValue instanceof String )
        {
            if ( entry.getAttribute( attributeDescription ) != null )
            {
                this.modify( entry.getAttribute( attributeDescription ), newRawValue );
            }
            else
            {
                EventRegistry.suspendEventFireingInCurrentThread();
                IAttribute attribute = new Attribute( entry, attributeDescription );
                entry.addAttribute( attribute, this );
                EventRegistry.resumeEventFireingInCurrentThread();

                Object newValue;
                if ( entry.getConnection().getSchema().getAttributeTypeDescription( attributeDescription )
                    .getSyntaxDescription().isString() )
                {
                    newValue = ( String ) newRawValue;
                }
                else
                {
                    newValue = LdifUtils.utf8encode( ( String ) newRawValue );
                }

                new CreateValuesJob( attribute, newValue ).execute();
            }
        }
    }


    private void modify( IAttribute attribute, Object newRawValue ) throws ModelModificationException
    {
        if ( attribute != null && newRawValue != null && newRawValue instanceof String )
        {
            if ( attribute.getValueSize() == 0 )
            {
                String newValue = ( String ) newRawValue;
                new CreateValuesJob( attribute, newValue ).execute();
            }
            else if ( attribute.getValueSize() == 1 )
            {
                this.modify( attribute.getValues()[0], newRawValue );
            }
        }
    }


    public void modify( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
        if ( oldValue != null && newRawValue != null && newRawValue instanceof String )
        {

            String newValue = ( String ) newRawValue;
            IAttribute attribute = oldValue.getAttribute();
            if ( !oldValue.getStringValue().equals( newValue ) )
            {
                if ( oldValue.isEmpty() )
                {
                    EventRegistry.suspendEventFireingInCurrentThread();
                    attribute.deleteEmptyValue( this );
                    EventRegistry.resumeEventFireingInCurrentThread();
                    new CreateValuesJob( attribute, newValue ).execute();
                }
                else
                {
                    new ModifyValueJob( attribute, oldValue, newValue ).execute();
                }
            }
        }
    }


    public void delete( AttributeHierachie ah ) throws ModelModificationException
    {
        if ( ah != null )
        {
            new DeleteAttributesValueJob( ah ).execute();
        }
    }


    public void delete( IValue oldValue ) throws ModelModificationException
    {
        if ( oldValue != null )
        {
            new DeleteAttributesValueJob( oldValue ).execute();
        }
    }

}
