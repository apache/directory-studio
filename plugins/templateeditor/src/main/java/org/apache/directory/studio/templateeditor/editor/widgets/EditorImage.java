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
import org.apache.directory.studio.templateeditor.model.widgets.TemplateImage;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateWidget;


/**
 * This class implements an editor image.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorImage extends EditorWidget<TemplateImage>
{
    /** The widget's composite */
    private Composite composite;

    /** The image label, used to display the image */
    private Label imageLabel;

    /** The 'Save As...' button */
    private ToolItem saveAsToolItem;

    /** The 'Clear' button */
    private ToolItem clearToolItem;

    /** The 'Browse...' button */
    private ToolItem browseToolItem;

    /** The current image */
    private Image image;

    /** The image data as bytes array */
    private byte[] imageBytes;

    /** The default width */
    private static int DEFAULT_WIDTH = 400;

    /** The default height */
    private static int DEFAULT_HEIGHT = 300;


    /**
     * Creates a new instance of EditorImage.
     * 
     * @param editor
     *      the associated editor
     * @param templateImage
     *      the associated template image
     * @param toolkit
     *      the associated toolkit
     */
    public EditorImage( IEntryEditor editor, TemplateImage templateImage, FormToolkit toolkit )
    {
        super( templateImage, editor, toolkit );
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
     * @param parent
     *      the parent composite
     * @return
     *      the associated composite
     */
    private Composite initWidget( Composite parent )
    {
        // Creating the widget composite
        composite = getToolkit().createComposite( parent );
        composite.setLayoutData( getGridata() );

        // Creating the layout
        GridLayout gl = new GridLayout( ( needsToolbar() ? 2 : 1 ), false );
        gl.marginHeight = gl.marginWidth = 0;
        gl.horizontalSpacing = gl.verticalSpacing = 0;
        composite.setLayout( gl );

        // Image Label
        imageLabel = getToolkit().createLabel( composite, null );
        imageLabel.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false ) );

        // Toolbar (if needed)
        if ( needsToolbar() )
        {
            ToolBar toolbar = new ToolBar( composite, SWT.VERTICAL );
            toolbar.setLayoutData( new GridData( SWT.NONE, SWT.FILL, false, true ) );

            // Save As Button
            if ( getWidget().isShowSaveAsButton() )
            {
                saveAsToolItem = new ToolItem( toolbar, SWT.PUSH );
                saveAsToolItem.setToolTipText( Messages.getString( "EditorImage.SaveAs" ) ); //$NON-NLS-1$
                saveAsToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_SAVE_AS ) );
            }

            // Clear Button
            if ( getWidget().isShowClearButton() )
            {
                clearToolItem = new ToolItem( toolbar, SWT.PUSH );
                clearToolItem.setToolTipText( Messages.getString( "EditorImage.Clear" ) ); //$NON-NLS-1$
                clearToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_CLEAR ) );
            }
            // Browse Button
            if ( getWidget().isShowBrowseButton() )
            {
                browseToolItem = new ToolItem( toolbar, SWT.PUSH );
                browseToolItem.setToolTipText( Messages.getString( "EditorImage.Browse" ) ); //$NON-NLS-1$
                browseToolItem.setImage( EntryTemplatePlugin.getDefault().getImage(
                    EntryTemplatePluginConstants.IMG_TOOLBAR_BROWSE_IMAGE ) );
            }
        }

        return composite;
    }


    /**
     * Indicates if the widget needs a toolbar for actions.
     *
     * @return
     *      <code>true</code> if the widget needs a toolbar for actions,
     *      <code>false</code> if not
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

        // Constrains and displays it
        constrainAndDisplayImage();

        // Updating the states of the buttons
        updateButtonsStates();
    }


    /**
     * Initializes the image bytes from the given entry.
     */
    private void initImageBytesFromEntry()
    {
        // Checking is we need to display a value taken from the entry
        // or use the given value
        String attributeType = getWidget().getAttributeType();
        if ( attributeType != null )
        {
            // Getting the image bytes in the attribute
            IAttribute attribute = getAttribute();
            if ( ( attribute != null ) && ( attribute.isBinary() ) && ( attribute.getValueSize() > 0 ) )
            {
                imageBytes = attribute.getBinaryValues()[0];
            }
            else
            {
                imageBytes = null;
            }
        }
        else
        {
            // Getting the image bytes given in the template
            String imageDataString = getWidget().getImageData();
            if ( ( imageDataString != null ) && ( !imageDataString.equals( "" ) ) ) //$NON-NLS-1$
            {
                imageBytes = Base64.decode( imageDataString.toCharArray() );
            }
        }
    }


    /**
     * Returns an {@link ImageData} constructed from an array of bytes.
     *
     * @param imageBytes
     *      the array of bytes
     * @return
     *      the corresponding {@link ImageData}
     * @throws SWTException
     *      if an error occurs when constructing the {@link ImageData}
     */
    private ImageData getImageData( byte[] imageBytes ) throws SWTException
    {
        if ( imageBytes != null && imageBytes.length > 0 )
        {
            return new ImageData( new ByteArrayInputStream( imageBytes ) );
        }
        else
        {
            return null;
        }
    }


    /**
     * Returns the {@link ImageData} associated with the current image bytes.
     *
     * @return
     *      the {@link ImageData} associated with the current image bytes.
     */
    private ImageData getImageData()
    {
        if ( imageBytes != null )
        {
            // Getting the image data associated with the bytes
            try
            {
                return getImageData( imageBytes );
            }
            catch ( SWTException e )
            {
                // Nothing to do, we just need to return the default image.
            }
        }

        // No image
        return EntryTemplatePlugin.getDefault().getImage( EntryTemplatePluginConstants.IMG_NO_IMAGE ).getImageData();
    }


    /**
     * Constrains and displays the image.
     */
    private void constrainAndDisplayImage()
    {
        // Getting the image data
        ImageData imageData = getImageData();

        // Getting width and height from the template image
        int templateImageWidth = getWidget().getImageWidth();
        int templateImageHeight = getWidget().getImageHeight();

        // No resizing is required
        if ( ( templateImageWidth == TemplateWidget.DEFAULT_SIZE )
            && ( templateImageHeight == TemplateWidget.DEFAULT_SIZE ) )
        {
            // Checking if the dimensions of the image are greater than the default values
            if ( ( imageData.width > DEFAULT_WIDTH ) || ( imageData.height > DEFAULT_HEIGHT ) )
            {
                // Calculating scale factors to determine whether width or height should be used
                float widthScaleFactor = imageData.width / DEFAULT_WIDTH;
                float heightScaleFactor = imageData.height / DEFAULT_HEIGHT;

                // Resizing the image data
                if ( widthScaleFactor >= heightScaleFactor )
                {
                    imageData = getScaledImageData( imageData, DEFAULT_WIDTH, TemplateWidget.DEFAULT_SIZE );
                }
                else
                {
                    imageData = getScaledImageData( imageData, TemplateWidget.DEFAULT_SIZE, DEFAULT_HEIGHT );
                }
            }
        }
        else
        {
            // Resizing the image data
            imageData = getScaledImageData( imageData, templateImageWidth, templateImageHeight );
        }

        // Creating the image
        image = new Image( PlatformUI.getWorkbench().getDisplay(), imageData );

        // Setting the image
        imageLabel.setImage( image );
    }


    /**
     * Returns a scaled copy of the given data scaled to the given dimensions, 
     * or the original image data if scaling is not needed.
     *
     * @param imageData
     *      the image data
     * @param width
     *      the preferred width
     * @param height
     *      the preferred height
     * @return
     *      a scaled copy of the given data scaled to the given dimensions, 
     *      or the original image data if scaling is not needed.
     */
    private ImageData getScaledImageData( ImageData imageData, int width, int height )
    {
        // Resizing the image with the given width value
        if ( ( width != TemplateWidget.DEFAULT_SIZE ) && ( height == TemplateWidget.DEFAULT_SIZE ) )
        {
            // Computing the scale factor
            float scaleFactor = ( float ) imageData.width / ( float ) width;

            // Computing the final height
            int finalHeight = ( int ) ( imageData.height / scaleFactor );

            // Returning the scaled image data
            return imageData.scaledTo( width, finalHeight );
        }
        // Resizing the image with the given height value
        else if ( ( width == TemplateWidget.DEFAULT_SIZE ) && ( height != TemplateWidget.DEFAULT_SIZE ) )
        {
            // Computing the scale factor
            float scaleFactor = ( float ) imageData.height / ( float ) height;

            // Computing the final height
            int finalWidth = ( int ) ( imageData.width / scaleFactor );

            // Returning the scaled image data
            return imageData.scaledTo( finalWidth, height );
        }
        // Resizing the image with the given width and height values
        else if ( ( width != TemplateWidget.DEFAULT_SIZE ) && ( height != TemplateWidget.DEFAULT_SIZE ) )
        {
            // Returning the original image data
            return imageData.scaledTo( width, height );
        }

        // No resizing needed
        return imageData;
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        // Save As button
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

        // Clear button
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

        // Browse button
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
                    fos.write( imageBytes );
                    fos.close();
                }
                catch ( Exception e )
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( e, "An error occurred while saving the image to disk.", //$NON-NLS-1$
                        new Object[0] );

                    // Launching an error dialog
                    MessageDialog
                        .openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Messages.getString( "EditorImage.ErrorSavingMessageDialogTitle" ), Messages.getString( "EditorImage.ErrorSavingMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
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
            .getString( "EditorImage.Confirmation" ), Messages.getString( "EditorImage.ConfirmationClearImage" ) ) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            // Removing the image bytes
            imageBytes = null;

            // Constrains and displays the image
            constrainAndDisplayImage();

            // Refreshing the states of the buttons
            updateButtonsStates();

            // Updating the entry
            updateEntry();

            // Updating the image
            composite.getParent().update();
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
                    FileInputStream fis = new FileInputStream( selectedFile );
                    ByteArrayOutputStream baos = new ByteArrayOutputStream( ( int ) selectedFile.length() );
                    byte[] buf = new byte[4096];
                    int len;
                    while ( ( len = fis.read( buf ) ) > 0 )
                    {
                        baos.write( buf, 0, len );
                    }

                    imageBytes = baos.toByteArray();
                }
                catch ( Exception e )
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( e, "An error occurred while reading the image from disk.", //$NON-NLS-1$
                        new Object[0] );

                    // Launching an error dialog
                    MessageDialog
                        .openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Messages.getString( "EditorImage.ErrorReadingMessageDialogTitle" ), Messages.getString( "EditorImage.ErrorReadingMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }

                // Constrains and displays the image
                constrainAndDisplayImage();

                // Refreshing the states of the buttons
                updateButtonsStates();
            }
            else
            {
                // Logging the error
                EntryTemplatePluginUtils
                    .logError(
                        null,
                        "An error occurred while reading the image from disk. Image file does not exist or is not readable.", //$NON-NLS-1$
                        new Object[0] );

                // Launching an error dialog
                MessageDialog
                    .openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.getString( "EditorImage.ErrorReadingMessageDialogTitle" ), Messages.getString( "EditorImage.ErrorReadingMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // Updating the entry
            updateEntry();

            // Updating the image
            composite.getParent().update();
        }
    }


    /**
     * Updates the states of the buttons.
     */
    private void updateButtonsStates()
    {
        if ( ( imageBytes != null ) && ( imageBytes.length > 0 ) )
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
        // Nothing to do
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
            if ( ( imageBytes != null ) && ( imageBytes.length != 0 ) )
            {
                // Creating a new attribute with the value
                addNewAttribute( imageBytes );
            }
        }
        else
        {
            if ( ( imageBytes != null ) && ( imageBytes.length != 0 ) )
            {
                // Modifying the existing attribute
                modifyAttributeValue( imageBytes );
            }
            else
            {
                // Deleting the attribute
                deleteAttribute();
            }
        }
    }
}