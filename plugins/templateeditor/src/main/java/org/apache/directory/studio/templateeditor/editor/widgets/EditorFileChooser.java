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
package org.apache.directory.studio.templateeditor.editor.widgets;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.directory.api.util.Base64;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateFileChooser;


/**
 * This class implements an Editor FileChooser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorFileChooser extends EditorWidget<TemplateFileChooser>
{
    /** The widget's composite */
    private Composite composite;

    /** The icon label */
    private Label iconLabel;

    /** The size label */
    private Label sizeLabel;

    /** The 'Save As...' toolbar item */
    private ToolItem saveAsToolItem;

    /** The 'Clear' toolbar item */
    private ToolItem clearToolItem;

    /** The 'Browse...' toolbar item */
    private ToolItem browseToolItem;

    /** The file data as bytes array */
    private byte[] fileBytes;
    
    /** The icon Image we might have to create */
    private Image iconImage;


    /**
     * Creates a new instance of EditorFileChooser.
     * 
     * @param editor the associated editor
     * @param templateFileChooser the associated template file chooser
     * @param toolkit the associated toolkit
     */
    public EditorFileChooser( IEntryEditor editor, TemplateFileChooser templateFileChooser, FormToolkit toolkit )
    {
        super( templateFileChooser, editor, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createWidget( Composite parent )
    {
        // Creating and initializing the widget UI
        Composite composite = initWidget( parent );

        // Updating the widget's content
        updateWidget();

        // Adding the listeners
        addListeners();

        return composite;
    }


    /**
     * Creates and initializes the widget UI.
     *
     * @param parent the parent composite
     * @return the associated composite
     */
    private Composite initWidget( Composite parent )
    {
        composite = getToolkit().createComposite( parent );
        composite.setLayoutData( getGridata() );

        // Creating the layout
        GridLayout gl = new GridLayout( getLayoutNumberOfColumns(), false );
        gl.marginHeight = gl.marginWidth = 0;
        gl.horizontalSpacing = gl.verticalSpacing = 0;
        composite.setLayout( gl );

        // Icon Label
        if ( getWidget().isShowIcon() )
        {
            // Creating the label for hosting the icon
            iconLabel = getToolkit().createLabel( composite, null );
            iconLabel.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

            // Getting the icon (if available)
            ImageData iconData = null;
            String icon = getWidget().getIcon();
            
            if ( ( icon != null ) && ( !icon.equals( "" ) ) ) //$NON-NLS-1$
            {
                try
                {
                    iconData = new ImageData( new ByteArrayInputStream( Base64.decode( icon.toCharArray() ) ) );
                }
                catch ( SWTException e )
                {
                    // Nothing to do, we just need to return the default image.
                }
            }

            // Assigning the icon
            if ( iconData != null )
            {
                iconImage = new Image( PlatformUI.getWorkbench().getDisplay(), iconData );
                iconLabel.setImage( iconImage );
            }
            else
            {
                iconLabel.setImage( EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_FILE ) );
            }
        }

        // Size Label
        sizeLabel = getToolkit().createLabel( composite, null );
        sizeLabel.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, true, false ) );

        // Toolbar (if needed)
        if ( needsToolbar() )
        {
            ToolBar toolbar = new ToolBar( composite, SWT.HORIZONTAL | SWT.FLAT );
            toolbar.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

            // Save As Button
            if ( getWidget().isShowSaveAsButton() )
            {
                saveAsToolItem = new ToolItem( toolbar, SWT.PUSH );
                saveAsToolItem.setToolTipText( Messages.getString( "EditorFileChooser.SaveAs" ) ); //$NON-NLS-1$
                saveAsToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_SAVE_AS ) );
            }

            // Clear Button
            if ( getWidget().isShowClearButton() )
            {
                clearToolItem = new ToolItem( toolbar, SWT.PUSH );
                clearToolItem.setToolTipText( Messages.getString( "EditorFileChooser.Clear" ) ); //$NON-NLS-1$
                clearToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_CLEAR ) );
            }
            // Browse Button
            if ( getWidget().isShowBrowseButton() )
            {
                browseToolItem = new ToolItem( toolbar, SWT.PUSH );
                browseToolItem.setToolTipText( Messages.getString( "EditorFileChooser.Browse" ) ); //$NON-NLS-1$
                browseToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_BROWSE_FILE ) );
            }
        }

        return composite;
    }


    /**
     * Gets the number of columns needed for the layout.
     *
     * @return the number of columns needed
     */
    private int getLayoutNumberOfColumns()
    {
        int numberOfColumns = 1;

        // Icon
        if ( getWidget().isShowIcon() )
        {
            numberOfColumns++;
        }
        
        // Toolbar
        if ( needsToolbar() )
        {
            numberOfColumns++;
        }

        return numberOfColumns;
    }


    /**
     * Indicates if the widget needs a toolbar for actions.
     *
     * @return<code>true</code> if the widget needs a toolbar for actions,
     * <code>false</code> if not
     */
    private boolean needsToolbar()
    {
        return getWidget().isShowSaveAsButton() || getWidget().isShowClearButton() || getWidget().isShowBrowseButton();
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        // Initializing the image bytes from the given entry.
        initImageBytesFromEntry();

        // Updating the file label
        updateSizeLabel();

        // Updating the states of the buttons
        updateButtonsStates();
    }


    /**
     * Initializes the image bytes from the given entry.
     */
    private void initImageBytesFromEntry()
    {
        // Getting the file bytes in the attribute
        IAttribute attribute = getAttribute();
        
        if ( ( attribute != null ) && ( attribute.isBinary() ) && ( attribute.getValueSize() > 0 ) )
        {
            fileBytes = attribute.getBinaryValues()[0];
            return;
        }

        fileBytes = null;
    }


    /**
     * Updates the "Size" label.
     */
    private void updateSizeLabel()
    {
        sizeLabel.setText( getFileSizeString() );
        sizeLabel.update();
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        // Save As toolbar item
        if ( ( saveAsToolItem != null ) && ( !saveAsToolItem.isDisposed() ) )
        {
            saveAsToolItem.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    saveAsToolItemAction();
                }
            } );
        }

        // Clear toolbar item
        if ( ( clearToolItem != null ) && ( !clearToolItem.isDisposed() ) )
        {
            clearToolItem.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    clearToolItemAction();
                }
            } );
        }

        // Browse toolbar item
        if ( ( browseToolItem != null ) && ( !browseToolItem.isDisposed() ) )
        {
            browseToolItem.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    browseToolItemAction();
                }
            } );
        }
    }


    /**
     * Gets the size string.
     * 
     * @return the size string
     */
    private String getFileSizeString()
    {
        if ( fileBytes != null )
        {
            int length = fileBytes.length;
            
            if ( length > 1000000 )
            {
                return NLS.bind( Messages.getString( "EditorFileChooser.MB" ), new Object[] //$NON-NLS-1$
                    { ( length / 1000000 ), length } );
            }
            else if ( length > 1000 )
            {
                return NLS.bind( Messages.getString( "EditorFileChooser.KB" ), new Object[] //$NON-NLS-1$
                    { ( length / 1000 ), length } );
            }
            else
            {
                return NLS.bind( Messages.getString( "EditorFileChooser.Bytes" ), new Object[] //$NON-NLS-1$
                    { length } );
            }
        }
        else
        {
            return Messages.getString( "EditorFileChooser.NoValue" ); //$NON-NLS-1$
        }
    }


    /**
     * This method is called when the 'Save As...' toolbar item is clicked.
     */
    private void saveAsToolItemAction()
    {
        // Launching a FileDialog to select where to save the file
        FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE );
        String selected = fd.open();
        
        if ( selected != null )
        {
            // Getting the selected file
            File selectedFile = new File( selected );
            
            if ( ( !selectedFile.exists() ) || ( selectedFile.canWrite() ) )
            {
                try
                {
                    FileOutputStream fos = new FileOutputStream( selectedFile );
                    fos.write( fileBytes );
                    fos.close();
                }
                catch ( Exception e )
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( e, "An error occurred while saving the file to disk.", //$NON-NLS-1$
                        new Object[0] );

                    // Launching an error dialog
                    MessageDialog
                        .openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Messages.getString( "EditorFileChooser.ErrorSavingMessageDialogTitle" ), Messages.getString( "EditorFileChooser.ErrorSavingMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }


    /**
     * This method is called when the 'Clear...' toolbar item is clicked.
     */
    private void clearToolItemAction()
    {
        // Launching a confirmation dialog
        if ( MessageDialog.openConfirm( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages
            .getString( "EditorFileChooser.Confirmation" ), Messages //$NON-NLS-1$
            .getString( "EditorFileChooser.ConfirmationClearFile" ) ) ) //$NON-NLS-1$
        {
            // Removing the file bytes
            fileBytes = null;

            // Refreshing the states of the buttons
            updateButtonsStates();

            // Updating the size label
            updateSizeLabel();

            // Updating the entry
            updateEntry();
        }
    }


    /**
     * This method is called when the 'Browse...' toolbar item is clicked.
     */
    private void browseToolItemAction()
    {
        // Launching a FileDialog to select the file to load
        FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
        String selected = fd.open();
        
        if ( selected != null )
        {
            // Getting the selected file
            File selectedFile = new File( selected );
            
            if ( ( selectedFile.exists() ) && ( selectedFile.canRead() ) )
            {
                try
                {
                    FileInputStream fis = null;
                    ByteArrayOutputStream baos = null;

                    try
                    {
                        fis = new FileInputStream( selectedFile );
                        baos = new ByteArrayOutputStream( ( int ) selectedFile.length() );
                        byte[] buf = new byte[4096];
                        int len;
                        
                        while ( ( len = fis.read( buf ) ) > 0 )
                        {
                            baos.write( buf, 0, len );
                        }
    
                        fileBytes = baos.toByteArray();
                    }
                    finally
                    {
                        if ( fis != null )
                        {
                            fis.close();
                        }
                        
                        if ( baos != null )
                        {
                            baos.close();
                        }
                    }
                }
                catch ( Exception e )
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( e, "An error occurred while reading the file from disk.", //$NON-NLS-1$
                        new Object[0] );

                    // Launching an error dialog
                    MessageDialog
                        .openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Messages.getString( "EditorFileChooser.ErrorReadingMessageDialogTitle" ), Messages.getString( "EditorFileChooser.ErrorReadingMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }

                // Refreshing the states of the buttons
                updateButtonsStates();
            }
            else
            {
                // Logging the error
                EntryTemplatePluginUtils.logError( null,
                    "An error occurred while reading the file from disk. File does not exist or is not readable.", //$NON-NLS-1$
                    new Object[0] );

                // Launching an error dialog
                MessageDialog
                    .openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.getString( "EditorFileChooser.ErrorReadingMessageDialogTitle" ), Messages.getString( "EditorFileChooser.ErrorReadingMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // Updating the size label
            updateSizeLabel();

            // Updating the entry
            updateEntry();
        }
    }


    /**
     * This method is called when the entry has been updated in the UI.
     */
    private void updateEntry()
    {
        // Getting the attribute
        IAttribute attribute = getAttribute();
        
        if ( attribute == null )
        {
            if ( ( fileBytes != null ) && ( fileBytes.length != 0 ) )
            {
                // Creating a new attribute with the value
                addNewAttribute( fileBytes );
            }
        }
        else
        {
            if ( ( fileBytes != null ) && ( fileBytes.length != 0 ) )
            {
                // Modifying the existing attribute
                modifyAttributeValue( fileBytes );
            }
            else
            {
                // Deleting the attribute
                deleteAttribute();
            }
        }
    }


    /**
     * Updates the states of the buttons.
     */
    private void updateButtonsStates()
    {
        if ( ( fileBytes != null ) && ( fileBytes.length > 0 ) )
        {
            if ( ( saveAsToolItem != null ) && ( !saveAsToolItem.isDisposed() ) )
            {
                saveAsToolItem.setEnabled( true );
            }

            if ( ( clearToolItem != null ) && ( !clearToolItem.isDisposed() ) )
            {
                clearToolItem.setEnabled( true );
            }

            if ( ( browseToolItem != null ) && ( !browseToolItem.isDisposed() ) )
            {
                browseToolItem.setEnabled( true );
            }
        }
        else
        {
            if ( ( saveAsToolItem != null ) && ( !saveAsToolItem.isDisposed() ) )
            {
                saveAsToolItem.setEnabled( false );
            }

            if ( ( clearToolItem != null ) && ( !clearToolItem.isDisposed() ) )
            {
                clearToolItem.setEnabled( false );
            }

            if ( ( browseToolItem != null ) && ( !browseToolItem.isDisposed() ) )
            {
                browseToolItem.setEnabled( true );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        updateWidget();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( iconImage != null )
        {
            iconImage.dispose();
        }
    }
}
