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
package org.apache.directory.studio.openldap.config.editor.dialogs.overlays;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.common.ui.dialogs.AttributeDialog;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.dialogs.AbstractOverlayDialogConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.model.overlay.OlcRefintConfig;


/**
 * This class implements a block for the configuration of the Referential Integrity overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReferentialIntegrityOverlayConfigurationBlock extends
    AbstractOverlayDialogConfigurationBlock<OlcRefintConfig>
{
    /** The default modifier name */
    private static final String DEFAULT_MODIFIER_NAME = "cn=Referential Integrity Overlay";

    /** The attributes list */
    private List<String> attributes = new ArrayList<String>();

    // UI widgets
    private TableViewer attributesTableViewer;
    private Button addAttributeButton;
    private Button deleteAttributeButton;
    private EntryWidget placeholderValueEntryWidget;
    private EntryWidget modifierNameEntryWidget;

    // Listeners
    private ISelectionChangedListener attributesTableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            deleteAttributeButton.setEnabled( !attributesTableViewer.getSelection().isEmpty() );
        }
    };
    private SelectionListener addAttributeButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeDialog dialog = new AttributeDialog( addAttributeButton.getShell(), browserConnection );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String attribute = dialog.getAttribute();

                if ( !attributes.contains( attribute ) )
                {
                    attributes.add( attribute );
                    attributesTableViewer.refresh();
                    attributesTableViewer.setSelection( new StructuredSelection( attribute ) );
                }
            }
        }
    };
    private SelectionListener deleteAttributeButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) attributesTableViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                String selectedAttribute = ( String ) selection.getFirstElement();

                attributes.remove( selectedAttribute );
                attributesTableViewer.refresh();
            }
        }
    };


    public ReferentialIntegrityOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection connection )
    {
        super( dialog, connection );
        setOverlay( new OlcRefintConfig() );
    }


    public ReferentialIntegrityOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection connection,
        OlcRefintConfig overlay )
    {
        super( dialog, connection );
        if ( overlay == null )
        {
            overlay = new OlcRefintConfig();
        }

        setOverlay( overlay );
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        // Attributes
        BaseWidgetUtils.createLabel( composite, "Attributes:", 1 );
        Composite attributesComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        // Attributes TableViewer
        attributesTableViewer = new TableViewer( attributesComposite );
        GridData tableViewerGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        tableViewerGridData.heightHint = 20;
        tableViewerGridData.widthHint = 100;
        attributesTableViewer.getControl().setLayoutData( tableViewerGridData );
        attributesTableViewer.setContentProvider( new ArrayContentProvider() );
        attributesTableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_ATTRIBUTE );
            }
        } );
        attributesTableViewer.setInput( attributes );
        attributesTableViewer.addSelectionChangedListener( attributesTableViewerSelectionChangedListener );

        // Attribute Add Button
        addAttributeButton = BaseWidgetUtils.createButton( attributesComposite, "Add...", 1 );
        addAttributeButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        addAttributeButton.addSelectionListener( addAttributeButtonSelectionListener );

        // Attribute Delete Button
        deleteAttributeButton = BaseWidgetUtils.createButton( attributesComposite, "Delete", 1 );
        deleteAttributeButton.setEnabled( false );
        deleteAttributeButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteAttributeButton.addSelectionListener( deleteAttributeButtonSelectionListener );

        // Placeholder Value
        BaseWidgetUtils.createLabel( composite, "Placeholder Value:", 1 );
        placeholderValueEntryWidget = new EntryWidget( getDialog().getBrowserConnection() );
        placeholderValueEntryWidget.createWidget( composite );
        placeholderValueEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Modifier Name
        BaseWidgetUtils.createLabel( composite, "Modifier's Name:", 1 );
        modifierNameEntryWidget = new EntryWidget( getDialog().getBrowserConnection() );
        modifierNameEntryWidget.createWidget( composite );
        modifierNameEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay != null )
        {
            // Attributes
            List<String> attributeValues = overlay.getOlcRefintAttribute();

            if ( ( attributeValues != null ) && ( attributeValues.size() > 0 ) )
            {
                for ( String attribute : attributeValues )
                {
                    attributes.add( attribute );
                }
            }

            attributesTableViewer.refresh();

            // Placeholder Value
            Dn placeholderValue = overlay.getOlcRefintNothing();

            if ( placeholderValue != null )
            {
                placeholderValueEntryWidget.setInput( placeholderValue );
            }
            else
            {
                placeholderValueEntryWidget.setInput( Dn.EMPTY_DN );
            }

            // Modifier Name
            Dn modifierName = overlay.getOlcRefintModifiersName();

            if ( modifierName != null )
            {
                modifierNameEntryWidget.setInput( modifierName );
            }
            else
            {
                try
                {
                    modifierNameEntryWidget.setInput( new Dn( DEFAULT_MODIFIER_NAME ) );
                }
                catch ( LdapInvalidDnException e )
                {
                    // Nothing to do.
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            // Attributes
            overlay.setOlcRefintAttribute( attributes );

            // Placeholder Value
            Dn placeholderValue = placeholderValueEntryWidget.getDn();

            if ( ( placeholderValue != null ) && ( !Dn.EMPTY_DN.equals( placeholderValue ) ) )
            {
                overlay.setOlcRefintNothing( placeholderValue );
            }
            else
            {
                overlay.setOlcRefintNothing( null );
            }

            // Modifier Name
            Dn modifierName = modifierNameEntryWidget.getDn();

            if ( ( modifierName != null ) && ( !Dn.EMPTY_DN.equals( modifierName ) )
                && ( !modifierName.toString().equals( DEFAULT_MODIFIER_NAME ) ) )
            {
                overlay.setOlcRefintModifiersName( modifierName );
            }
            else
            {
                overlay.setOlcRefintModifiersName( null );
            }
        }
    }
}
