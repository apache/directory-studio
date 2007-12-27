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
package org.apache.directory.studio.schemaeditor.model.difference;


/**
 * This interface represents a property difference
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface PropertyDifference extends Difference
{
    /**
     * Gets the old value.
     *
     * @return
     *      the old value
     */
    public Object getOldValue();


    /**
     * Sets the old value.
     *
     * @param oldValue
     *      the old value
     */
    public void setOldValue( Object oldValue );


    /**
     * Gets the new value.
     *
     * @return
     *      the new value
     */
    public Object getNewValue();


    /**
     * Sets the new value.
     *
     * @param newValue
     *      the new value
     */
    public void setNewValue( Object newValue );
}
