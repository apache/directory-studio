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


import java.util.ArrayList;
import java.util.List;


/**
 * This class implements an abstract widget for templates.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractTemplateWidget implements TemplateWidget
{
    /** The parent element*/
    private TemplateWidget parent;

    /** The children list */
    private List<TemplateWidget> children;

    /** The attribute type the widget is associated with */
    private String attributeType;

    /** How the widget is positioned horizontally */
    private WidgetAlignment horizontalAlignment = DEFAULT_HORIZONTAL_ALIGNMENT;

    /** How the widget is positioned vertically */
    private WidgetAlignment verticalAlignment = DEFAULT_VERTICAL_ALIGNMENT;

    /** 
     * The flag to know whether the widget will be made wide 
     * enough to fit the remaining horizontal space.
     */
    private boolean grabExcessHorizontalSpace = DEFAULT_GRAB_EXCESS_HORIZONTAL_SPACE;

    /** 
     * The flag to know whether the widget will be made wide 
     * enough to fit the remaining vertical space.
     */
    private boolean grabExcessVerticalSpace = DEFAULT_GRAB_EXCESS_VERTICAL_SPACE;

    /** The number of columns that the widget will take up */
    private int horizontalSpan = DEFAULT_HORIZONTAL_SPAN;

    /** The number of rows that the widget will take up */
    private int verticalSpan = DEFAULT_VERTICAL_SPAN;

    /** The preferred width */
    private int width = DEFAULT_SIZE;

    /** The preferred height*/
    private int height = DEFAULT_SIZE;


    /**
     * Creates a new instance of AbstractTemplateWidget.
     *
     * @param parent
     *      the parent element
     */
    public AbstractTemplateWidget( TemplateWidget parent )
    {
        this.parent = parent;
        children = new ArrayList<TemplateWidget>();

        if ( parent != null )
        {
            parent.addChild( this );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean addChild( TemplateWidget widget )
    {
        return children.add( widget );
    }


    /**
     * {@inheritDoc}
     */
    public String getAttributeType()
    {
        return attributeType;
    }


    /**
     * {@inheritDoc}
     */
    public List<TemplateWidget> getChildren()
    {
        return children;
    }


    /**
     * {@inheritDoc}
     */
    public int getImageHeight()
    {
        return height;
    }


    /**
     * {@inheritDoc}
     */
    public WidgetAlignment getHorizontalAlignment()
    {
        return horizontalAlignment;
    }


    /**
     * {@inheritDoc}
     */
    public int getHorizontalSpan()
    {
        return horizontalSpan;
    }


    /**
     * {@inheritDoc}
     */
    public TemplateWidget getParent()
    {
        return parent;
    }


    /**
     * {@inheritDoc}
     */
    public WidgetAlignment getVerticalAlignment()
    {
        return verticalAlignment;
    }


    /**
     * {@inheritDoc}
     */
    public int getVerticalSpan()
    {
        return verticalSpan;
    }


    /**
     * {@inheritDoc}
     */
    public int getImageWidth()
    {
        return width;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasChildren()
    {
        return children.size() > 0;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isGrabExcessHorizontalSpace()
    {
        return grabExcessHorizontalSpace;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isGrabExcessVerticalSpace()
    {
        return grabExcessVerticalSpace;
    }


    /**
     * {@inheritDoc}
     */
    public void setAttributeType( String attributeType )
    {
        this.attributeType = attributeType;
    }


    /**
     * {@inheritDoc}
     */
    public void setGrabExcessHorizontalSpace( boolean grabExcessHorizontalSpace )
    {
        this.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
    }


    /**
     * {@inheritDoc}
     */
    public void setGrabExcessVerticalSpace( boolean grabExcessVerticalSpace )
    {
        this.grabExcessVerticalSpace = grabExcessVerticalSpace;
    }


    /**
     * {@inheritDoc}
     */
    public void setImageHeight( int height )
    {
        this.height = height;
    }


    /**
     * {@inheritDoc}
     */
    public void setHorizontalAlignment( WidgetAlignment horizontalAlignment )
    {
        this.horizontalAlignment = horizontalAlignment;
    }


    /**
     * {@inheritDoc}
     */
    public void setHorizontalSpan( int horizontalSpan )
    {
        this.horizontalSpan = horizontalSpan;
    }


    /**
     * {@inheritDoc}
     */
    public void setVerticalAlignment( WidgetAlignment verticalAlignment )
    {
        this.verticalAlignment = verticalAlignment;
    }


    /**
     * {@inheritDoc}
     */
    public void setVerticalSpan( int verticalSpan )
    {
        this.verticalSpan = verticalSpan;
    }


    /**
     * {@inheritDoc}
     */
    public void setImageWidth( int width )
    {
        this.width = width;
    }
}
