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
 * This class implements a template section.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateSection extends AbstractTemplateWidget
{
    /** The default number of columns value */
    public static int DEFAULT_NUMBER_OF_COLUMNS = 1;

    /** The default equals columns value */
    public static boolean DEFAULT_EQUAL_COLUMNS = false;

    /** The default expandable value */
    public static boolean DEFAULT_EXPANDABLE = false;

    /** The default expanded value */
    public static boolean DEFAULT_EXPANDED = true;

    /** The default title value */
    public static String DEFAULT_TITLE = null;

    /** The default description value */
    public static String DEFAULT_DESCRIPTION = null;

    /** The number of columns of the layout */
    private int numberOfColumns = DEFAULT_NUMBER_OF_COLUMNS;

    /** The flag indicating if all columns are equal in width size */
    private boolean equalColumns = DEFAULT_EQUAL_COLUMNS;

    /** The flag indicating if the section is expandable */
    private boolean expandable = DEFAULT_EXPANDABLE;

    /** The flag indicating if the section is expanded */
    private boolean expanded = DEFAULT_EXPANDED;

    /** The title */
    private String title = DEFAULT_TITLE;

    /** The description */
    private String description = DEFAULT_DESCRIPTION;


    /**
     * Creates a new instance of TemplateSection.
     *
     * @param parent
     *      the parent element
     */
    public TemplateSection( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Gets the description.
     *
     * @return
     *      the description
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Gets the number of columns.
     *
     * @return
     *      the number of columns
     */
    public int getNumberOfColumns()
    {
        return numberOfColumns;
    }


    /**
     * Gets the title.
     *
     * @return
     *      the title
     */
    public String getTitle()
    {
        return title;
    }


    /**
     * Indicates if the columns are equals in width.
     *
     * @return
     *      <code>true</code> if the columns are equals in width,
     *      <code>false</code> if not
     */
    public boolean isEqualColumns()
    {
        return equalColumns;
    }


    /**
     * Indicates if the section is expandable.
     *
     * @return
     *      <code>true</code> if the section is expandable,
     *      <code>false</code> if not
     */
    public boolean isExpandable()
    {
        return expandable;
    }


    /**
     * Indicates if the section is expanded.
     *
     * @return
     *      <code>true</code> if the section is expanded,
     *      <code>false</code> if not
     */
    public boolean isExpanded()
    {
        return expanded;
    }


    /**
     * Sets the description.
     *
     * @param description
     *      the description
     */
    public void setDescription( String description )
    {
        this.description = description;
    }


    /**
     * Sets the flag that indicates if the columns are equals in width.
     *
     * @param equalColumns
     *      the flag that indicates if the columns are equals in width
     */
    public void setEqualColumns( boolean equalColumns )
    {
        this.equalColumns = equalColumns;
    }


    /**
     * Sets the flag that indicates if the section is expandable.
     *
     * @param expandable
     *      the flag that indicates if the section is expandable
     */
    public void setExpandable( boolean expandable )
    {
        this.expandable = expandable;
    }


    /**
     * Sets the flag that indicates if the section is expanded.
     *
     * @param expanded
     *      the flag that indicates if the section is expanded
     */
    public void setExpanded( boolean expanded )
    {
        this.expanded = expanded;
    }


    /**
     * Sets the number of columns.
     *
     * @param numberOfColumns
     *      the number of columns
     */
    public void setNumberOfColumns( int numberOfColumns )
    {
        this.numberOfColumns = numberOfColumns;
    }


    /**
     * Sets the title.
     *
     * @param title
     *      the title
     */
    public void setTitle( String title )
    {
        this.title = title;
    }
}
