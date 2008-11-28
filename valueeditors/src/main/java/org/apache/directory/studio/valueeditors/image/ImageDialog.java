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
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
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

    private static final String SELECTED_TAB_DIALOGSETTINGS_KEY = ImageDialog.class.getName() + ".tab"; //$NON-NLS-1$

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
        shell.setText( Messages.getString( "ImageDialog.ImageEditor" ) );
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
            currentImageTypeText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageType" ) );
            currentImageSizeText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageSize" ) );
            currentImageWidthText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageWidth" ) );
            currentImageHeightText = createImageInfo( currentImageInfoContainer, Messages
                .getString( "ImageDialog.ImageHeight" ) );

            Composite currentImageSaveContainer = createImageInfoContainer( currentImageContainer );
            Label dummyLabel = BaseWidgetUtils.createLabel( currentImageSaveContainer, "", 1 ); //$NON-NLS-1$
            GridData gd = new GridData( GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL );
            dummyLabel.setLayoutData( gd );
            currentImageSaveButton = createButton( currentImageSaveContainer, Messages.getString( "ImageDialog.Save" ) );
            currentImageSaveButton.addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent event )
                {
                    FileDialog fileDialog = new FileDialog( ImageDialog.this.getShell(), SWT.SAVE );
                    fileDialog.setText( Messages.getString( "ImageDialog.SaveImage" ) );
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
                                    .getString( "ImageDialog.CantWriteFile" ), e ) );
                        }
                        catch ( IOException e )
                        {
                            ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                                new Status( IStatus.ERROR, ValueEditorsConstants.PLUGIN_ID, IStatus.ERROR, Messages
                                    .getString( "ImageDialog.CantWriteFile" ), e ) );
                        }
                    }
                }
            } );

            currentTab = new TabItem( tabFolder, SWT.NONE );
            currentTab.setText( Messages.getString( "ImageDialog.CurrentImage" ) );
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
        newImageTypeText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageType" ) );
        newImageSizeText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageSize" ) );
        newImageWidthText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageWidth" ) );
        newImageHeightText = createImageInfo( newImageInfoContainer, Messages.getString( "ImageDialog.ImageHeight" ) );

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
        newImageBrowseButton = createButton( newImageSelectContainer, Messages.getString( "ImageDialog.Browse" ) );
        newImageBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent event )
            {
                FileDialog fileDialog = new FileDialog( ImageDialog.this.getShell(), SWT.OPEN );
                fileDialog.setText( Messages.getString( "ImageDialog.SelectImage" ) );
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
        newTab.setText( Messages.getString( "ImageDialog.NewImage" ) );
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
                    currentImageLabel.setText( "" ); //$NON-NLS-1$
                    currentImageLabel.setImage( currentImage );
                    currentImageTypeText.setText( getImageType( imageData.type ) );
                    currentImageSizeText.setText( getSizeString( currentImageRawData.length ) );
                    currentImageWidthText.setText( imageData.width + Messages.getString( "ImageDialog.Pixel" ) );
                    currentImageHeightText.setText( imageData.height + Messages.getString( "ImageDialog.Pixel" ) );
                }
                catch ( SWTException swte )
                {
                    currentImageLabel.setImage( null );
                    currentImageLabel.setText( Messages.getString( "ImageDialog.UnsupportedFormatSpaces" ) );
                    currentImageTypeText.setText( Messages.getString( "ImageDialog.UnsupportedFormat" ) );
                    currentImageSizeText.setText( getSizeString( currentImageRawData.length ) );
                    currentImageWidthText.setText( "-" ); //$NON-NLS-1$
                    currentImageHeightText.setText( "-" ); //$NON-NLS-1$
                }
            }
            else
            {
                currentImageLabel.setImage( null );
                currentImageLabel.setText( Messages.getString( "ImageDialog.NoImageSpaces" ) );
                currentImageTypeText.setText( Messages.getString( "ImageDialog.NoImage" ) );
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
        if ( newImage != null && !newImage.isDisposed() )
        {
            newImage.dispose();
            newImage = null;
        }

        if ( !"".equals( newImageFilenameText.getText() ) ) //$NON-NLS-1$
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
                newImageLabel.setText( Messages.getString( "ImageDialog.ErrorFileNotFound" ) );
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
            newImageLabel.setText( Messages.getString( "ImageDialog.NoImageSelected" ) );
            newImageTypeText.setText( "-" ); //$NON-NLS-1$
            newImageSizeText.setText( "-" ); //$NON-NLS-1$
            newImageWidthText.setText( "-" ); //$NON-NLS-1$
            newImageHeightText.setText( "-" ); //$NON-NLS-1$
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
                    newImageTypeText
                        .setText( newImageTypeText.getText()
                            + NLS
                                .bind(
                                    Messages.getString( "ImageDialog.WillBeConverted" ), new String[] { getImageType( requiredImageType ) } ) ); //$NON-NLS-1$
                }
                newImageSizeText.setText( getSizeString( newImageRawData.length ) );
                newImageWidthText.setText( imageData.width + Messages.getString( "ImageDialog.Pixel" ) );
                newImageHeightText.setText( imageData.height + Messages.getString( "ImageDialog.Pixel" ) );
            }
            catch ( SWTException swte )
            {
                newImageLabel.setImage( null );
                newImageLabel.setText( Messages.getString( "ImageDialog.UnsupportedFormatSpaces" ) );
                newImageTypeText.setText( Messages.getString( "ImageDialog.UnsupportedFormat" ) );
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
            return ( length / 1000000 ) + NLS.bind( Messages.getString( "ImageDialog.MB" ), new int[]
                { length } ); //$NON-NLS-1$
        }
        else if ( length > 1000 )
        {
            return ( length / 1000 ) + NLS.bind( Messages.getString( "ImageDialog.KB" ), new int[]
                { length } ); //$NON-NLS-1$
        }
        else
        {
            return length + Messages.getString( "ImageDialog.Bytes" );
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
            return "NULL"; //$NON-NLS-1$
        }

        String text = NLS.bind( Messages.getString( "ImageDialog.Image" ), new Object[] { imageRawData.length } ); //$NON-NLS-1$
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream( imageRawData );
            ImageData imageData = new ImageData( bais );

            String typePrefix = getImageType( imageData.type );
            if ( !"".equals( typePrefix ) ) //$NON-NLS-1$
                typePrefix += "-"; //$NON-NLS-1$

            text = typePrefix
                + NLS
                    .bind(
                        Messages.getString( "ImageDialog.Pixel" ), new Object[] { imageData.width, imageData.height, imageRawData.length } ); //$NON-NLS-2$
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
        String type = ""; //$NON-NLS-1$

        if ( swtCode == SWT.IMAGE_JPEG )
        {
            type = "JPEG"; //$NON-NLS-1$
        }
        else if ( swtCode == SWT.IMAGE_GIF )
        {
            type = "GIF"; //$NON-NLS-1$
        }
        else if ( swtCode == SWT.IMAGE_PNG )
        {
            type = "PNG"; //$NON-NLS-1$
        }
        else if ( swtCode == SWT.IMAGE_BMP || swtCode == SWT.IMAGE_BMP_RLE )
        {
            type = "BMP"; //$NON-NLS-1$
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
