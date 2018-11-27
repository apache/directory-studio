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
 * This class implements a template composite.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateComposite extends AbstractTemplateWidget
{
    /** The default number of columns value */
    public static int DEFAULT_NUMBER_OF_COLUMNS = 1;

    /** The default equal columns value */
    public static boolean DEFAULT_EQUAL_COLUMNS = false;

    /** The number of columns of the layout */
    private int numberOfColumns = DEFAULT_NUMBER_OF_COLUMNS;

    /** The flag indicating if all columns are equal in width size */
    private boolean equalColumns = DEFAULT_EQUAL_COLUMNS;


    /**
     * Creates a new instance of TemplateComposite.
     *
     * @param parent
     *      the parent element
     */
    public TemplateComposite( TemplateWidget parent )
    {
        super( parent );
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
     * Sets the number of columns.
     *
     * @param numberOfColumns
     *      the number of columns
     */
    public void setNumberOfColumns( int numberOfColumns )
    {
        this.numberOfColumns = numberOfColumns;
    }
}
