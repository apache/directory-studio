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
 * This abstract class extends AbstractDifference and implements ModifyDifference
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractModifyDifference extends AbstractDifference implements ModifyDifference
{
    /** The old value */
    private Object oldValue;

    /** The new value */
    private Object newValue;


    /**
     * Creates a new instance of AbstractModifyDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public AbstractModifyDifference( Object source, Object destination )
    {
        super( source, destination );
    }


    /**
     * Creates a new instance of AbstractModifyDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     * @param oldValue
     *      the old value
     * @param newValue
     *      the new value
     */
    public AbstractModifyDifference( Object source, Object destination, Object oldValue, Object newValue )
    {
        super( source, destination );
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyDifference#getNewValue()
     */
    public Object getNewValue()
    {
        return newValue;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyDifference#getOldValue()
     */
    public Object getOldValue()
    {
        return oldValue;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyDifference#setNewValue(java.lang.Object)
     */
    public void setNewValue( Object newValue )
    {
        this.newValue = newValue;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyDifference#setOldValue(java.lang.Object)
     */
    public void setOldValue( Object oldValue )
    {
        this.oldValue = oldValue;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return this.getClass().getSimpleName() + " - old value:" + oldValue + " - new value:" + newValue;
    }
}
