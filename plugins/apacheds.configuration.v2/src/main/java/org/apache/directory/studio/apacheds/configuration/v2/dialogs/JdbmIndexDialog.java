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
package org.apache.directory.studio.apacheds.configuration.v2.dialogs;


import org.apache.directory.server.config.beans.JdbmIndexBean;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Dialog for a JDBM index.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class JdbmIndexDialog extends Dialog
{
    /** The Indexed Attribute */
    private JdbmIndexBean index;

    /** The dirty flag */
    private boolean dirty = false;

    // UI Fields
    private Text attributeIdText;
    private Text cacheSizeText;


    /**
     * Creates a new instance of JdbmIndexDialog.
     */
    public JdbmIndexDialog( JdbmIndexBean index )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.index = index;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "JdbmIndexDialog.IndexedAttributeDialog" ) ); //$NON-NLS-1$
    }


    /**
     * This create a dialog like :
     * 
     * <pre>
     *   +-----------------------------------------------------+
     *   | Attribute ID: [           ]  Cache Size: [        ] |
     *   +-----------------------------------------------------+
     * </pre>
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        Label attributeIdLabel = new Label( composite, SWT.NONE );
        attributeIdLabel.setText( Messages.getString( "JdbmIndexDialog.AttributeID" ) ); //$NON-NLS-1$

        attributeIdText = new Text( composite, SWT.BORDER );
        attributeIdText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        Label cacheSizeLabel = new Label( composite, SWT.NONE );
        cacheSizeLabel.setText( Messages.getString( "JdbmIndexDialog.CacheSize" ) ); //$NON-NLS-1$

        cacheSizeText = new Text( composite, SWT.BORDER );
        cacheSizeText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                // The cache size must be a numeric
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        
        cacheSizeText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initFromInput();
        addListeners();

        return composite;
    }


    /**
     * Initializes the UI from the input.
     */
    private void initFromInput()
    {
        String attributeId = index.getIndexAttributeId();
        attributeIdText.setText( ( attributeId == null ) ? "" : attributeId ); //$NON-NLS-1$
        cacheSizeText.setText( "" + index.getIndexCacheSize() ); //$NON-NLS-1$
    }


    /**
     * Adds listeners to the UI Fields.
     */
    private void addListeners()
    {
        attributeIdText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dirty = true;
            }
        } );

        cacheSizeText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dirty = true;
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        index.setIndexAttributeId( attributeIdText.getText() );
        
        try
        {
            index.setIndexCacheSize( Integer.parseInt( cacheSizeText.getText() ) );
        }
        catch ( NumberFormatException e )
        {
            // Nothing to do, it won't happen
        }

        super.okPressed();
    }


    /**
     * Gets the Indexed Attribute.
     *
     * @return the Indexed Attribute
     */
    public JdbmIndexBean getIndex()
    {
        return index;
    }


    /**
     * Returns the dirty flag of the dialog.
     *
     * @return the dirty flag of the dialog
     */
    public boolean isDirty()
    {
        return dirty;
    }
}
