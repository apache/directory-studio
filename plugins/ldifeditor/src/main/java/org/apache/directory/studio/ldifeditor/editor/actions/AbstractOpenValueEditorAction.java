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

package org.apache.directory.studio.ldifeditor.editor.actions;


import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifeditor.editor.LdifEditor;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDeloldrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewsuperiorLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifValueLineBase;
import org.apache.directory.studio.valueeditors.AbstractDialogValueEditor;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;


public abstract class AbstractOpenValueEditorAction extends AbstractLdifAction
{

    protected ValueEditorManager valueEditorManager;

    protected IValueEditor valueEditor;


    public AbstractOpenValueEditorAction( LdifEditor editor )
    {
        super( Messages.getString( "AbstractOpenValueEditorAction.EditValue" ), editor ); //$NON-NLS-1$
        valueEditorManager = editor.getValueEditorManager();
    }


    public Object getValueEditor()
    {
        return valueEditor;
    }


    protected void doRun()
    {

        LdifPart[] parts = getSelectedLdifParts();
        if ( parts.length == 1 && ( parts[0] instanceof LdifValueLineBase ) )
        {
            LdifValueLineBase line = ( LdifValueLineBase ) parts[0];

            String attributeDescription = getAttributeDescription();
            Object rawValue = getValueEditorRawValue();

            if ( valueEditor instanceof AbstractDialogValueEditor )
            {
                AbstractDialogValueEditor cellEditor = ( AbstractDialogValueEditor ) valueEditor;
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
                        document.replace( line.getOffset(), line.getLength(), newLine.toFormattedString( Utils
                            .getLdifFormatParameters() ) );
                    }
                    catch ( BadLocationException e )
                    {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    protected IBrowserConnection getConnection()
    {
        return editor.getConnection() != null ? editor.getConnection() : new DummyConnection( Schema.DEFAULT_SCHEMA );
    }


    protected Object getValueEditorRawValue()
    {
        IBrowserConnection connection = getConnection();
        String dn = getDn();
        String description = getAttributeDescription();
        Object value = getValue();

        Object rawValue = null;
        if ( value != null )
        {
            try
            {
                // some value editors need the real DN (e.g. the password editor)
                DummyEntry dummyEntry = new DummyEntry( DN.isValid( dn ) ? new DN( dn ) : new DN(),
                    connection );
                Attribute dummyAttribute = new Attribute( dummyEntry, description );
                Value dummyValue = new Value( dummyAttribute, value );
                rawValue = valueEditor.getRawValue( dummyValue );
            }
            catch ( LdapInvalidDnException e )
            {
                // should not occur, as we check with isValid()
            }
        }

        return rawValue;
    }


    protected String getDn()
    {
        LdifContainer[] selectedLdifContainers = getSelectedLdifContainers();
        String dn = null;
        if ( selectedLdifContainers.length == 1 && selectedLdifContainers[0] instanceof LdifRecord )
        {
            LdifRecord record = ( LdifRecord ) selectedLdifContainers[0];
            LdifDnLine dnLine = record.getDnLine();
            dn = dnLine.getValueAsString();
        }
        return dn;
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
                attributeDescription = ""; //$NON-NLS-1$
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
