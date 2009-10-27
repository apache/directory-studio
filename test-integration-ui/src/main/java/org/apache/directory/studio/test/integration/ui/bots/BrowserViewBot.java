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
package org.apache.directory.studio.test.integration.ui.bots;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.test.integration.ui.ContextMenuHelper;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


public class BrowserViewBot
{
    private SWTWorkbenchBot bot = new SWTWorkbenchBot();


    public boolean existsEntry( String... path )
    {
        // ensure the parent exists
        String[] parentPath = new String[path.length - 1];
        System.arraycopy( path, 0, parentPath, 0, parentPath.length );
        getEntry( parentPath );

        // check if the child exists
        try
        {
            getEntry( path );
            return true;
        }
        catch ( WidgetNotFoundException e )
        {
            return false;
        }
    }


    public void selectEntry( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        select( entry );
    }


    public void selectChildrenOfEnty( String[] children, String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        entry.select( children );
    }


    public ReferralDialogBot selectEntryExpectingReferralDialog( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        select( entry );
        return new ReferralDialogBot();
    }


    public void expandEntry( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        expand( entry, true, null );
    }


    public void waitForEntry( String... path )
    {
        getEntry( path );
    }


    public ReferralDialogBot expandEntryExpectingReferralDialog( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        expand( entry, false, null );
        return new ReferralDialogBot();
    }


    public NewEntryWizardBot openNewEntryWizard()
    {
        ContextMenuHelper.clickContextMenu( getBrowserTree(), "New", "New Entry..." );
        return new NewEntryWizardBot();
    }


    public RenameEntryDialogBot openRenameDialog()
    {
        ContextMenuHelper.clickContextMenu( getBrowserTree(), "Rename Entry..." );
        return new RenameEntryDialogBot();
    }


    public DeleteDialogBot openDeleteDialog()
    {
        ContextMenuHelper.clickContextMenu( getBrowserTree(), "Delete Entries" );
        return new DeleteDialogBot();
    }


    private SWTBotTreeItem getEntry( String... path )
    {
        SWTBotTree browserTree = getBrowserTree();
        List<String> pathList = new ArrayList<String>( Arrays.asList( path ) );
        SWTBotTreeItem entry = null;

        while ( !pathList.isEmpty() )
        {
            String node = pathList.remove( 0 );

            if ( entry == null )
            {
                node = adjustNodeName( browserTree, node );
                entry = browserTree.getTreeItem( node );
            }
            else
            {
                // adjust current path, because the label is decorated with the
                // number of children
                node = adjustNodeName( entry, node );
                entry = entry.getNode( node );
            }

            if ( !pathList.isEmpty() )
            {
                // expand entry and wait till
                // - children are displayed
                // - next child is visible
                final String nextNode = !pathList.isEmpty() ? pathList.get( 0 ) : null;
                expand( entry, true, nextNode );
            }
        }

        return entry;
    }


    private void expand( final SWTBotTreeItem entry, boolean wait, final String nextNode )
    {
        UIThreadRunnable.asyncExec( bot.getDisplay(), new VoidResult()
        {
            public void run()
            {
                entry.expand();
            }
        } );

        if ( wait )
        {
            bot.waitUntil( new DefaultCondition()
            {
                public boolean test() throws Exception
                {
                    //                    if ( nextNode != null )
                    //                    {
                    //                        String adjustedNodeName = nextNode != null ? adjustNodeName( entry, nextNode ) : null;
                    //                        SWTBotTreeItem node = entry.getNode( adjustedNodeName );
                    //                        if ( node == null )
                    //                        {
                    //                            return false;
                    //                        }
                    //                    }
                    return !entry.getNodes().contains( "Fetching Entries..." )
                        && !entry.getNodes().contains( "Opening Connection..." );
                }


                public String getFailureMessage()
                {
                    return "Could not find entry " + entry.getText() + " -> " + nextNode;
                }
            } );
        }
    }


    private void select( final SWTBotTreeItem entry )
    {
        if ( !getBrowserTree().isEnabled() )
        {
            bot.waitUntil( new ICondition()
            {

                public boolean test() throws Exception
                {
                    return getBrowserTree().isEnabled();
                }


                public void init( SWTBot bot )
                {
                }


                public String getFailureMessage()
                {
                    return "Entry " + entry + " is not enabled!";
                }
            } );
        }
        entry.click();
        entry.select();
    }


    private String adjustNodeName( SWTBotTreeItem child, String nodeName )
    {
        List<String> nodes = child.getNodes();
        for ( String node : nodes )
        {
            if ( node.toUpperCase().startsWith( nodeName.toUpperCase() ) )
            {
                return node;
            }
        }
        return nodeName;
    }


    private String adjustNodeName( SWTBotTree tree, String nodeName )
    {
        SWTBotTreeItem[] allItems = tree.getAllItems();
        for ( SWTBotTreeItem item : allItems )
        {
            String node = item.getText();
            if ( node.toUpperCase().startsWith( nodeName.toUpperCase() ) )
            {
                return node;
            }
        }
        return nodeName;
    }


    private SWTBotTree getBrowserTree()
    {
        SWTBotView view = bot.viewByTitle( "LDAP Browser" );
        view.show();
        SWTBotTree tree = view.bot().tree();
        return tree;
    }

}
