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


import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.wizards.AttributeWizard;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifeditor.editor.LdifEditor;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifValueLineBase;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;


public class EditLdifAttributeAction extends AbstractLdifAction
{

    public EditLdifAttributeAction( LdifEditor editor )
    {
        super( Messages.getString( "EditLdifAttributeAction.EditAttributeDescription" ), editor ); //$NON-NLS-1$
        super.setActionDefinitionId( BrowserCommonConstants.ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION );
    }


    public void update()
    {
        LdifContainer[] containers = getSelectedLdifContainers();
        LdifModSpec modSpec = getSelectedLdifModSpec();
        LdifPart[] parts = getSelectedLdifParts();

        super
            .setEnabled( parts.length == 1
                && ( parts[0] instanceof LdifAttrValLine || modSpec != null )
                && containers.length == 1
                && ( containers[0] instanceof LdifContentRecord || containers[0] instanceof LdifChangeAddRecord || containers[0] instanceof LdifChangeModifyRecord ) );
    }


    protected void doRun()
    {

        LdifContainer[] containers = getSelectedLdifContainers();
        LdifModSpec modSpec = getSelectedLdifModSpec();
        LdifPart[] parts = getSelectedLdifParts();
        if ( parts.length == 1
            && ( parts[0] instanceof LdifAttrValLine || parts[0] instanceof LdifModSpecTypeLine )
            && containers.length == 1
            && ( containers[0] instanceof LdifContentRecord || containers[0] instanceof LdifChangeAddRecord || containers[0] instanceof LdifChangeModifyRecord ) )
        {
            try
            {
                LdifValueLineBase line = ( LdifValueLineBase ) parts[0];
                String attributeDescription = null;
                String oldValue = null;
                if ( modSpec != null && line instanceof LdifModSpecTypeLine )
                {
                    LdifModSpecTypeLine oldLine = ( LdifModSpecTypeLine ) line;
                    attributeDescription = oldLine.getUnfoldedAttributeDescription();
                    oldValue = null;
                }
                else
                {
                    LdifAttrValLine oldLine = ( LdifAttrValLine ) line;
                    attributeDescription = oldLine.getUnfoldedAttributeDescription();
                    oldValue = oldLine.getValueAsString();
                }

                Schema schema = editor.getConnection() != null ? editor.getConnection().getSchema()
                    : Schema.DEFAULT_SCHEMA;
                IBrowserConnection dummyConnection = new DummyConnection( schema );

                IEntry dummyEntry = null;
                if ( containers[0] instanceof LdifContentRecord )
                {
                    dummyEntry = ModelConverter.ldifContentRecordToEntry( ( LdifContentRecord ) containers[0],
                        dummyConnection );
                }
                else if ( containers[0] instanceof LdifChangeAddRecord )
                {
                    dummyEntry = ModelConverter.ldifChangeAddRecordToEntry( ( LdifChangeAddRecord ) containers[0],
                        dummyConnection );
                }
                else if ( containers[0] instanceof LdifChangeModifyRecord )
                {
                    dummyEntry = new DummyEntry( new Dn(), dummyConnection );
                }

                AttributeWizard wizard = new AttributeWizard( Messages
                    .getString( "EditLdifAttributeAction.EditAttributeDescription" ), true, false, //$NON-NLS-1$
                    attributeDescription, dummyEntry );
                WizardDialog dialog = new WizardDialog( Display.getDefault().getActiveShell(), wizard );
                dialog.setBlockOnOpen( true );
                dialog.create();
                if ( dialog.open() == Dialog.OK )
                {
                    String newAttributeDescription = wizard.getAttributeDescription();

                    if ( newAttributeDescription != null )
                    {
                        IDocument document = editor.getDocumentProvider().getDocument( editor.getEditorInput() );

                        if ( modSpec != null )
                        {
                            LdifModSpecTypeLine oldTypeLine = modSpec.getModSpecType();
                            LdifModSpecTypeLine newTypeLine = null;
                            if ( oldTypeLine.isAdd() )
                            {
                                newTypeLine = LdifModSpecTypeLine.createAdd( newAttributeDescription );
                            }
                            else if ( oldTypeLine.isDelete() )
                            {
                                newTypeLine = LdifModSpecTypeLine.createDelete( newAttributeDescription );
                            }
                            else if ( oldTypeLine.isReplace() )
                            {
                                newTypeLine = LdifModSpecTypeLine.createReplace( newAttributeDescription );
                            }

                            LdifAttrValLine[] oldAttrValLines = modSpec.getAttrVals();
                            LdifAttrValLine[] newAttrValLines = new LdifAttrValLine[oldAttrValLines.length];
                            for ( int i = 0; i < oldAttrValLines.length; i++ )
                            {
                                LdifAttrValLine oldAttrValLine = oldAttrValLines[i];
                                newAttrValLines[i] = LdifAttrValLine.create( newAttributeDescription, oldAttrValLine
                                    .getValueAsString() );

                            }

                            LdifModSpecSepLine newSepLine = LdifModSpecSepLine.create();

                            String text = newTypeLine.toFormattedString( Utils.getLdifFormatParameters() );
                            for ( int j = 0; j < newAttrValLines.length; j++ )
                            {
                                text += newAttrValLines[j].toFormattedString( Utils.getLdifFormatParameters() );
                            }
                            text += newSepLine.toFormattedString( Utils.getLdifFormatParameters() );
                            try
                            {
                                document.replace( modSpec.getOffset(), modSpec.getLength(), text );
                            }
                            catch ( BadLocationException e )
                            {
                                e.printStackTrace();
                            }

                        }
                        else
                        { // LdifContentRecord ||
                          // LdifChangeAddRecord
                            LdifAttrValLine newLine = LdifAttrValLine.create( newAttributeDescription, oldValue );
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

                    // ...
                }
            }
            catch ( LdapInvalidDnException e )
            {
            }
        }
    }

}
