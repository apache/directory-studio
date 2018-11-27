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

package org.apache.directory.studio.valueeditors.image;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;


/**
 * The ImageDialog is used from the image value editor to view the current image
 * and to select a new image.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImageDialog extends Dialog
{
    /** The dialog setting key for the currently selected tab item */
    private static final String SELECTED_TAB_DIALOGSETTINGS_KEY = ImageDialog.class.getName() + ".tab"; //$NON-NLS-1$

    /** The maximum width for the image */
    private static final int MAX_WIDTH = 400;
    
    /** The maximum height for the image */
    private static final int MAX_HEIGHT = 400;

    /** The current image tab item */
    private static final int CURRENT_TAB = 0;
    
    /** The new image tab item */
    private static final int NEW_TAB = 1;

    /** The current image bytes */
    private byte[] currentImageRawData;

    /** The required image type */
    private int requiredImageType;

    /** The new image bytes */
    private byte[] newImageRawData;

    /** The new image bytes in the required image format */
    private byte[] newImageRawDataInRequiredFormat;

    // UI widgets
    private TabFolder tabFolder;

    private TabItem currentTab;
    private Composite currentImageContainer;
    private Image currentImage;
    private Label currentImageLabel;
    private Text currentImageTypeText;
    private Text currentImageWidthText;
    private Text currentImageHeightText;
    private Text currentImageSizeText;
    private Button currentImageSaveButton;

    private TabItem newTab;
    private Composite newImageContainer;
    private Image newImage;
    private Label newImageLabel;
    private Text newImageTypeText;
    private Text newImageWidthText;
    private Text newImageHeightText;
    private Text newImageSizeText;
    private Text newImageFilenameText;
    private Button newImageBrowseButton;

    private Button okButton;


    /**
     * Creates a new instance of ImageDialog.
     * 
     * @param parentShell the parent shell
     * @param currentImageRawData the current image raw data
     * @param requiredImageType the required image type
     */
    public ImageDialog( Shell parentShell, byte[] currentImageRawData, int requiredImageType )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.currentImageRawData = currentImageRawData;
        this.requiredImageType = requiredImageType;

        newImageRawDataInRequiredFormat = null;
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        // Disposing the current image
        if ( ( currentImage != null ) && !currentImage.isDisposed() )
        {
            currentImage.dispose();
        }

        // Disposing the new image
        if ( ( newImage != null ) && !newImage.isDisposed() )
        {
            newImage.dispose();
        }

        // Saving the selected tab item to dialog settings
        ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_TAB_DIALOGSETTINGS_KEY,
            tabFolder.getSelectionIndex() );

        return super.close();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            if ( newImageRawData != null )
            {
                // Preparing the new image bytes for the required format
                try
                {
                    ImageData imageData = new ImageData( new ByteArrayInputStream( newImageRawData ) );
                    
                    if ( imageData.type != requiredImageType )
                    {
                        // Converting the new image in the required format
                        ImageLoader imageLoader = new ImageLoader();
                        imageLoader.data = new ImageData[]
                            { imageData };
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageLoader.save( baos, requiredImageType );
                        newImageRawDataInRequiredFormat = baos.toByteArray();
                    }
                    else
                    {
                        // Directly using the new image bytes
                        newImageRawDataInRequiredFormat = newImageRawData;
                    }
                }
                catch ( SWTException swte )
                {
                    newImageRawDataInRequiredFormat = null;
                }
            }
        }
        else
        {
            newImageRawDataInRequiredFormat = null;
        }

        super.buttonPressed( buttonId );
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "ImageDialog.ImageEditor" ) ); //$NON-NLS-1$
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_IMAGEEDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        // load dialog settings
        try
        {
            int tabIndex = ValueEditorsActivator.getDefault().getDialogSettings().getInt(
                SELECTED_TAB_DIALOGSETTINGS_KEY );
            tabFolder.setSelection( tabIndex );
        }
        catch ( Exception e )
        {
            // Nothing to do
        }

        // Updating the tab folder on load
        updateTabFolder();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        tabFolder = new TabFolder( composite, SWT.TOP );
        tabFolder.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        
        tabFolder.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateTabFolder();
            }
        } );

        // current image
        if ( currentImageRawData != null && currentImageRawData.length > 0 )
        {
            currentTab = new TabItem( tabFolder, SWT.NONE );
            currentTab.setText( Messages.getString( "ImageDialog.CurrentImage" ) ); //$NON-NLS-1$

            currentImageContainer = createTabItemComposite();
            currentImageLabel = createImageLabel( currentImageContainer );

            Composite currentImageInfoContainer = createImageInfoContainer( currentImageContainer );
            currentImageTypeText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageType" ) ); //$NON-NLS-1$
            currentImageSizeText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageSize" ) ); //$NON-NLS-1$
            currentImageWidthText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageWidth" ) ); //$NON-NLS-1$
            currentImageHeightText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageHeight" ) ); //$NON-NLS-1$

            Composite currentImageSaveContainer = createImageInfoContainer( currentImageContainer );
            Label dummyLabel = BaseWidgetUtils.createLabel( currentImageSaveContainer, "", 1 ); //$NON-NLS-1$
            GridData gd = new GridData( GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL );
            dummyLabel.setLayoutData( gd );
            currentImageSaveButton = createButton( currentImageSaveContainer, Messages.getString( "ImageDialog.Save" ) ); //$NON-NLS-1$
            
            currentImageSaveButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent event )
                {
                    FileDialog fileDialog = new FileDialog( ImageDialog.this.getShell(), SWT.SAVE );
                    fileDialog.setText( Messages.getString( "ImageDialog.SaveImage" ) ); //$NON-NLS-1$
                    fileDialog.setFilterExtensions( new String[]
                        { "*.jpg" } ); //$NON-NLS-1$
                    String returnedFileName = fileDialog.open();
                    
                    if ( returnedFileName != null )
                    {
                        try
                        {
                            File file = new File( returnedFileName );
                            FileOutputStream out = new FileOutputStream( file );
                            out.write( currentImageRawData );
                            out.flush();
                            out.close();
                        }
                        catch ( FileNotFoundException e )
                        {
                            ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                                new Status( IStatus.ERROR, ValueEditorsConstants.PLUGIN_ID, IStatus.ERROR, Messages
                                    .getString( "ImageDialog.CantWriteFile" ), e ) ); //$NON-NLS-1$
                        }
                        catch ( IOException e )
                        {
                            ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                                new Status( IStatus.ERROR, ValueEditorsConstants.PLUGIN_ID, IStatus.ERROR, Messages
                                    .getString( "ImageDialog.CantWriteFile" ), e ) ); //$NON-NLS-1$
                        }
                    }
                }
            } );

            currentTab.setControl( currentImageContainer );
        }

        // new image
        newTab = new TabItem( tabFolder, SWT.NONE );
        newTab.setText( Messages.getString( "ImageDialog.NewImage" ) ); //$NON-NLS-1$

        newImageContainer = createTabItemComposite();
        newImageLabel = createImageLabel( newImageContainer );

        Composite newImageInfoContainer = createImageInfoContainer( newImageContainer );
        newImageTypeText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageType" ) ); //$NON-NLS-1$
        newImageSizeText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageSize" ) ); //$NON-NLS-1$
        newImageWidthText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageWidth" ) ); //$NON-NLS-1$
        newImageHeightText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageHeight" ) ); //$NON-NLS-1$

        Composite newImageSelectContainer = createImageInfoContainer( newImageContainer );
        newImageFilenameText = new Text( newImageSelectContainer, SWT.SINGLE | SWT.BORDER );
        GridData gd = new GridData( SWT.FILL, SWT.CENTER, true, false );
        newImageFilenameText.setLayoutData( gd );
        
        newImageFilenameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updateNewImageGroup();
            }
        } );
        
        newImageBrowseButton = createButton( newImageSelectContainer, Messages.getString( "ImageDialog.Browse" ) ); //$NON-NLS-1$
        
        newImageBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                FileDialog fileDialog = new FileDialog( ImageDialog.this.getShell(), SWT.OPEN );
                fileDialog.setText( Messages.getString( "ImageDialog.SelectImage" ) ); //$NON-NLS-1$
                fileDialog.setFileName( new File( newImageFilenameText.getText() ).getName() );
                fileDialog.setFilterPath( new File( newImageFilenameText.getText() ).getParent() );

                String returnedFileName = fileDialog.open();
                
                if ( returnedFileName != null )
                {
                    newImageFilenameText.setText( returnedFileName );
                }
            }
        } );

        newTab.setControl( newImageContainer );
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates a tab item composite.
     *
     * @return a tab item composite
     */
    private Composite createTabItemComposite()
    {
        Composite composite = new Composite( tabFolder, SWT.NONE );

        GridLayout compositeLayout = new GridLayout( 1, false );
        compositeLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        compositeLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        compositeLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        compositeLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        composite.setLayout( compositeLayout );

        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        return composite;
    }


    /**
     * Creates the image label.
     * 
     * @param parent the parent
     * @return the image label
     */
    private Label createImageLabel( Composite parent )
    {
        Composite labelComposite = new Composite( parent, SWT.BORDER );
        labelComposite.setLayout( new GridLayout() );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        labelComposite.setLayoutData( gd );
        labelComposite.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WIDGET_NORMAL_SHADOW ) );

        Label imageLabel = new Label( labelComposite, SWT.CENTER );
        gd = new GridData( SWT.CENTER, SWT.CENTER, true, true );
        imageLabel.setLayoutData( gd );

        return imageLabel;
    }


    /**
     * Update current image tab.
     */
    private void updateCurrentImageGroup()
    {
        if ( currentTab != null )
        {
            if ( ( currentImage != null ) && !currentImage.isDisposed() )
            {
                currentImage.dispose();
                currentImage = null;
            }

            if ( currentImageRawData != null && currentImageRawData.length > 0 )
            {
                try
                {
                    ImageData imageData = new ImageData( new ByteArrayInputStream( currentImageRawData ) );
                    currentImage = new Image( getShell().getDisplay(), resizeImage( imageData ) );
                    currentImageLabel.setText( "" ); //$NON-NLS-1$
                    currentImageLabel.setImage( currentImage );
                    GridData currentImageLabelGridData = new GridData( SWT.CENTER, SWT.CENTER, true, true );
                    currentImageLabelGridData.widthHint = currentImage.getBounds().width;
                    currentImageLabelGridData.heightHint = currentImage.getBounds().height;
                    currentImageLabel.setLayoutData( currentImageLabelGridData );
                    currentImageTypeText.setText( getImageType( imageData.type ) );
                    currentImageSizeText.setText( getSizeString( currentImageRawData.length ) );
                    currentImageWidthText.setText( NLS
                        .bind( Messages.getString( "ImageDialog.Pixel" ), imageData.width ) ); //$NON-NLS-1$
                    currentImageHeightText.setText( NLS.bind(
                        Messages.getString( "ImageDialog.Pixel" ), imageData.height ) ); //$NON-NLS-1$
                }
                catch ( SWTException swte )
                {
                    currentImageLabel.setImage( null );
                    currentImageLabel.setText( Messages.getString( "ImageDialog.UnsupportedFormatSpaces" ) ); //$NON-NLS-1$
                    currentImageTypeText.setText( Messages.getString( "ImageDialog.UnsupportedFormat" ) ); //$NON-NLS-1$
                    currentImageSizeText.setText( getSizeString( currentImageRawData.length ) );
                    currentImageWidthText.setText( "-" ); //$NON-NLS-1$
                    currentImageHeightText.setText( "-" ); //$NON-NLS-1$
                }
            }
            else
            {
                currentImageLabel.setImage( null );
                currentImageLabel.setText( Messages.getString( "ImageDialog.NoImageSpaces" ) ); //$NON-NLS-1$
                currentImageTypeText.setText( Messages.getString( "ImageDialog.NoImage" ) ); //$NON-NLS-1$
                currentImageSizeText.setText( "-" ); //$NON-NLS-1$
                currentImageWidthText.setText( "-" ); //$NON-NLS-1$
                currentImageHeightText.setText( "-" ); //$NON-NLS-1$
            }

            currentImageSaveButton.setEnabled( currentImageRawData != null && currentImageRawData.length > 0 );
        }
    }


    /**
     * Update new image tab.
     */
    private void updateNewImageGroup()
    {
        if ( ( newImage != null ) && !newImage.isDisposed() )
        {
            newImage.dispose();
            newImage = null;
        }

        String newImageFileName = newImageFilenameText.getText();
        
        if ( !Strings.isEmpty( newImageFileName ) ) //$NON-NLS-1$
        {
            try
            {
                File file = new File( newImageFileName );
                FileInputStream in = new FileInputStream( file );
                ByteArrayOutputStream out = new ByteArrayOutputStream( ( int ) file.length() );
                byte[] buf = new byte[4096];
                int len;
                
                while ( ( len = in.read( buf ) ) > 0 )
                {
                    out.write( buf, 0, len );
                }
                
                newImageRawData = out.toByteArray();
                out.close();
                in.close();
            }
            catch ( FileNotFoundException e )
            {
                newImageRawData = null;
                newImageLabel.setImage( null );
                newImageLabel.setText( Messages.getString( "ImageDialog.ErrorFileNotFound" ) ); //$NON-NLS-1$
                newImageTypeText.setText( "-" ); //$NON-NLS-1$
                newImageSizeText.setText( "-" ); //$NON-NLS-1$
                newImageWidthText.setText( "-" ); //$NON-NLS-1$
                newImageHeightText.setText( "-" ); //$NON-NLS-1$
            }
            catch ( IOException e )
            {
                newImageRawData = null;
                newImageLabel.setImage( null );
                newImageLabel.setText( NLS.bind(
                    Messages.getString( "ImageDialog.CantReadFile" ), new String[] { e.getMessage() } ) ); //$NON-NLS-1$
                newImageTypeText.setText( "-" ); //$NON-NLS-1$
                newImageSizeText.setText( "-" ); //$NON-NLS-1$
                newImageWidthText.setText( "-" ); //$NON-NLS-1$
                newImageHeightText.setText( "-" ); //$NON-NLS-1$
            }
        }
        else
        {
            newImageRawData = null;
            newImageLabel.setImage( null );
            newImageLabel.setText( Messages.getString( "ImageDialog.NoImageSelected" ) ); //$NON-NLS-1$
            newImageTypeText.setText( "-" ); //$NON-NLS-1$
            newImageSizeText.setText( "-" ); //$NON-NLS-1$
            newImageWidthText.setText( "-" ); //$NON-NLS-1$
            newImageHeightText.setText( "-" ); //$NON-NLS-1$
        }

        if ( ( newImageRawData != null ) && ( newImageRawData.length > 0 ) )
        {
            try
            {
                ImageData imageData = new ImageData( new ByteArrayInputStream( newImageRawData ) );
                newImage = new Image( getShell().getDisplay(), resizeImage( imageData ) );
                newImageLabel.setImage( newImage );
                newImageTypeText.setText( getImageType( imageData.type ) );
                
                if ( imageData.type != requiredImageType )
                {
                    newImageTypeText
                        .setText( newImageTypeText.getText()
                            + NLS
                                .bind(
                                    Messages.getString( "ImageDialog.WillBeConverted" ), new String[] { getImageType( requiredImageType ) } ) ); //$NON-NLS-1$
                }
                
                newImageSizeText.setText( getSizeString( newImageRawData.length ) );
                newImageWidthText.setText( NLS.bind( Messages.getString( "ImageDialog.Pixel" ), imageData.width ) ); //$NON-NLS-1$
                newImageHeightText.setText( NLS.bind( Messages.getString( "ImageDialog.Pixel" ), imageData.height ) ); //$NON-NLS-1$
            }
            catch ( SWTException swte )
            {
                newImageLabel.setImage( null );
                newImageLabel.setText( Messages.getString( "ImageDialog.UnsupportedFormatSpaces" ) ); //$NON-NLS-1$
                newImageTypeText.setText( Messages.getString( "ImageDialog.UnsupportedFormat" ) ); //$NON-NLS-1$
                newImageSizeText.setText( getSizeString( newImageRawData.length ) );
                newImageWidthText.setText( "-" ); //$NON-NLS-1$
                newImageHeightText.setText( "-" ); //$NON-NLS-1$
            }
        }

        if ( okButton != null )
        {
            okButton.setEnabled( newImage != null );
        }

        newImageLabel.getParent().layout();
        newImageTypeText.getParent().layout();
    }


    /**
     * Update tab folder and the tabs.
     */
    private void updateTabFolder()
    {
        if ( currentImageSaveButton != null )
        {
            if ( tabFolder.getSelectionIndex() == CURRENT_TAB )
            {
                currentImageSaveButton.setFocus();
            }
            
            updateCurrentImageGroup();
        }

        if ( newImageBrowseButton != null )
        {
            if ( ( tabFolder.getSelectionIndex() == NEW_TAB ) || ( currentImageSaveButton == null ) )
            {
                newImageBrowseButton.setFocus();
            }
            
            updateNewImageGroup();
        }
    }


    /**
     * Resizes the image.
     * 
     * @param imageData the image data to resize
     * 
     * @return the resized image data
     */
    private ImageData resizeImage( ImageData imageData )
    {
        // Computing the width scale factor
        double widthScaleFactor = 1.0;
        
        if ( imageData.width > MAX_WIDTH )
        {
            widthScaleFactor = ( double ) MAX_WIDTH / imageData.width;
        }

        // Computing the height scale factor
        double heightScaleFactor = 1.0;
        
        if ( imageData.height > MAX_HEIGHT )
        {
            heightScaleFactor = ( double ) MAX_HEIGHT / imageData.height;
        }

        // Taking the minimum of both
        double minScalefactor = Math.min( heightScaleFactor, widthScaleFactor );

        // Resizing the image data
        return resize( imageData, ( int ) ( imageData.width * minScalefactor ),
            ( int ) ( imageData.height * minScalefactor ) );
    }


    /**
     * Resizes an image using the GC (for better quality).
     *
     * @param imageData the image data
     * @param width the width
     * @param height the height
     * @return the resized image
     */
    private ImageData resize( ImageData imageData, int width, int height )
    {
        Image image = new Image( Display.getDefault(), imageData );
        Image resizedImage = new Image( Display.getDefault(), width, height );

        try
        {
            GC gc = new GC( resizedImage );
            
            try
            {
                gc.setAntialias( SWT.ON );
                gc.setInterpolation( SWT.HIGH );
                gc.drawImage( image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height );
            }
            finally
            {
                gc.dispose();
            }
    
            ImageData resizedImageData = resizedImage.getImageData();
            
            return resizedImageData;
        }
        finally
        {
            image.dispose();
            resizedImage.dispose();
        }
    }


    /**
     * Creates the image info container.
     * 
     * @param parent the parent
     * 
     * @return the image info container
     */
    private Composite createImageInfoContainer( Composite parent )
    {
        Composite imageInfoContainer = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginHeight = gl.marginWidth = 0;
        imageInfoContainer.setLayout( gl );
        imageInfoContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        
        return imageInfoContainer;
    }


    /**
     * Creates the image info.
     * 
     * @param label the label
     * @param parent the parent
     * 
     * @return the image info
     */
    private Text createImageInfo( Composite parent, String label )
    {
        BaseWidgetUtils.createLabel( parent, label, 1 );
        Text text = BaseWidgetUtils.createLabeledText( parent, "", 1 ); //$NON-NLS-1$
        
        return text;
    }


    /**
     * Creates the button.
     * 
     * @param label the label
     * @param parent the parent
     * 
     * @return the button
     */
    private Button createButton( Composite parent, String label )
    {
        Button button = BaseWidgetUtils.createButton( parent, label, 1 );
        
        return button;
    }


    /**
     * Gets the size string.
     * 
     * @param length the length
     * 
     * @return the size string
     */
    private static String getSizeString( int length )
    {
        if ( length > 1000000 )
        {
            return ( length / 1000000 ) + NLS.bind( Messages.getString( "ImageDialog.MB" ), new Integer[] //$NON-NLS-1$
                { length } ); //$NON-NLS-1$
        }
        else if ( length > 1000 )
        {
            return ( length / 1000 ) + NLS.bind( Messages.getString( "ImageDialog.KB" ), new Integer[] //$NON-NLS-1$
                { length } ); //$NON-NLS-1$
        }
        else
        {
            return length + Messages.getString( "ImageDialog.Bytes" ); //$NON-NLS-1$
        }
    }


    /**
     * Gets the image info.
     * 
     * @param imageRawData the image raw data
     * 
     * @return the image info
     */
    public static String getImageInfo( byte[] imageRawData )
    {
        if ( imageRawData == null )
        {
            return IValueEditor.NULL;
        }

        String text;
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream( imageRawData );
            ImageData imageData = new ImageData( bais );
            String typePrefix = getImageType( imageData.type );
            
            if ( !Strings.isEmpty( typePrefix ) ) //$NON-NLS-1$
            {
                typePrefix += "-"; //$NON-NLS-1$
            }

            text = NLS
                .bind(
                    Messages.getString( "ImageDialog.Image" ), new Object[] { typePrefix, imageData.width, imageData.height, imageRawData.length } ); //$NON-NLS-1$
        }
        catch ( SWTException swte )
        {
            text = NLS.bind( Messages.getString( "ImageDialog.InvalidImage" ), new Object[] { imageRawData.length } ); //$NON-NLS-1$
        }
        
        return text;
    }


    /**
     * Gets the image type.
     * 
     * @param swtCode the swt code
     * 
     * @return the image type
     */
    private static String getImageType( int swtCode )
    {
        switch ( swtCode )
        {
            case SWT.IMAGE_JPEG :
                return "JPEG"; //$NON-NLS-1$
                
            case SWT.IMAGE_GIF :
                return "GIF"; //$NON-NLS-1$
                
            case SWT.IMAGE_PNG :
                return "PNG"; //$NON-NLS-1$
                
            case SWT.IMAGE_BMP :
            case SWT.IMAGE_BMP_RLE :
                return "BMP"; //$NON-NLS-1$
                
            default :
                return "";
        }
    }


    /**
     * Gets the image data in required format.
     * 
     * @return Returns the image data in required format or null.
     */
    public byte[] getNewImageRawData()
    {
        return newImageRawDataInRequiredFormat;
    }
}
