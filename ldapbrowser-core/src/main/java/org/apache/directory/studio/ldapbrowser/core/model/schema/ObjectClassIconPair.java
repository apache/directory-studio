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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


/**
 * A pair of object classes to the related icon.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassIconPair
{

    /** The object classes numeric OIDs. */
    private String[] objectClassNumericOIDs;

    /** The path to the icon. */
    private String iconPath;


    /**
     * Creates a new instance of ObjectClassIconPair.
     */
    public ObjectClassIconPair()
    {
    }


    /**
     * Creates a new instance of ObjectClassIconPair.
     *
     * @param objectClassNumericOIDs the object class numeric OIDs
     * @param iconPath the icon path
     */
    public ObjectClassIconPair( String[] objectClassNumericOIDs, String iconPath )
    {
        super();
        this.objectClassNumericOIDs = objectClassNumericOIDs;
        this.iconPath = iconPath;
    }


    /**
     * Gets the object class numeric OIDs.
     * 
     * @return the object class numeric OIDs
     */
    public String[] getOcNumericOids()
    {
        return objectClassNumericOIDs;
    }


    /**
     * Sets the object class numeric OIDs.
     * 
     * @param objectClassNumericOIDs the object class numeric OIDs
     */
    public void setOcNumericOids( String[] objectClassNumericOIDs )
    {
        this.objectClassNumericOIDs = objectClassNumericOIDs;
    }


    /**
     * Gets the icon path.
     * 
     * @return the icon path
     */
    public String getIconPath()
    {
        return iconPath;
    }


    /**
     * Sets the icon path.
     * 
     * @param iconPath the new icon path
     */
    public void setIconPath( String iconPath )
    {
        this.iconPath = iconPath;
    }

}
