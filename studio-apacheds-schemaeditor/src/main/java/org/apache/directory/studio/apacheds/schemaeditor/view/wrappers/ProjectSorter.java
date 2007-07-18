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

package org.apache.directory.studio.apacheds.schemaeditor.view.wrappers;


import java.util.Comparator;


/**
 * This class is used to compare and sort ascending two TreeNode
 */
public class ProjectSorter implements Comparator<TreeNode>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( TreeNode tn1, TreeNode tn2 )
    {
        if ( ( tn1 instanceof ProjectWrapper ) && ( tn2 instanceof ProjectWrapper ) )
        {
            ProjectWrapper pw1 = ( ProjectWrapper ) tn1;
            ProjectWrapper pw2 = ( ProjectWrapper ) tn2;

            return pw1.getProject().getName().compareToIgnoreCase( pw2.getProject().getName() );
        }

        // Default
        return 0;
    }
}
