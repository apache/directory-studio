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


import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;


public class SchemaViewBot
{
    private static final String ATTRIBUTE_TYPES = "Attribute Types";
    private static final String OBJECT_CLASSES = "Object Classes";

    private SWTWorkbenchBot bot = new SWTWorkbenchBot();


    public SchemaViewBot()
    {
        SWTBotView view = bot.viewByTitle( "Schema" );
        view.show();
    }


    private SWTBotTree getSchemaTree()
    {
        SWTBotView view = bot.viewByTitle( "Schema" );
        view.show();
        SWTBotTree tree = view.bot().tree();
        return tree;
    }


    public boolean existsSchema( String schema )
    {
        SWTBotTreeItem item = getSchemaTree().getTreeItem( schema );
        return true;
    }


    public void selectObjectClass( String schema, String objectClass )
    {
        selectSchemaElement( schema, OBJECT_CLASSES, objectClass );
    }


    public void selectAttributeType( String schema, String attributeType )
    {
        selectSchemaElement( schema, ATTRIBUTE_TYPES, attributeType );
    }


    private void selectSchemaElement( String schema, String type, String schemaElement )
    {
        SWTBotTreeItem item = getSchemaElementTreeItem( schema, type, schemaElement );
        item.select();
    }


    public boolean existsObjectClass( String schema, String objectClass )
    {
        return existsSchemaElement( schema, OBJECT_CLASSES, objectClass );
    }


    public boolean existsAttributeType( String schema, String attributeType )
    {
        return existsSchemaElement( schema, ATTRIBUTE_TYPES, attributeType );
    }


    private boolean existsSchemaElement( String schema, String type, String schemaElement )
    {
        SWTBotTreeItem item = getSchemaElementTreeItem( schema, type, schemaElement );
        return item != null;
    }


    private SWTBotTreeItem getSchemaElementTreeItem( String schema, String type, String schemaElement )
    {
        SWTBotTreeItem schemaItem = getSchemaTree().getTreeItem( schema );
        schemaItem.expand();
        SWTBotTreeItem[] typeItems = schemaItem.getItems();
        System.out.println( Arrays.asList( typeItems ) );
        for ( SWTBotTreeItem typeItem : typeItems )
        {
            if ( typeItem.getText().startsWith( type ) )
            {
                typeItem.expand();
                SWTBotTreeItem[] elementItems = typeItem.getItems();
                System.out.println( Arrays.asList( elementItems ) );
                for ( SWTBotTreeItem elementItem : elementItems )
                {
                    if ( elementItem.getText().startsWith( schemaElement + "  [" ) )
                    {
                        return elementItem;
                    }
                }
            }
        }

        return null;
    }


    public ObjectClassEditorBot openObjectClassEditor( String schema, String objectClass )
    {
        selectObjectClass( schema, objectClass );
        getSchemaTree().contextMenu( "Open" ).click();
        return new ObjectClassEditorBot( objectClass );
    }


    public AttributeTypeEditorBot openAttributeTypeEditor( String schema, String attributeType )
    {
        selectAttributeType( schema, attributeType );
        getSchemaTree().contextMenu( "Open" ).click();
        return new AttributeTypeEditorBot( attributeType );
    }


    public SchemaEditorBot openSchemaEditor( String schema )
    {
        getSchemaTree().select( schema ).contextMenu( "Open" ).click();
        return new SchemaEditorBot( schema );
    }

}
