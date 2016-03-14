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
package org.apache.directory.studio.templateeditor.view.preferences;


import org.apache.directory.studio.templateeditor.model.AbstractTemplate;


/**
 * This class implements a template based on a file (i.e. stored on the disk in the plugin's folder).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PreferencesFileTemplate extends AbstractTemplate
{
    /** The absolute path to the file */
    private String filePath;


    /**
     * Gets the absolute file to the path.
     *
     * @return
     *      the asolute path to the file
     */
    public String getFilePath()
    {
        return filePath;
    }


    /**
     * Sets the absolute path to the file
     *
     * @param filePath
     *      the absolute path to the file
     */
    public void setFilePath( String filePath )
    {
        this.filePath = filePath;
    }
}