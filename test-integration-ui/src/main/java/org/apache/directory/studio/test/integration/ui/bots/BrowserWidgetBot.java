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
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


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
        if ( !bot.tree().isEnabled() )
        {
            bot.waitUntil( new DefaultCondition()
            {

                public boolean test() throws Exception
                {
                    return bot.tree().isEnabled();
                }


                public String getFailureMessage()
                {
                    return "Entry " + entry + " is not enabled!";
                }
            } );
        }
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__init_entries_title_attonly );
        entry.click();
        entry.select();
        watcher.waitUntilDone();
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


    SWTBotTree getTree()
    {
        return bot.tree();
    }

}
