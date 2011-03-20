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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import java.util.Collection;

import org.apache.directory.shared.ldap.model.schema.MutableAttributeTypeImpl;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
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
     * @param cursor the cursor
     * @param valueEditorManager the value editor manager
     * @param actionGroup the action group
     */
    public OpenBestEditorAction( TableViewer viewer, SearchResultEditorCursor cursor,
        ValueEditorManager valueEditorManager, SearchResultEditorActionGroup actionGroup )
    {
        super( viewer, cursor, valueEditorManager, actionGroup );
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
        return isEnabled() ? bestValueEditor.getValueEditorName() : null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( getSelectedSearchResults().length == 1 && getSelectedProperties().length == 1
            && viewer.getCellModifier().canModify( getSelectedSearchResults()[0], getSelectedProperties()[0] ) )
        {
            if ( getSelectedAttributeHierarchies().length == 0 )
            {
                bestValueEditor = valueEditorManager.getCurrentValueEditor( getSelectedSearchResults()[0].getEntry(),
                    getSelectedProperties()[0] );
            }
            else
            {
                bestValueEditor = valueEditorManager.getCurrentValueEditor( getSelectedAttributeHierarchies()[0] );
            }

            super.cellEditor = bestValueEditor.getCellEditor();
            return true;
        }
        else
        {
            super.cellEditor = null;
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        boolean ok = true;

        // validate non-modifiable attributes
        AttributeHierarchy[] attributeHierarchies = getSelectedAttributeHierarchies();
        if ( attributeHierarchies.length == 1 )
        {
            AttributeHierarchy attributeHierarchy = attributeHierarchies[0];
            StringBuffer message = new StringBuffer();

            if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 0 )
            {
                // validate if value is allowed
                IEntry entry = attributeHierarchy.getAttribute().getEntry();
                Collection<MutableAttributeTypeImpl> allAtds = SchemaUtils.getAllAttributeTypeDescriptions( entry );
                MutableAttributeTypeImpl atd = attributeHierarchy.getAttribute().getAttributeTypeDescription();
                if ( !allAtds.contains( atd ) )
                {
                    message.append( NLS.bind( Messages.getString( "OpenBestEditorAction.AttributeNotInSubSchema" ), //$NON-NLS-1$
                        attributeHierarchy.getAttribute().getDescription() ) );
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }

            if ( attributeHierarchy.size() == 1
                && attributeHierarchy.getAttribute().getValueSize() == 1
                && attributeHierarchy.getAttributeDescription().equalsIgnoreCase(
                    attributeHierarchy.getAttribute().getValues()[0].getAttribute().getDescription() )
                && !attributeHierarchy.getAttribute().getValues()[0].isRdnPart() )
            {
                // validate non-modifiable attributes
                IValue value = attributeHierarchy.getAttribute().getValues()[0];
                if ( !value.isEmpty() && !SchemaUtils.isModifiable( value.getAttribute().getAttributeTypeDescription() ) )
                {
                    message
                        .append( NLS
                            .bind(
                                Messages.getString( "OpenBestEditorAction.EditValueNotModifiable" ), value.getAttribute().getDescription() ) ); //$NON-NLS-1$
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                    message.append( BrowserCoreConstants.LINE_SEPARATOR );
                }
            }

            if ( message.length() > 0 )
            {
                message.append( Messages.getString( "OpenBestEditorAction.EditValueQuestion" ) ); //$NON-NLS-1$
                ok = MessageDialog.openConfirm( getShell(), getText(), message.toString() );
            }
        }

        if ( ok )
        {
            super.run();
        }
    }

}
