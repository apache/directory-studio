package org.apache.directory.studio.test.integration.ui.bots;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.test.integration.ui.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


class EntryEditorWidgetBot
{
    private SWTBot bot;


    EntryEditorWidgetBot( SWTBot bot )
    {
        this.bot = bot;
    }


    boolean isVisisble()
    {
        return bot.tree() != null;
    }


    List<String> getAttributeValues()
    {
        SWTBotTree tree = bot.tree();
        List<String> attributes = new ArrayList<String>();
        int rowCount = tree.rowCount();
        for ( int i = 0; i < rowCount; i++ )
        {
            String attribute = tree.cell( i, 0 );
            String value = tree.cell( i, 1 );
            attributes.add( attribute + ": " + value );
        }
        return attributes;
    }


    NewAttributeWizardBot openNewAttributeWizard()
    {
        ContextMenuHelper.clickContextMenu( bot.tree(), "New Attribute..." );
        return new NewAttributeWizardBot();
    }


    void typeValueAndFinish( String value )
    {
        SWTBotPreferences.KEYBOARD_LAYOUT = "org.eclipse.swtbot.swt.finder.keyboard.EN_US";
        SWTBotText text = bot.text( 1 );
        text.setText( value );
        bot.tree().pressShortcut( Keystrokes.LF );
    }


    void cancelEditValue()
    {
        SWTBotTree tree = bot.tree( 0 );
        tree.getTreeItem( "objectClass" ).click();
    }


    void addValue( String attributeType )
    {
        SWTBotTree tree = bot.tree();
        tree.getTreeItem( attributeType ).click();
        ContextMenuHelper.clickContextMenu( bot.tree(), "New Value" );
    }


    void editValue( String attributeType, String value )
    {
        cancelEditValue();
        SWTBotTreeItem treeItem = getTreeItem( attributeType, value );
        treeItem.doubleClick();
    }


    DnEditorDialogBot editValueExpectingDnEditor( String attributeType, String value )
    {
        editValue( attributeType, value );
        return new DnEditorDialogBot();
    }


    private SWTBotTreeItem getTreeItem( String attributeType, String value )
    {
        SWTBotTree tree = bot.tree();
        SWTBotTreeItem[] allItems = tree.getAllItems();
        for ( SWTBotTreeItem item : allItems )
        {
            if ( item.cell( 0 ).equals( attributeType ) && item.cell( 1 ).equals( value ) )
            {
                return item;
            }
        }
        throw new WidgetNotFoundException( "Attribute " + attributeType + ":" + value + " not found." );
    }


    void deleteValue( String attributeType, String value )
    {
        SWTBotTreeItem treeItem = getTreeItem( attributeType, value );
        treeItem.click();
        ContextMenuHelper.clickContextMenu( bot.tree(), "Delete Value" );
        DeleteDialogBot deleteDialogBot = new DeleteDialogBot( DeleteDialogBot.DELETE_VALUE_TITLE );
        deleteDialogBot.clickOkButton();
    }

}
