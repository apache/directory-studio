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
package org.apache.directory.studio.test.integration.ui.bots.utils;


import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


public class TreeBot
{
    private SWTBotTree tree;


    public TreeBot( SWTBotTree tree )
    {
        this.tree = tree;
    }


    public boolean exists( String... nodes )
    {
        SWTBotTreeItem[] items = tree.getAllItems();
        return exists( items, new LinkedList<String>( Arrays.asList( nodes ) ) );
    }


    private boolean exists( SWTBotTreeItem[] items, LinkedList<String> nodes )
    {
        for ( SWTBotTreeItem item : items )
        {
            if ( item.getText().equals( nodes.getFirst() ) )
            {
                if ( nodes.size() == 1 )
                {
                    return true;
                }
                else
                {
                    if ( !item.isExpanded() )
                    {
                        item.expand();
                    }
                    nodes.removeFirst();
                    SWTBotTreeItem[] children = item.getItems();
                    return exists( children, nodes );
                }
            }
        }

        return false;
    }
}
