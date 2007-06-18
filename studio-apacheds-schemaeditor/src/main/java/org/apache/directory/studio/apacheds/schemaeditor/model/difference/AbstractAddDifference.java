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
 * This abstract class extends AbstractDifference and implements AddDifference
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractAddDifference extends AbstractDifference implements AddDifference
{
    /** The value */
    private Object value;


    /**
     * Creates a new instance of AbstractAddDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public AbstractAddDifference( Object source, Object destination )
    {
        super( source, destination );
    }


    /**
     * Creates a new instance of AbstractAddDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     * @param value
     *      the value
     */
    public AbstractAddDifference( Object source, Object destination, Object value )
    {
        super( source, destination );
        this.value = value;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddDifference#getValue()
     */
    public Object getValue()
    {
        return value;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddDifference#setValue(java.lang.Object)
     */
    public void setValue( Object value )
    {
        this.value = value;
    }
}
