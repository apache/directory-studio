package org.apache.directory.studio.test.integration.ui.bots;


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;


public class SelectCopyDepthDialogBot extends DialogBot
{
    private String objectText;
    private String oneLevelText;
    private String subTreeText;
    private String jobName;


    public SelectCopyDepthDialogBot( int numEntries )
    {
        super( "Select Copy Depth" );
        if ( numEntries > 1 )
        {
            objectText = "Object (Only the copied entries)";
            oneLevelText = "One Level (Only copied entries and their direct children)";
            subTreeText = "Subtree (The whole subtrees)";
            jobName = BrowserCoreMessages.jobs__copy_entries_name_n;
        }
        else
        {
            objectText = "Object (Only the copied entry)";
            oneLevelText = "One Level (Only copied entry and its direct children)";
            subTreeText = "Subtree (The whole subtree)";
            jobName = BrowserCoreMessages.jobs__copy_entries_name_1;
        }
        activate();
    }


    public void selectObject()
    {
        bot.radio( objectText ).click();
    }


    public void selectOneLevel()
    {
        bot.radio( oneLevelText ).click();
    }


    public void selectSubTree()
    {
        bot.radio( subTreeText ).click();
    }


    public void clickOkButton()
    {
        JobWatcher watcher = new JobWatcher( jobName );
        super.clickOkButton();
        watcher.waitUntilDone();
    }

}
