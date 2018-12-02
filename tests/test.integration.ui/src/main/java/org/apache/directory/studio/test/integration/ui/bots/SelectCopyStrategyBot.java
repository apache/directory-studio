package org.apache.directory.studio.test.integration.ui.bots;


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;


public class SelectCopyStrategyBot extends DialogBot
{
    public SelectCopyStrategyBot()
    {
        super( "Select copy strategy" );
        waitForDialog();
        activate();
    }


    public void selectStopCopyProcess()
    {
        bot.radio( "Stop copy process" ).click();
    }


    public void selectIgnoreEntryAndContinue()
    {
        bot.radio( "Ignore entry and continue" ).click();
    }


    public void selectOverwriteEntryAndContinue()
    {
        bot.radio( "Overwrite entry and continue" ).click();
    }


    public void selectRenameEntryAndContinue()
    {
        bot.radio( "Rename entry and continue" ).click();
    }


    public void setRdnValue( int number, String text )
    {
        int index = number - 1;
        bot.text( index ).setText( text );
    }


    public void setRdnType( int number, String text )
    {
        int index = number - 1;
        bot.comboBox( index ).setText( text );
    }


    public void clickOkButton()
    {
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__copy_entries_name_1 );
        super.clickOkButton();
        watcher.waitUntilDone();
    }

}
