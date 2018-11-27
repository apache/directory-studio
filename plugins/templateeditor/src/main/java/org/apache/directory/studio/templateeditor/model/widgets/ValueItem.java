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
 * This class implements a value item. A value item is composed of a value and 
 * eventually a label.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ValueItem
{
    /** The label */
    private String label;

    /** The value */
    private Object value;


    /**
     * Creates a new instance of ValueItem.
     */
    public ValueItem()
    {
    }


    /**
     * Creates a new instance of ValueItem.
     *
     * @param label
     *      the label
     */
    public ValueItem( String label )
    {
        this.label = label;
    }


    /**
     * Creates a new instance of ValueItem.
     *
     * @param value
     *      the value
     */
    public ValueItem( Object value )
    {
        this.value = value;
    }


    /**
     * Creates a new instance of ValueItem.
     *
     * @param label
     *      the label
     * @param value
     *      the value
     */
    public ValueItem( String label, Object value )
    {
        this.label = label;
        this.value = value;
    }


    /**
     * Gets the label.
     *
     * @return
     *      the label
     */
    public String getLabel()
    {
        return label;
    }


    /**
     * Sets the label.
     *
     * @param label
     *      the label
     */
    public void setLabel( String label )
    {
        this.label = label;
    }


    /**
     * Gets the value.
     *
     * @return
     *      the value
     */
    public Object getValue()
    {
        return value;
    }


    /**
     * Sets the value.
     *
     * @param value
     *      the value
     */
    public void setValue( Object value )
    {
        this.value = value;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof ValueItem )
        {
            ValueItem comparisonObject = ( ValueItem ) obj;

            // Comparing the label
            if ( ( getLabel() != null ) && ( comparisonObject.getLabel() != null ) )
            {
                if ( !getLabel().equals( comparisonObject.getLabel() ) )
                {
                    return false;
                }
            }

            // Comparing the value
            if ( ( getValue() != null ) && ( comparisonObject.getValue() != null ) )
            {
                if ( !getValue().equals( comparisonObject.getValue() ) )
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        int result = 17;

        // The label
        if ( getLabel() != null )
        {
            result = 37 * result + getLabel().hashCode();
        }

        // The value
        if ( getValue() != null )
        {
            result = 37 * result + getValue().hashCode();
        }

        return result;
    }
}
