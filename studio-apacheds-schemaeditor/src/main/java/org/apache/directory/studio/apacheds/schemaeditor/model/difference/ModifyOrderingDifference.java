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
 * This class represents the difference of a modified ordering.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModifyOrderingDifference extends AbstractModifyDifference
{
    /**
     * Creates a new instance of ModifyOrderingDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public ModifyOrderingDifference( Object source, Object destination )
    {
        super( source, destination );
    }


    /**
     * Creates a new instance of ModifyOrderingDifference.
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
    public ModifyOrderingDifference( Object source, Object destination, Object oldValue, Object newValue )
    {
        super( source, destination, oldValue, newValue );
    }
}
