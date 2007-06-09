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

package org.apache.directory.ldapstudio.valueeditors.image;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsActivator;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsConstants;
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


public class ImageDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Image Editor";

    // public static final String[] IMAGE_FILE_EXTENSIONS = {"*.jpg; *.jpeg;
    // *.gif; *.bmp; *.png"};
    public static final int MAX_WIDTH = 250;

    public static final int MAX_HEIGHT = 250;

    public static final int CURRENT_TAB = 0;

    public static final int NEW_TAB = 1;

    public static final String SELECTED_TAB_DIALOGSETTINGS_KEY = ImageDialog.class.getName() + ".tab";

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


    public ImageDialog( Shell parentShell, byte[] currentImageRawData, int requiredImageType )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.currentImageRawData = currentImageRawData;
        this.requiredImageType = requiredImageType;

        newImageRawDataInRequiredFormat = null;
    }


    public boolean close()
    {
        if ( this.currentImage != null && !this.currentImage.isDisposed() )
        {
            this.currentImage.dispose();
        }
        if ( this.newImage != null && !this.newImage.isDisposed() )
        {
            this.newImage.dispose();
        }

        // save selected tab to dialog settings
        ValueEditorsActivator.getDefault().getDialogSettings().put( SELECTED_TAB_DIALOGSETTINGS_KEY,
            this.tabFolder.getSelectionIndex() );

        return super.close();
    }


    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            if ( this.newImageRawData != null )
            {
                try
                {
                    ImageData imageData = new ImageData( new ByteArrayInputStream( this.newImageRawData ) );
                    if ( imageData.type != this.requiredImageType )
                    {
                        ImageLoader imageLoader = new ImageLoader();
                        imageLoader.data = new ImageData[]
                            { imageData };
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageLoader.save( baos, this.requiredImageType );
                        this.newImageRawDataInRequiredFormat = baos.toByteArray();
                    }
                    else
                    {
                        this.newImageRawDataInRequiredFormat = this.newImageRawData;
                    }
                }
                catch ( SWTException swte )
                {
                    this.newImageRawDataInRequiredFormat = null;
                }
            }

        }
        else
        {
            this.newImageRawDataInRequiredFormat = null;
        }

        super.buttonPressed( buttonId );
    }


    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( ValueEditorsActivator.getDefault().getImage( ValueEditorsConstants.IMG_IMAGEEDITOR ) );
    }


    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        // load dialog settings
        try
        {
            int tabIndex = ValueEditorsActivator.getDefault().getDialogSettings().getInt( SELECTED_TAB_DIALOGSETTINGS_KEY );
            this.tabFolder.setSelection( tabIndex );
        }
        catch ( Exception e )
        {
        }

        // update on load
        updateTabFolder();
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd1 = new GridData( GridData.FILL_BOTH );
        gd1.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd1.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd1 );

        this.tabFolder = new TabFolder( composite, SWT.TOP );
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        this.tabFolder.setLayout( mainLayout );
        this.tabFolder.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        this.tabFolder.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                updateTabFolder();
            }
        } );

        // current image
        if ( this.currentImageRawData != null && this.currentImageRawData.length > 0 )
        {
            currentImageContainer = new Composite( this.tabFolder, SWT.NONE );
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

                            BrowserCommonActivator.getDefault()
                                .getExceptionHandler().handleException(
                                    new Status( IStatus.ERROR, ValueEditorsActivator.PLUGIN_ID, IStatus.ERROR,
                                        "Can't write to file", e ) );
                        }
                        catch ( IOException e )
                        {
                            BrowserCommonActivator.getDefault()
                                .getExceptionHandler().handleException(
                                    new Status( IStatus.ERROR, ValueEditorsActivator.PLUGIN_ID, IStatus.ERROR,
                                        "Can't write to file", e ) );
                        }
                    }
                }
            } );

            this.currentTab = new TabItem( this.tabFolder, SWT.NONE );
            this.currentTab.setText( "Current Image" );
            this.currentTab.setControl( currentImageContainer );
        }

        // new image
        newImageContainer = new Composite( this.tabFolder, SWT.NONE );
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

        this.newTab = new TabItem( this.tabFolder, SWT.NONE );
        this.newTab.setText( "New Image" );
        this.newTab.setControl( newImageContainer );

        applyDialogFont( composite );
        return composite;
    }


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
                // currentImageGroup.setVisible(true);
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

            // super.initializeBounds();
        }
    }


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
                this.newImageRawData = out.toByteArray();
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
                        + getImageType( this.requiredImageType ) + ")" );
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
        // newImageGroup.layout();
        // super.initializeBounds();

    }


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


    private Composite createImageInfoContainer( Composite parent )
    {
        Composite imageInfoContainer = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginHeight = gl.marginWidth = 0;
        imageInfoContainer.setLayout( gl );
        imageInfoContainer.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        return imageInfoContainer;
    }


    private Text createImageInfo( Composite parent, String label )
    {
        BaseWidgetUtils.createLabel( parent, label, 1 );
        Text text = BaseWidgetUtils.createLabeledText( parent, "", 1 );
        return text;
    }


    private Button createButton( Composite parent, String label )
    {
        Button button = BaseWidgetUtils.createButton( parent, label, 1 );
        return button;
    }


    public static String getSizeString( int length )
    {
        if ( length > 1000000 )
            return ( length / 1000000 ) + " MB (" + length + " bytes)";
        else if ( length > 1000 )
            return ( length / 1000 ) + " KB (" + length + " bytes)";
        else
            return length + " bytes";
    }


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


    public static String getImageType( int swtCode )
    {
        String type = "";
        if ( swtCode == SWT.IMAGE_JPEG )
            type = "JPEG";
        else if ( swtCode == SWT.IMAGE_GIF )
            type = "GIF";
        else if ( swtCode == SWT.IMAGE_PNG )
            type = "PNG";
        else if ( swtCode == SWT.IMAGE_BMP || swtCode == SWT.IMAGE_BMP_RLE )
            type = "BMP";
        return type;
    }


    /**
     * 
     * 
     * @return Returns the image data in required format or null.
     */
    public byte[] getNewImageRawData()
    {
        return this.newImageRawDataInRequiredFormat;
    }
}
