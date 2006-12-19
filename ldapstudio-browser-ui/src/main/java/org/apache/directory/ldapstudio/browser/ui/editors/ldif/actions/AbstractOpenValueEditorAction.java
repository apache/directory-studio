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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif.actions;


import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifPart;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifControlLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifDeloldrdnLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifDnLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifNewrdnLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifNewsuperiorLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifValueLineBase;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifEditor;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.AbstractDialogCellEditor;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProviderManager;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;


public abstract class AbstractOpenValueEditorAction extends AbstractLdifAction
{

    protected ValueProviderManager manager;

    protected ValueProvider valueProvider;


    public AbstractOpenValueEditorAction( LdifEditor editor )
    {
        super( "Edit Value", editor );
        manager = editor.getValueProviderManager();
    }


    public Object getValueProvider()
    {
        return valueProvider;
    }


    protected void doRun()
    {

        LdifPart[] parts = getSelectedLdifParts();
        if ( parts.length == 1 && ( parts[0] instanceof LdifValueLineBase ) )
        {
            LdifValueLineBase line = ( LdifValueLineBase ) parts[0];

            String attributeDescription = getAttributeDescription();
            Object rawValue = getValueProviderRawValue();

            if ( valueProvider instanceof AbstractDialogCellEditor )
            {
                AbstractDialogCellEditor cellEditor = ( AbstractDialogCellEditor ) valueProvider;
                cellEditor.setValue( rawValue );
                cellEditor.activate();
                Object newValue = cellEditor.getValue();

                if ( newValue != null && newValue instanceof String || newValue instanceof byte[] )
                {
                    IDocument document = editor.getDocumentProvider().getDocument( editor.getEditorInput() );

                    LdifValueLineBase newLine;
                    if ( line instanceof LdifControlLine )
                    {
                        LdifControlLine oldControlLine = ( LdifControlLine ) line;
                        if ( newValue instanceof String )
                        {
                            newLine = LdifControlLine.create( oldControlLine.getUnfoldedOid(), oldControlLine
                                .getUnfoldedCriticality(), ( String ) newValue );
                        }
                        else
                        {
                            newLine = LdifControlLine.create( oldControlLine.getUnfoldedOid(), oldControlLine
                                .getUnfoldedCriticality(), ( byte[] ) newValue );
                        }
                    }
                    else
                    {
                        if ( newValue instanceof String )
                        {
                            newLine = LdifAttrValLine.create( attributeDescription, ( String ) newValue );
                        }
                        else
                        {
                            newLine = LdifAttrValLine.create( attributeDescription, ( byte[] ) newValue );
                        }
                    }

                    try
                    {
                        document.replace( line.getOffset(), line.getLength(), newLine.toFormattedString() );
                    }
                    catch ( BadLocationException e )
                    {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    protected Schema getSchema()
    {
        return editor.getConnection() != null ? editor.getConnection().getSchema() : Schema.DEFAULT_SCHEMA;
    }


    protected Object getValueProviderRawValue()
    {
        Object rawValue = null;
        Object value = getValue();
        if ( value != null )
        {
            Schema schema = getSchema();
            rawValue = valueProvider.getRawValue( null, schema, value );
        }

        return rawValue;
    }


    protected Object getValue()
    {
        LdifPart[] parts = getSelectedLdifParts();
        Object oldValue = null;
        if ( parts.length == 1 && ( parts[0] instanceof LdifValueLineBase ) )
        {
            LdifValueLineBase line = ( LdifValueLineBase ) parts[0];
            oldValue = line.getValueAsObject();

            if ( line instanceof LdifControlLine )
            {
                oldValue = ( ( LdifControlLine ) line ).getUnfoldedControlValue();
            }

        }
        return oldValue;
    }


    protected String getAttributeDescription()
    {
        String attributeDescription = null;
        LdifPart[] parts = getSelectedLdifParts();
        if ( parts.length == 1 && ( parts[0] instanceof LdifValueLineBase ) )
        {
            LdifValueLineBase line = ( LdifValueLineBase ) parts[0];

            if ( line instanceof LdifControlLine )
            {
                attributeDescription = "";
            }
            else
            {
                attributeDescription = line.getUnfoldedLineStart();
            }
        }
        return attributeDescription;
    }


    protected boolean isEditableLineSelected()
    {
        LdifPart[] parts = getSelectedLdifParts();
        boolean b = parts.length == 1
            && ( parts[0] instanceof LdifAttrValLine || parts[0] instanceof LdifDnLine
                || parts[0] instanceof LdifControlLine || parts[0] instanceof LdifNewrdnLine
                || parts[0] instanceof LdifDeloldrdnLine || parts[0] instanceof LdifNewsuperiorLine );
        return b;
    }

}
