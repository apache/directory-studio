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


import java.util.List;


/**
 * This interface defines a widget that can be used in a template.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface TemplateWidget
{
    /** The default horizontal alignment value */
    public static WidgetAlignment DEFAULT_HORIZONTAL_ALIGNMENT = WidgetAlignment.NONE;

    /** The default vertical alignment value */
    public static WidgetAlignment DEFAULT_VERTICAL_ALIGNMENT = WidgetAlignment.NONE;

    /** The default grab excess horizontal space value */
    public static boolean DEFAULT_GRAB_EXCESS_HORIZONTAL_SPACE = false;

    /** The default grab excess vertical space value */
    public static boolean DEFAULT_GRAB_EXCESS_VERTICAL_SPACE = false;

    /** The default horizontal span value */
    public static int DEFAULT_HORIZONTAL_SPAN = 1;

    /** The default vertical span value */
    public static int DEFAULT_VERTICAL_SPAN = 1;

    /** The default integer value for sizes */
    public static int DEFAULT_SIZE = -1;


    /**
     * Adds a child.
     *
     * @param widget
     *      the child
     * @return
     *      <code>true</code> (as per the general contract of the Collection.add method).
     */
    public boolean addChild( TemplateWidget widget );


    /**
     * Gets the attribute type the widget is associated with.
     *
     * @return
     *      the attribute type the widget is associated with
     */
    public String getAttributeType();


    /**
     * Gets the children.
     *
     * @return
     *      the children
     */
    public List<TemplateWidget> getChildren();


    /**
     * Gets the preferred height.
     *
     * @return
     *      the preferred height
     */
    public int getImageHeight();


    /**
     * Gets how the widget is positioned horizontally.
     *
     * @return
     *      how the widget is positioned horizontally
     */
    public WidgetAlignment getHorizontalAlignment();


    /**
     * Gets the number of columns that the widget will take up.
     *
     * @return
     *      the number of columns that the widget will take up
     */
    public int getHorizontalSpan();


    /**
     * Gets the parent element.
     *
     * @return
     *      the parent element
     */
    public TemplateWidget getParent();


    /**
     * Gets how the widget is positioned vertically.
     *
     * @return
     *      how the widget is positioned vertically
     */
    public WidgetAlignment getVerticalAlignment();


    /**
     * Gets the number of rows that the widget will take up.
     *
     * @return
     *      the number of rows that the widget will take up
     */
    public int getVerticalSpan();


    /**
     * Gets the preferred width.
     *
     * @return
     *      the preferred width
     */
    public int getImageWidth();


    /**
     * Indicates if the widget has children.
     *
     * @return
     *      <code>true</code> if the widget has children,
     *      <code>false</code> if not.
     */
    public boolean hasChildren();


    /**
     * Indicates whether the widget will be made wide 
     * enough to fit the remaining horizontal space.
     *
     * @return
     *      <code>true</code> if the widget will be made wide 
     *      enough to fit the remaining horizontal space,
     *      <code>false</code> if not
     */
    public boolean isGrabExcessHorizontalSpace();


    /**
     * Indicates whether the widget will be made wide 
     * enough to fit the remaining vertical space.
     *
     * @return
     *      <code>true</code> if the widget will be made wide 
     *      enough to fit the remaining vertical space,
     *      <code>false</code> if not
     */
    public boolean isGrabExcessVerticalSpace();


    /**
     * Sets the attribute type the widget is associated with.
     *
     * @param attributeType
     *      the attribute type the widget is associated with
     */
    public void setAttributeType( String attributeType );


    /**
     * Sets whether the widget will be made wide 
     * enough to fit the remaining horizontal space.
     *
     * @param horizontalSpan
     *      whether the widget will be made wide 
     *      enough to fit the remaining horizontal space
     */
    public void setGrabExcessHorizontalSpace( boolean grabExcessHorizontalSpace );


    /**
     * Sets whether the widget will be made wide 
     * enough to fit the remaining horizontal space.
     *
     * @param grabExcessVerticalSpace
     *      whether the widget will be made wide 
     *      enough to fit the remaining vertical space
     */
    public void setGrabExcessVerticalSpace( boolean grabExcessVerticalSpace );


    /**
     * Sets the preferred height.
     *
     * @param height
     *      the preferred height
     */
    public void setImageHeight( int height );


    /**
     * Sets how the widget is positioned horizontally.
     *
     * @param horizontalAlignment
     *      how the widget is positioned horizontally
     */
    public void setHorizontalAlignment( WidgetAlignment horizontalAlignment );


    /**
     * Sets the number of columns that the widget will take up.
     *
     * @param grabExcessHorizontalSpace
     *      the number of columns that the widget will take up
     */
    public void setHorizontalSpan( int horizontalSpan );


    /**
     * Gets how the widget is positioned vertically.
     *
     * @param verticalAlignment
     *      how the widget is positioned vertically
     */
    public void setVerticalAlignment( WidgetAlignment verticalAlignment );


    /**
     * Sets the number of rows that the widget will take up.
     *
     * @param verticalSpan
     *      the number of rows that the widget will take up
     */
    public void setVerticalSpan( int verticalSpan );


    /**
     * Sets the preferred width.
     *
     * @param width
     *      the preferred width
     */
    public void setImageWidth( int width );
}
