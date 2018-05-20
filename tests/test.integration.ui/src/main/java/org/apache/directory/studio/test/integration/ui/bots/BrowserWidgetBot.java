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

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
class BrowserWidgetBot
{
    private SWTBot bot;


    BrowserWidgetBot( SWTBot bot )
    {
        this.bot = bot;
    }


    boolean existsEntry( String... path )
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


    void selectEntry( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        select( entry );
    }


    void selectChildrenOfEnty( String[] children, String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        entry.select( children );
    }


    ReferralDialogBot selectEntryExpectingReferralDialog( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        select( entry );
        return new ReferralDialogBot();
    }


    void expandEntry( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        expand( entry, true, null );
    }


    void waitForEntry( String... path )
    {
        getEntry( path );
    }


    ReferralDialogBot expandEntryExpectingReferralDialog( String... path )
    {
        SWTBotTreeItem entry = getEntry( path );
        expand( entry, false, null );
        return new ReferralDialogBot();
    }


    String getSelectedEntry()
    {
        return getTree().selection().get( 0 ).get( 0 );
    }


    private SWTBotTreeItem getEntry( String... path )
    {
        SWTBotTree browserTree = bot.tree();
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
                entry = getChild(entry, node);
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
                if ( !entry.isExpanded() )
                {
                    entry.expand();
                }
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
        if ( !bot.tree().isEnabled() )
        {
            bot.waitUntil( Conditions.widgetIsEnabled( bot.tree() ) );
        }
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__init_entries_title_attonly );
        entry.click();
        entry.select();
        watcher.waitUntilDone();
    }


    private SWTBotTreeItem getChild( SWTBotTreeItem entry, String nodeName )
    {
        // adjust current path, because the label is decorated with the number of children
        bot.waitUntil( new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                String adjustedNodeName = adjustNodeName( entry, nodeName );
                return adjustedNodeName != null;
            }


            @Override
            public String getFailureMessage()
            {
                return "Node " + nodeName + " not found";
            }
        } );

        String adjustedNodeName = adjustNodeName( entry, nodeName );
        return entry.getNode( adjustedNodeName );
    }


    private String adjustNodeName( SWTBotTreeItem entry, String nodeName )
    {
        List<String> nodes = entry.getNodes();
        for ( String node : nodes )
        {
            if ( node.toUpperCase().startsWith( nodeName.toUpperCase() ) )
            {
                return node;
            }
        }
        return null;
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


    SWTBotTree getTree()
    {
        return bot.tree();
    }


    public void waitUntilEntryIsSelected( String label )
    {
        bot.waitUntil( new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                String selectedEntry = getSelectedEntry();
                return selectedEntry.equals( label );
            }


            @Override
            public String getFailureMessage()
            {
                return "Entry " + label + " was not selected, but " + getSelectedEntry();
            }
        } );
    }

}
