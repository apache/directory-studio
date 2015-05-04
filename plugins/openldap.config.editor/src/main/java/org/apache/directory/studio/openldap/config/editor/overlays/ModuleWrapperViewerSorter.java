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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * This class defines a sorter for a ModuleList wrapper viewer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModuleWrapperViewerSorter extends ViewerSorter
{
    public int compare( Viewer viewer, Object e1, Object e2 )
    {
        if ( ( e1 != null ) && ( e2 != null ) && ( e1 instanceof ModuleWrapper )
            && ( e2 instanceof ModuleWrapper ) )
        {
            ModuleWrapper module1 = (ModuleWrapper)e1;
            ModuleWrapper module2 = (ModuleWrapper)e2;
            
            if ( e1 == e2 )
            {
                // Short circuit...
                return 0;
            }
            
            // First, compare the moduleList
            int comp = module1.getModuleListName().compareToIgnoreCase( module2.getModuleListName() );
            
            if ( comp == 0 )
            {
                // Same ModuleList. Check the index
                if ( module1.getModuleListIndex() == module2.getModuleListIndex() )
                {
                    // Same index : check the modules' order
                    if ( module1.getOrder() > module2.getOrder() )
                    {
                        return 1;
                    }
                    else
                    {
                        return -1;
                    }
                }
                else
                {
                    // We can get out
                    if ( module1.getModuleListIndex() > module2.getModuleListIndex() )
                    {
                        return 1;
                    }
                    else
                    {
                        return -1;
                    }
                }
            }
            else
            {
                // The are different, we can get out
                return comp;
            }
        }

        return super.compare( viewer, e1, e2 );
    }
}
