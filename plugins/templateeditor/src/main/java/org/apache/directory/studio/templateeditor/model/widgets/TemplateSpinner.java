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
 * This class implements a template spinner.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateSpinner extends AbstractTemplateWidget
{
    /** The default minimum value */
    public static int DEFAULT_MINIMUM = Integer.MIN_VALUE;

    /** The default maximum value */
    public static int DEFAULT_MAXIMUM = Integer.MAX_VALUE;

    /** The default increment value */
    public static int DEFAULT_INCREMENT = 1;

    /** The default page increment value */
    public static int DEFAULT_PAGE_INCREMENT = 10;

    /** The default digits value */
    public static int DEFAULT_DIGITS = 0;

    /** The minimum value */
    private int minimum = DEFAULT_MINIMUM;

    /** The maximum value */
    private int maximum = DEFAULT_MAXIMUM;

    /** The increment */
    private int increment = DEFAULT_INCREMENT;

    /** The page increment */
    private int pageIncrement = DEFAULT_PAGE_INCREMENT;

    /** The number of decimal places */
    private int digits = DEFAULT_DIGITS;


    /**
     * Creates a new instance of TemplateSpinner.
     *
     * @param parent
     *      the parent element
     */
    public TemplateSpinner( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Gets the increment.
     *
     * @return
     *      the increment
     */
    public int getIncrement()
    {
        return increment;
    }


    /**
     * Gets the maximum.
     *
     * @return
     *      the maximum
     */
    public int getMaximum()
    {
        return maximum;
    }


    /**
     * Gets the minimum.
     *
     * @return
     *      the minimum
     */
    public int getMinimum()
    {
        return minimum;
    }


    /**
     * Gets the page increment.
     *
     * @return
     *      the page increment
     */
    public int getPageIncrement()
    {
        return pageIncrement;
    }


    /**
     * Sets the increment.
     *
     * @param increment
     *      the increment
     */
    public void setIncrement( int increment )
    {
        this.increment = increment;
    }


    /**
     * Sets the maximum.
     *
     * @param maximum
     *      the maximum
     */
    public void setMaximum( int maximum )
    {
        this.maximum = maximum;
    }


    /**
     * Sets the minimum.
     *
     * @param minimum
     *      the minimum
     */
    public void setMinimum( int minimum )
    {
        this.minimum = minimum;
    }


    /**
     * Sets the page increment.
     *
     * @param pageIncrement
     *      the page increment
     */
    public void setPageIncrement( int pageIncrement )
    {
        this.pageIncrement = pageIncrement;
    }


    /**
     * Sets the number of decimal places.
     *
     * @param digits
     *      the number of decimal places
     */
    public void setDigits( int digits )
    {
        this.digits = digits;
    }


    /**
     * Gets the number of decimal places.
     *
     * @return
     *      the number of decimal places
     */
    public int getDigits()
    {
        return digits;
    }
}
