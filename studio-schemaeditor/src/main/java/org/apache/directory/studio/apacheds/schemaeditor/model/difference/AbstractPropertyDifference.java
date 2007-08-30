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
package org.apache.directory.studio.apacheds.schemaeditor.model.difference;


/**
 * This abstract class represents an Abstract Property Difference.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractPropertyDifference extends AbstractDifference implements PropertyDifference
{
    /** The old value*/
    private Object oldValue;

    /** The new value */
    private Object newValue;


    /**
     * Creates a new instance of AbstractPropertyDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     * @param type
     *      the type
     */
    public AbstractPropertyDifference( Object source, Object destination, DifferenceType type )
    {
        super( source, destination, type );
    }


    /**
     * Creates a new instance of AbstractPropertyDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public AbstractPropertyDifference( Object source, Object destination )
    {
        super( source, destination );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.PropertyDifference#getNewValue()
     */
    public Object getNewValue()
    {
        return newValue;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.PropertyDifference#setNewValue(java.lang.Object)
     */
    public void setNewValue( Object newValue )
    {
        this.newValue = newValue;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.PropertyDifference#getOldValue()
     */
    public Object getOldValue()
    {
        return oldValue;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.PropertyDifference#setOldValue(java.lang.Object)
     */
    public void setOldValue( Object oldValue )
    {
        this.oldValue = oldValue;
    }
}
