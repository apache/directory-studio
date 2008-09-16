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
package org.apache.directory.studio.apacheds.configuration.editor.v154.dialogs;


import org.apache.directory.studio.apacheds.configuration.model.v154.IndexedAttribute;
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
 * This class implements the Dialog for Indexed Attribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class IndexedAttributeDialog extends Dialog
{
    /** The Indexed Attribute */
    private IndexedAttribute indexedAttribute;

    /** The dirty flag */
    private boolean dirty = false;

    // UI Fields
    private Text attributeIdText;
    private Text cacheSizeText;


    /**
     * Creates a new instance of IndexedAttributeDialog.
     */
    public IndexedAttributeDialog( IndexedAttribute indexedAttribute )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.indexedAttribute = indexedAttribute;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Indexed Attribute Dialog" );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        Label attributeIdLabel = new Label( composite, SWT.NONE );
        attributeIdLabel.setText( "Attribute ID:" );

        attributeIdText = new Text( composite, SWT.BORDER );
        attributeIdText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        Label cacheSizeLabel = new Label( composite, SWT.NONE );
        cacheSizeLabel.setText( "Cache Size:" );

        cacheSizeText = new Text( composite, SWT.BORDER );
        cacheSizeText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
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
        String attributeId = indexedAttribute.getAttributeId();
        attributeIdText.setText( ( attributeId == null ) ? "" : attributeId );
        cacheSizeText.setText( "" + indexedAttribute.getCacheSize() );
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


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        indexedAttribute.setAttributeId( attributeIdText.getText() );
        try
        {
            indexedAttribute.setCacheSize( Integer.parseInt( cacheSizeText.getText() ) );
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
     * @return
     *      the Indexed Attribute
     */
    public IndexedAttribute getIndexedAttribute()
    {
        return indexedAttribute;
    }


    /**
     * Returns the dirty flag of the dialog.
     *
     * @return
     *      the dirty flag of the dialog
     */
    public boolean isDirty()
    {
        return dirty;
    }
}
