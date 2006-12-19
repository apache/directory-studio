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


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.jobs.CreateValuesJob;
import org.apache.directory.ldapstudio.browser.core.jobs.ModifyValueJob;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierachie;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.ObjectClassDialog;
import org.apache.directory.ldapstudio.browser.ui.dialogs.TextDialog;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class ObjectClassValueProvider extends AbstractDialogCellEditor implements ValueProvider, ModelModifier
{

    public ObjectClassValueProvider( Composite parent )
    {
        super( parent );
    }


    public Object openDialogBox( Control cellEditorWindow )
    {
        Object value = getValue();
        if ( value != null && value instanceof ObjectClassValueProviderRawValue )
        {
            ObjectClassValueProviderRawValue rawValue = ( ObjectClassValueProviderRawValue ) value;
            ObjectClassDialog dialog = new ObjectClassDialog( cellEditorWindow.getShell(), rawValue.schema,
                rawValue.value );
            if ( dialog.open() == TextDialog.OK && !"".equals( dialog.getObjectClass() ) )
            {
                return dialog.getObjectClass();
            }
        }
        return null;
    }


    public String getDisplayValue( AttributeHierachie ah )
    {
        if ( ah == null )
        {
            return "NULL";
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {
            return getDisplayValue( ah.getAttribute().getValues()[0] );
        }
        else
        {
            return "not displayable";
        }
    }


    public String getDisplayValue( IValue value )
    {
        if ( value == null )
        {
            return "NULL";
        }
        else if ( value.isString() )
        {
            String ocName = value.getStringValue();
            if ( !BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES ) )
            {
                Schema schema = value.getAttribute().getEntry().getConnection().getSchema();
                ObjectClassDescription ocd = schema.getObjectClassDescription( ocName );
                if ( ocd.isStructural() )
                {
                    ocName = ocName + " (structural)";
                }
                else if ( ocd.isAbstract() )
                {
                    ocName = ocName + " (abstract)";
                }
                else if ( ocd.isAuxiliary() )
                {
                    ocName = ocName + " (auxiliary)";
                }
                else if ( ocd.isObsolete() )
                {
                    ocName = ocName + " (obsolete)";
                }
            }
            return ocName;
        }
        else
        {
            return "BINARY";
        }
    }


    public Object getEmptyRawValue( IEntry entry, String attributeName )
    {
        return null;
    }


    public Object getRawValue( AttributeHierachie ah )
    {
        return null;
    }


    public Object getRawValue( IValue value )
    {
        if ( value == null || !value.isString() || !value.getAttribute().isObjectClassAttribute() )
        {
            return null;
        }
        else
        {
            return getRawValue( value.getAttribute().getEntry().getConnection(), null, value.getStringValue() );
        }
    }


    public Object getRawValue( IConnection connection, Schema schema, Object value )
    {
        if ( schema == null && connection != null )
        {
            schema = connection.getSchema();
        }
        if ( schema == null || value == null || !( value instanceof String ) )
        {
            return null;
        }

        String ocValue = ( String ) value;
        ObjectClassValueProviderRawValue rawValue = new ObjectClassValueProviderRawValue( schema, ocValue );
        return rawValue;
    }


    public void create( IEntry entry, String attributeName, Object newRawValue ) throws ModelModificationException
    {
    }


    public void modify( IValue oldValue, Object newValue ) throws ModelModificationException
    {

        if ( oldValue.isString() && newValue instanceof String )
        {

            IEntry entry = oldValue.getAttribute().getEntry();
            String[] allOcNames = entry.getConnection().getSchema().getObjectClassDescriptionNames();

            if ( newValue != null && Arrays.asList( allOcNames ).contains( newValue ) )
            {
                String newObjectClassName = ( String ) newValue;
                IAttribute attribute = oldValue.getAttribute();
                if ( !oldValue.getStringValue().equals( newObjectClassName ) )
                {
                    if ( oldValue.isEmpty() )
                    {
                        attribute.deleteEmptyValue( this );
                        new CreateValuesJob( attribute, newObjectClassName ).execute();
                    }
                    else
                    {
                        new ModifyValueJob( attribute, oldValue, newObjectClassName ).execute();
                    }
                }

                if ( entry.getSubschema() != null )
                {
                    String[] must = entry.getSubschema().getMustAttributeNames();
                    for ( int i = 0; i < must.length; i++ )
                    {
                        if ( entry.getAttribute( must[i] ) == null )
                        {
                            IAttribute att = new Attribute( entry, must[i] );
                            entry.addAttribute( att, this );
                            att.addEmptyValue( this );
                        }
                    }
                }
            }
        }
    }


    public void delete( AttributeHierachie ah ) throws ModelModificationException
    {
    }


    public void delete( IValue oldValue ) throws ModelModificationException
    {
    }


    public CellEditor getCellEditor()
    {
        return this;
    }


    public String getCellEditorName()
    {
        return "Object Class Editor";
    }


    public ImageDescriptor getCellEditorImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_INPLACE_OCEDITOR );
    }

    class ObjectClassValueProviderRawValue
    {
        Schema schema;

        String value;


        public ObjectClassValueProviderRawValue( Schema schema, String value )
        {
            super();
            this.schema = schema;
            this.value = value;
        }
    }

}
