package org.apache.directory.studio.test.integration.ui.bots;


import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;


public class BaseSchemaEditorBot
{
    protected SWTBotMultiPageEditor editor;


    public BaseSchemaEditorBot( String title )
    {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        editor = bot.multipageEditorByTitle( title );
        bot.waitUntil( new DefaultCondition()
        {
            @Override
            public boolean test() throws Exception
            {
                return editor.getPageCount() >= 2;
            }


            @Override
            public String getFailureMessage()
            {
                return "Schema editor not ready";
            }
        } );
    }


    public void activateSourceCodeTab()
    {
        editor.activatePage( "Source Code" );
    }

    public String getSourceCode()
    {
        activateSourceCodeTab();
        return editor.bot().styledText().getText();
    }

}
