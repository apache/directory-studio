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
import org.apache.directory.studio.test.integration.ui.ContextMenuHelper;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
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


    void typeValueAndFinish( String value, boolean wait )
    {
        SWTBotText text = bot.text( 1 );
        text.setText( value );

        if ( wait )
        {
            JobWatcher jobWatcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
            bot.tree().pressShortcut( Keystrokes.LF );
            jobWatcher.waitUntilDone();
        }
        else
        {
            bot.tree().pressShortcut( Keystrokes.LF );
        }
    }


    void cancelEditValue()
    {
        SWTBotTree tree = bot.tree( 0 );
        // TODO: Workaround for DIRAPI-228/DIRAPI-229
        //tree.getTreeItem( "objectClass" ).click();
        SWTBotTreeItem[] allItems = tree.getAllItems();
        for ( SWTBotTreeItem item : allItems )
        {
            if ( "objectclass".equalsIgnoreCase( item.getText() ) )
            {
                item.click();
                return;
            }
        }
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


    void editValueWith( String attributeType, String value, String valueEditorLabel )
    {
        cancelEditValue();
        SWTBotTreeItem treeItem = getTreeItem( attributeType, value );
        treeItem.select();
        ContextMenuHelper.clickContextMenu( bot.tree(), "Edit Value With", valueEditorLabel );
    }


    DnEditorDialogBot editValueExpectingDnEditor( String attributeType, String value )
    {
        editValue( attributeType, value );
        return new DnEditorDialogBot();
    }


    PasswordEditorDialogBot editValueExpectingPasswordEditor( String attributeType, String value )
    {
        editValue( attributeType, value );
        return new PasswordEditorDialogBot();
    }


    TextEditorDialogBot editValueWithTextEditor( String attributeType, String value )
    {
        editValueWith( attributeType, value, "^Text Editor$" );
        return new TextEditorDialogBot();
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


    private List<SWTBotTreeItem> getTreeItems( String... attributeTypes )
    {
        List<String> attributeTypeList = Arrays.asList( attributeTypes );
        List<SWTBotTreeItem> items = new ArrayList<>();
        SWTBotTree tree = bot.tree();
        SWTBotTreeItem[] allItems = tree.getAllItems();
        for ( SWTBotTreeItem item : allItems )
        {
            if ( attributeTypeList.contains( item.cell( 0 ) ) )
            {
                items.add( item );
            }
        }
        return items;
    }


    void deleteValue( String attributeType, String value )
    {
        SWTBotTreeItem treeItem = getTreeItem( attributeType, value );
        treeItem.select();
        ContextMenuHelper.clickContextMenu( bot.tree(), "Delete Value" );
        DeleteDialogBot deleteDialogBot = new DeleteDialogBot( DeleteDialogBot.DELETE_VALUE_TITLE );
        deleteDialogBot.clickOkButton();
    }


    public void copyValue( String attributeType, String value )
    {

        SWTBotTreeItem treeItem = getTreeItem( attributeType, value );
        treeItem.select();
        ContextMenuHelper.clickContextMenu( bot.tree(), "Copy Value" );
    }


    public void copyValues( String... attributeTypes )
    {
        List<SWTBotTreeItem> items = getTreeItems( attributeTypes );
        bot.tree().select( items.toArray( new SWTBotTreeItem[0] ) );
        ContextMenuHelper.clickContextMenu( bot.tree(), "Copy Values" );
    }


    public void pasteValue()
    {
        ContextMenuHelper.clickContextMenu( bot.tree(), "Paste Value" );
    }


    public void pasteValues()
    {
        ContextMenuHelper.clickContextMenu( bot.tree(), "Paste Values" );
    }

}
