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

import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
 * @version $Rev$, $Date$
 */
public class ImageDialog extends Dialog
{

    private static final int MAX_WIDTH = 250;

    private static final int MAX_HEIGHT = 250;

    private static final int CURRENT_TAB = 0;

    private static final int NEW_TAB = 1;

    private static final String SELECTED_TAB_DIALOGSETTINGS_KEY = ImageDialog.class.getName() + ".tab";

    private TabFolder tabFolder;

    private TabItem currentTab;

    private TabItem newTab;

    private byte[] currentImageRawData;

    private Image currentImage;

    private Composite currentImageContainer;

    private Label currentImageLabel;

    private Text currentImageTypeText;

    private Text currentImageWidthText;

    private Text currentImageHeightText;

    private Text currentImageSizeText;

    private Button currentImageSaveButton;

    private byte[] newImageRawData;

    private Image newImage;

    private Composite newImageContainer;

    private Label newImageLabel;

    private Text newImageTypeText;

    private Text newImageWidthText;

    private Text newImageHeightText;

    private Text newImageSizeText;

    private Text newImageFilenameText;

    private Button newImageBrowseButton;

    private int requiredImageType;

    private byte[] newImageRawDataInRequiredFormat;

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
        if ( currentImage != null && !currentImage.isDisposed() )
        {
            currentImage.dispose();
        }
        if ( newImage != null && !newImage.isDisposed() )
        {
            newImage.dispose();
        }

        // save selected tab to dialog settings
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
                try
                {
                    ImageData imageData = new ImageData( new ByteArrayInputStream( newImageRawData ) );
                    if ( imageData.type != requiredImageType )
                    {
                        ImageLoader imageLoader = new ImageLoader();
                        imageLoader.data = new ImageData[]
                            { imageData };
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageLoader.save( baos, requiredImageType );
                        newImageRawDataInRequiredFormat = baos.toByteArray();
                    }
                    else
                    {
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
        shell.setText( "Image Editor" );
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
            int tabIndex = ValueEditorsActivator.getDefault().getDialogSettings().getInt( SELECTED_TAB_DIALOGSETTINGS_KEY );
            tabFolder.setSelection( tabIndex );
        }
        catch ( Exception e )
        {
        }

        // update on load
        updateTabFolder();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd1 = new GridData( GridData.FILL_BOTH );
        gd1.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd1.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd1 );

        tabFolder = new TabFolder( composite, SWT.TOP );
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        tabFolder.setLayout( mainLayout );
        tabFolder.setLayoutData( new GridData( GridData.FILL_BOTH ) );
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
            currentImageContainer = new Composite( tabFolder, SWT.NONE );
            GridLayout currentLayout = new GridLayout( 1, false );
            currentLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
            currentLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
            currentLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
            currentLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
            currentImageContainer.setLayout( currentLayout );
            currentImageContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

            currentImageLabel = createImageLabel( currentImageContainer );

            Composite currentImageInfoContainer = createImageInfoContainer( currentImageContainer );
            currentImageTypeText = createImageInfo( currentImageInfoContainer, "Image Type:" );
            currentImageSizeText = createImageInfo( currentImageInfoContainer, "Image Size:" );
            currentImageWidthText = createImageInfo( currentImageInfoContainer, "Image Width:" );
            currentImageHeightText = createImageInfo( currentImageInfoContainer, "Image Height:" );

