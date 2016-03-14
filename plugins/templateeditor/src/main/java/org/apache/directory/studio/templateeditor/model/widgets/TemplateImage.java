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
package org.apache.directory.studio.templateeditor.model.widgets;


/**
 * This class implements a template image.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateImage extends AbstractTemplateWidget
{
    /** The default show save as button value */
    public static String DEFAULT_IMAGE_DATA = null;

    /** The default show save as button value */
    public static boolean DEFAULT_SHOW_SAVE_AS_BUTTON = true;

    /** The default show clear button value */
    public static boolean DEFAULT_SHOW_CLEAR_BUTTON = true;

    /** The default show browse button value */
    public static boolean DEFAULT_SHOW_BROWSE_BUTTON = true;

    /** The image data */
    private String imageData = DEFAULT_IMAGE_DATA;

    /** The flag which indicates if a "<em>Save As...</em>" button should be shown */
    private boolean showSaveAsButton = DEFAULT_SHOW_SAVE_AS_BUTTON;

    /** The flag which indicates if a "<em>Clear</em>" button should be shown */
    private boolean showClearButton = DEFAULT_SHOW_CLEAR_BUTTON;

    /** The flag which indicates if a "<em>Browse...</em>" button should be shown */
    private boolean showBrowseButton = DEFAULT_SHOW_BROWSE_BUTTON;

    /** The width of the image */
    private int imageWidth = TemplateWidget.DEFAULT_SIZE;

    /** The height of the image */
    private int imageHeight = TemplateWidget.DEFAULT_SIZE;


    /**
     * Creates a new instance of TemplateImage.
     *
     * @param parent
     *      the parent element
     */
    public TemplateImage( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Gets the height of the image.
     *
     * @return
     *      the height of the image
     */
    public int getImageHeight()
    {
        return imageHeight;
    }


    /**
     * Gets the image data.
     *
     * @return
     *      the image data
     */
    public String getImageData()
    {
        return imageData;
    }


    /**
     * Gets the width of the image.
     *
     * @return
     *      the width of the image
     */
    public int getImageWidth()
    {
        return imageWidth;
    }


    /**
     * Indicates if a "<em>Browse...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Browse...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowBrowseButton()
    {
        return showBrowseButton;
    }


    /**
     * Indicates if a "<em>Clear</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Clear</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowClearButton()
    {
        return showClearButton;
    }


    /**
     * Indicates if a "<em>Save As...</em>" button should be shown.
     *
     * @return
     *      <code>true</code> if a "<em>Save As...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public boolean isShowSaveAsButton()
    {
        return showSaveAsButton;
    }


    /**
     * Sets the height of the image.
     *
     * @param imageHeight
     *      height of the image
     */
    public void setImageHeight( int imageHeight )
    {
        this.imageHeight = imageHeight;
    }


    /**
     * Sets the image data.
     *
     * @param imageData
     *      the image data
     */
    public void setImageData( String imageData )
    {
        this.imageData = imageData;
    }


    /**
     * Sets the flag which indicates if a "<em>Browse...</em>" button should 
     * be shown.
     *
     * @param showBrowseButton
     *      <code>true</code> if a "<em>Browse...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setShowBrowseButton( boolean showBrowseButton )
    {
        this.showBrowseButton = showBrowseButton;
    }


    /**
     * Sets the flag which indicates if a "<em>Clear</em>" button should 
     * be shown.
     *
     * @param showClearButton
     *      <code>true</code> if a "<em>Clear</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setShowClearButton( boolean showClearButton )
    {
        this.showClearButton = showClearButton;
    }


    /**
     * Sets the flag which indicates if a "<em>Save As...</em>" button should 
     * be shown.
     *
     * @param showSaveAsButton
     *      <code>true</code> if a "<em>Save As...</em>" button should be 
     *      shown, <code>false</code> if not.
     */
    public void setShowSaveAsButton( boolean showSaveAsButton )
    {
        this.showSaveAsButton = showSaveAsButton;
    }


    /**
     * Sets the width of the image.
     *
     * @param imageWidth
     *      the width of the image
     */
    public void setImageWidth( int imageWidth )
    {
        this.imageWidth = imageWidth;
    }
}
