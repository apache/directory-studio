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
package org.apache.directory.studio.openldap.config.editor.overlays;



/**
 * This class implements a Module wrapper used in the 'Overlays' page UI. A Module
 * has a name, a path and an order. We also have an additional index, which is
 * the ModuleList index (this is necessary as two modules might have the same name
 * but for a different path). 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModuleWrapper
{
    /** The moduleList's name */
    private String moduleListName;
    
    /** The wrapped  moduleList index : we may have more than one moduleList */
    private int moduleListIndex;
    
    /** The wrapped Module name */
    private String moduleName;
    
    /** The wrapped module path */
    private String path;
    
    /** The wrapped module order : we may have many modules for a given path*/
    private int order;


    /**
     * Creates a new instance of ModuleWrapper.
     */
    public ModuleWrapper()
    {
    }


    /**
     * Creates a new instance of ModuleWrapper.
     *
     * @param moduleListName The ModuleList's name
     * @param moduleListIndex the module's index
     * @param module the wrapped module
     * @param path the module's path
     * @param order the module's order
     */
    public ModuleWrapper( String moduleListName, int moduleListIndex, String module, String path, int order )
    {
        this.moduleListName = moduleListName;
        this.moduleName = module;
        this.path = path;
        this.moduleListIndex = moduleListIndex;
        this.order = order;
    }


    /**
     * @return the moduleListName
     */
    public String getModuleListName()
    {
        return moduleListName;
    }


    /**
     * Gets the moduleList's index
     *
     * @return the moduleList's index
     */
    public int getModuleListIndex()
    {
        return moduleListIndex;
    }


    /**
     * Gets the wrapped module's name
     *
     * @return the wrapped module's name
     */
    public String getModuleName()
    {
        return moduleName;
    }


    /**
     * Gets the wrapped path
     *
     * @return the wrapped path
     */
    public String getPath()
    {
        return path;
    }


    /**
     * Gets the wrapped order
     *
     * @return the wrapped order
     */
    public int getOrder()
    {
        return order;
    }


    /**
     * Sets the wrapped module's name.
     *
     * @param moduleName the wrapped module's name
     */
    public void setModuleName( String moduleName )
    {
        this.moduleName = moduleName;
    }


    /**
     * Sets the moduleList's index.
     *
     * @param moduleListIndex the moduleList's index
     */
    public void setModuleIndex( int moduleListIndex )
    {
        this.moduleListIndex = moduleListIndex;
    }


    /**
     * Sets the wrapped path.
     *
     * @param path the wrapped path
     */
    public void setPath( String path )
    {
        this.path = path;
    }


    /**
     * @param moduleListName the moduleListName to set
     */
    public void setModuleListName( String moduleListName )
    {
        this.moduleListName = moduleListName;
    }


    /**
     * Sets the wrapped order.
     *
     * @param moduleListIndex the wrapped order
     */
    public void setOrder( int order )
    {
        this.order = order;
    }

    
    public String getModulePathName()
    {
        StringBuilder sb = new StringBuilder();
        
        if ( path != null )
        {
            sb.append( path ).append( "/" );
        }
        
        sb.append( "{" ).append( order ).append( '}' ).append( moduleName );
        
        return sb.toString();
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( moduleListName ).append( '{').append(  moduleListIndex ).append( '}' );

        sb.append( ':' );
        
        if ( path != null )
        {
            sb.append( path ).append( "/" );
        }
        
        sb.append( "{" ).append( order ).append( '}' ).append( moduleName );
        
        return sb.toString();
    }
}