            Composite currentImageSaveContainer = createImageInfoContainer( currentImageContainer );
            Label dummyLabel = BaseWidgetUtils.createLabel( currentImageSaveContainer, "", 1 );
            GridData gd = new GridData( GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL );
            dummyLabel.setLayoutData( gd );
            currentImageSaveButton = createButton( currentImageSaveContainer, "Save..." );
            currentImageSaveButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent event )
                {
                    FileDialog fileDialog = new FileDialog( ImageDialog.this.getShell(), SWT.SAVE );
                    fileDialog.setText( "Save Image" );
                    fileDialog.setFilterExtensions( new String[]
                        { "*.jpg" } );
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

                            ConnectionUIPlugin.getDefault()
                                .getExceptionHandler().handleException(
                                    new Status( IStatus.ERROR, ValueEditorsActivator.PLUGIN_ID, IStatus.ERROR,
                                        "Can't write to file", e ) );
                        }
                        catch ( IOException e )
                        {
                            ConnectionUIPlugin.getDefault()
                                .getExceptionHandler().handleException(
                                    new Status( IStatus.ERROR, ValueEditorsActivator.PLUGIN_ID, IStatus.ERROR,
                                        "Can't write to file", e ) );
                        }
                    }
                }
            } );

            currentTab = new TabItem( tabFolder, SWT.NONE );
            currentTab.setText( "Current Image" );
            currentTab.setControl( currentImageContainer );
        }

        // new image
        newImageContainer = new Composite( tabFolder, SWT.NONE );
        GridLayout newLayout = new GridLayout( 1, false );
        newLayout.marginHeight = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_MARGIN );
        newLayout.marginWidth = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_MARGIN );
        newLayout.verticalSpacing = convertVerticalDLUsToPixels( IDialogConstants.VERTICAL_SPACING );
        newLayout.horizontalSpacing = convertHorizontalDLUsToPixels( IDialogConstants.HORIZONTAL_SPACING );
        newImageContainer.setLayout( newLayout );
        newImageContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        newImageLabel = createImageLabel( newImageContainer );

        Composite newImageInfoContainer = createImageInfoContainer( newImageContainer );
        newImageTypeText = createImageInfo( newImageInfoContainer, "Image Type:" );
        newImageSizeText = createImageInfo( newImageInfoContainer, "Image Size:" );
        newImageWidthText = createImageInfo( newImageInfoContainer, "Image Width:" );
        newImageHeightText = createImageInfo( newImageInfoContainer, "Image Height:" );

        Composite newImageSelectContainer = createImageInfoContainer( newImageContainer );
        newImageFilenameText = new Text( newImageSelectContainer, SWT.SINGLE | SWT.BORDER );
        GridData gd = new GridData( GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL );
        newImageFilenameText.setLayoutData( gd );
        newImageFilenameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                updateNewImageGroup();
            }
        } );
        newImageBrowseButton = createButton( newImageSelectContainer, "Browse..." );
        newImageBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                FileDialog fileDialog = new FileDialog( ImageDialog.this.getShell(), SWT.OPEN );
                fileDialog.setText( "Select Image" );
                // fileDialog.setFilterExtensions(IMAGE_FILE_EXTENSIONS);
                fileDialog.setFileName( new File( newImageFilenameText.getText() ).getName() );
                fileDialog.setFilterPath( new File( newImageFilenameText.getText() ).getParent() );
                String returnedFileName = fileDialog.open();
                if ( returnedFileName != null )
                {
                    newImageFilenameText.setText( returnedFileName );
                }
            }
        } );

        newTab = new TabItem( tabFolder, SWT.NONE );
        newTab.setText( "New Image" );
        newTab.setControl( newImageContainer );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Update current image tab.
     */
    private void updateCurrentImageGroup()
    {
        if ( currentTab != null )
        {
            if ( currentImage != null && !currentImage.isDisposed() )
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
                    currentImageLabel.setText( "" );
                    currentImageLabel.setImage( currentImage );
                    currentImageTypeText.setText( getImageType( imageData.type ) );
                    currentImageSizeText.setText( getSizeString( currentImageRawData.length ) );
                    currentImageWidthText.setText( imageData.width + " Pixel" );
                    currentImageHeightText.setText( imageData.height + " Pixel" );
                }
                catch ( SWTException swte )
                {
                    currentImageLabel.setImage( null );
                    currentImageLabel.setText( " Unsupported format " );
                    currentImageTypeText.setText( "Unsupported format" );
                    currentImageSizeText.setText( getSizeString( currentImageRawData.length ) );
                    currentImageWidthText.setText( "-" );
                    currentImageHeightText.setText( "-" );
                }
            }
            else
            {
                currentImageLabel.setImage( null );
                currentImageLabel.setText( " No Image " );
                currentImageTypeText.setText( "No Image" );
                currentImageSizeText.setText( "-" );
                currentImageWidthText.setText( "-" );
                currentImageHeightText.setText( "-" );
            }

            currentImageSaveButton.setEnabled( currentImageRawData != null && currentImageRawData.length > 0 );
        }
    }


    /**
     * Update new image tab.
     */
    private void updateNewImageGroup()
    {
        if ( newImage != null && !newImage.isDisposed() )
        {
            newImage.dispose();
            newImage = null;
        }

        if ( !"".equals( newImageFilenameText.getText() ) )
        {
            try
            {
                File file = new File( newImageFilenameText.getText() );
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
                newImageLabel.setText( " Error: File not found " );
                newImageTypeText.setText( "-" );
                newImageSizeText.setText( "-" );
                newImageWidthText.setText( "-" );
                newImageHeightText.setText( "-" );
            }
            catch ( IOException e )
            {
                newImageRawData = null;
                newImageLabel.setImage( null );
                newImageLabel.setText( " Error: Can't read file (" + e.getMessage() + ") " );
                newImageTypeText.setText( "-" );
                newImageSizeText.setText( "-" );
                newImageWidthText.setText( "-" );
                newImageHeightText.setText( "-" );
            }
        }
        else
        {
            newImageRawData = null;
            newImageLabel.setImage( null );
            newImageLabel.setText( " No image selected " );
            newImageTypeText.setText( "-" );
            newImageSizeText.setText( "-" );
            newImageWidthText.setText( "-" );
            newImageHeightText.setText( "-" );
        }

        if ( newImageRawData != null && newImageRawData.length > 0 )
        {
            try
            {
                ImageData imageData = new ImageData( new ByteArrayInputStream( newImageRawData ) );
                newImage = new Image( getShell().getDisplay(), resizeImage( imageData ) );
                newImageLabel.setImage( newImage );
                newImageTypeText.setText( getImageType( imageData.type ) );
                if ( imageData.type != requiredImageType )
                {
                    newImageTypeText.setText( newImageTypeText.getText() + " (will be converted to "
                        + getImageType( requiredImageType ) + ")" );
                }
                newImageSizeText.setText( getSizeString( newImageRawData.length ) );
                newImageWidthText.setText( imageData.width + " Pixel" );
                newImageHeightText.setText( imageData.height + " Pixel" );
            }
            catch ( SWTException swte )
            {
                newImageLabel.setImage( null );
                newImageLabel.setText( " Unsupported format " );
                newImageTypeText.setText( "Unsupported format" );
                newImageSizeText.setText( getSizeString( newImageRawData.length ) );
                newImageWidthText.setText( "-" );
                newImageHeightText.setText( "-" );
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
            if ( tabFolder.getSelectionIndex() == NEW_TAB || currentImageSaveButton == null )
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
        double widthScaleFactor = 1.0;
        if ( imageData.width > MAX_WIDTH )
        {
            widthScaleFactor = ( double ) MAX_WIDTH / imageData.width;
        }
        double heightScaleFactor = 1.0;
        if ( imageData.height > MAX_HEIGHT )
        {
            heightScaleFactor = ( double ) MAX_HEIGHT / imageData.height;
        }

        if ( heightScaleFactor < widthScaleFactor )
        {
            imageData = imageData.scaledTo(
                convertHorizontalDLUsToPixels( ( int ) ( imageData.width * heightScaleFactor ) ),
                convertHorizontalDLUsToPixels( ( int ) ( imageData.height * heightScaleFactor ) ) );
        }
        else
        {
            imageData = imageData.scaledTo(
                convertHorizontalDLUsToPixels( ( int ) ( imageData.width * widthScaleFactor ) ),
                convertHorizontalDLUsToPixels( ( int ) ( imageData.height * widthScaleFactor ) ) );
        }

        return imageData;
    }


    /**
     * Creates the image label.
     * 
     * @param parent the parent
     * 
     * @return the image label
     */
    private Label createImageLabel( Composite parent )
    {
        Composite labelComposite = new Composite( parent, SWT.BORDER );
        GridLayout gl = new GridLayout( 1, true );
        labelComposite.setLayout( gl );
        GridData gd = new GridData( GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL );
        gd.widthHint = MAX_WIDTH;
        gd.heightHint = MAX_HEIGHT;
        labelComposite.setLayoutData( gd );
        labelComposite.setBackground( getShell().getDisplay().getSystemColor( SWT.COLOR_WIDGET_NORMAL_SHADOW ) );

        Label imageLabel = new Label( labelComposite, SWT.CENTER );
        gd = new GridData( SWT.CENTER, SWT.CENTER, true, true );
        imageLabel.setLayoutData( gd );
        return imageLabel;
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
        Text text = BaseWidgetUtils.createLabeledText( parent, "", 1 );
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
            return ( length / 1000000 ) + " MB (" + length + " bytes)";
        }
        else if ( length > 1000 )
        {
            return ( length / 1000 ) + " KB (" + length + " bytes)";
        }
        else
        {
            return length + " bytes";
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
            return "NULL";
        }

        String text = "Image (" + imageRawData.length + " Bytes)";
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream( imageRawData );
            ImageData imageData = new ImageData( bais );

            String typePrefix = getImageType( imageData.type );
            if ( !"".equals( typePrefix ) )
                typePrefix += "-";

            text = typePrefix + "Image (" + imageData.width + "x" + imageData.height + " Pixel, " + imageRawData.length
                + " Bytes)";
        }
        catch ( SWTException swte )
        {
            text = "Invalid Image (" + imageRawData.length + " Bytes)";
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
        String type = "";
        
        if ( swtCode == SWT.IMAGE_JPEG )
        {
            type = "JPEG";
        }
        else if ( swtCode == SWT.IMAGE_GIF )
        {
            type = "GIF";
        }
        else if ( swtCode == SWT.IMAGE_PNG )
        {
            type = "PNG";
        }
        else if ( swtCode == SWT.IMAGE_BMP || swtCode == SWT.IMAGE_BMP_RLE )
        {
            type = "BMP";
        }
        
        return type;
    }


    /**
     * Gets the iimage data in required format.
     * 
     * @return Returns the image data in required format or null.
     */
    public byte[] getNewImageRawData()
    {
        return newImageRawDataInRequiredFormat;
    }
}
