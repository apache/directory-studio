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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import java.util.Collection;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;


/**
 * The OpenBestEditorAction is used to edit a value with the best value editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenBestEditorAction extends AbstractOpenEditorAction
{

    /** The best value editor. */
    private IValueEditor bestValueEditor;


    /**
     * Creates a new instance of OpenBestEditorAction.
     * 
     * @param viewer the viewer
     * @param valueEditorManager the value editor manager
     * @param actionGroup the action group
     */
    public OpenBestEditorAction( TreeViewer viewer, ValueEditorManager valueEditorManager,
        EntryEditorWidgetActionGroup actionGroup )
    {
        super( viewer, valueEditorManager, actionGroup );
    }


    /**
     * Gets the best value editor.
     * 
     * @return the best value editor
     */
    public IValueEditor getBestValueEditor()
    {
        return this.bestValueEditor;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        bestValueEditor = null;
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return isEnabled() ? bestValueEditor.getValueEditorImageDescriptor() : null;
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( isEnabled() )
        {
            return bestValueEditor.getValueEditorName();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( getSelectedValues().length == 1
            && getSelectedAttributes().length == 0
            && viewer.getCellModifier().canModify( getSelectedValues()[0],
                EntryEditorWidgetTableMetadata.VALUE_COLUMN_NAME ) )
        {
            // update value editor
            bestValueEditor = valueEditorManager.getCurrentValueEditor( getSelectedValues()[0] );
            setCellEditor( bestValueEditor.getCellEditor() );

            return true;
        }
        else
        {
            bestValueEditor = null;
            return false;
        }
    }


    @Override
    public void run()
    {
        boolean ok = true;

        if ( ( getSelectedValues().length == 1 ) && ( getSelectedAttributes().length == 0 ) )
        {
            IValue value = getSelectedValues()[0];
            StringBuffer message = new StringBuffer();
            IAttribute attribute = value.getAttribute();
            String description = attribute.getDescription();
            AttributeType atd = attribute.getAttributeTypeDescription();

            if ( value.isEmpty() )
            {
                // validate single-valued attributes
                if ( ( attribute.getValueSize() > 1 ) && atd.isSingleValued() )
                {
                    message.append( NLS.bind( Messages.getString( "OpenBestEditorAction.ValueSingleValued" ), description ) );//$NON-NLS-1$
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                }

                // validate if value is allowed
                IEntry entry = attribute.getEntry();
                Collection<AttributeType> allAtds = SchemaUtils.getAllAttributeTypeDescriptions( entry );
                
                if ( !allAtds.contains( atd ) )
                {
                    message.append( NLS.bind( Messages.getString( "OpenBestEditorAction.AttributeNotInSubSchema" ), description ) );//$NON-NLS-1$
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }

            // validate non-modifiable attributes
            if ( !SchemaUtils.isModifiable( atd ) )
            {
                message.append( NLS.bind( Messages.getString( "OpenBestEditorAction.ValueNotModifiable" ), description ) );//$NON-NLS-1$ 
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            // validate modification of Rdn
            if ( value.isRdnPart() && ( getCellEditor() != valueEditorManager.getRenameValueEditor() ) )
            {
                message.append( NLS.bind( Messages.getString( "OpenBestEditorAction.ValueIsRdnPart" ), description ) );//$NON-NLS-1$ 
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
                message.append( BrowserCoreConstants.LINE_SEPARATOR );
            }

            if ( message.length() > 0 )
            {
                if ( value.isEmpty() )
                {
                    message.append( Messages.getString( "OpenBestEditorAction.NewValueQuestion" ) ); //$NON-NLS-1$
                }
                else
                {
                    message.append( Messages.getString( "OpenBestEditorAction.EditValueQuestion" ) ); //$NON-NLS-1$
                }
                
                ok = MessageDialog.openConfirm( getShell(), getText(), message.toString() );
            }

            if ( ok )
            {
                super.run();
            }
            else
            {
                if ( value.isEmpty() )
                {
                    attribute.deleteEmptyValue();
                }
            }
        }
    }
}
