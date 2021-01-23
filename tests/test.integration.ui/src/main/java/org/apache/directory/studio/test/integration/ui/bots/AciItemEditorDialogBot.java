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


import org.apache.directory.api.ldap.model.constants.AuthenticationLevel;


public class AciItemEditorDialogBot extends DialogBot
{

    public AciItemEditorDialogBot()
    {
        super( "ACI Item Editor" );
    }


    public void activateVisualEditorTab()
    {
        bot.tabItem( "Visual Editor" ).activate();
    }


    public void setIdentificationTag( String identificationTag )
    {
        bot.textWithLabel( "Identification Tag:" ).setText( identificationTag );
    }


    public void setPrecedence( int precedence )
    {
        bot.spinnerWithLabel( "Precedence:" ).setSelection( precedence );
    }


    public void setAuthenticationLevel( AuthenticationLevel authenticationLevel )
    {
        bot.comboBoxWithLabel( "Authentication Level:" ).setSelection( authenticationLevel.getName() );
    }


    public void setUserFirst()
    {
        bot.radio( "User First" ).click();
    }


    public void setItemFirst()
    {
        bot.radio( "Item First" ).click();
    }


    public void enableUserClassAllUsers()
    {
        bot.table().getTableItem( 0 ).check();
    }


    public void disableUserClassAllUsers()
    {
        bot.table().getTableItem( 0 ).uncheck();
    }


    public void enableUserClassThisEntry()
    {
        bot.table().getTableItem( 1 ).check();
    }


    public void disableUserClassThisEntry()
    {
        bot.table().getTableItem( 1 ).uncheck();
    }


    public void enableUserClassParentOfEntry()
    {
        bot.table().getTableItem( 2 ).check();
    }


    public void disableUserClassParentOfEntry()
    {
        bot.table().getTableItem( 2 ).uncheck();
    }


    public void enableUserClassName()
    {
        bot.table().getTableItem( 3 ).check();
    }


    public void disableUserClassName()
    {
        bot.table().getTableItem( 3 ).uncheck();
    }


    public void enableUserClassUserGroup()
    {
        bot.table().getTableItem( 4 ).check();
    }


    public void disableUserClassUserGroup()
    {
        bot.table().getTableItem( 4 ).uncheck();
    }


    public void enableUserClassSubtree()
    {
        bot.table().getTableItem( 5 ).check();
    }


    public void disableUserClassSubtree()
    {
        bot.table().getTableItem( 5 ).uncheck();
    }


    public void enableProtectedItemEntry()
    {
        bot.table().getTableItem( 0 ).check();
    }


    public void disableProtectedItemEntry()
    {
        bot.table().getTableItem( 0 ).uncheck();
    }


    public void enableProtectedItemAllUserAttributeTypes()
    {
        bot.table().getTableItem( 1 ).check();
    }


    public void disableProtectedItemAllUserAttributeTypes()
    {
        bot.table().getTableItem( 1 ).uncheck();
    }


    public void enableProtectedItemAttributeType()
    {
        bot.table().getTableItem( 2 ).check();
    }


    public void disableProtectedItemAttributeType()
    {
        bot.table().getTableItem( 2 ).uncheck();
    }


    public void enableProtectedItemAllAttributeValues()
    {
        bot.table().getTableItem( 3 ).check();
    }


    public void disableProtectedItemAllAttributeValues()
    {
        bot.table().getTableItem( 3 ).uncheck();
    }


    public void enableProtectedItemAllUserAttributeTypesAndValues()
    {
        bot.table().getTableItem( 4 ).check();
    }


    public void disableProtectedItemAllUserAttributeTypesAndValues()
    {
        bot.table().getTableItem( 4 ).uncheck();
    }


    public void enableProtectedItemAttributeValues()
    {
        bot.table().getTableItem( 5 ).check();
    }


    public void disableProtectedItemAttributeValues()
    {
        bot.table().getTableItem( 5 ).uncheck();
    }


    public void enableProtectedItemSelfValue()
    {
        bot.table().getTableItem( 6 ).check();
    }


    public void disableProtectedItemSelfValue()
    {
        bot.table().getTableItem( 6 ).uncheck();
    }


    public void enableProtectedItemRangeOfValues()
    {
        bot.table().getTableItem( 7 ).check();
    }


    public void disableProtectedItemRangeOfValues()
    {
        bot.table().getTableItem( 7 ).uncheck();
    }


    public void enableProtectedItemMaxValueCount()
    {
        bot.table().getTableItem( 8 ).check();
    }


    public void disableProtectedItemMaxValueCount()
    {
        bot.table().getTableItem( 8 ).uncheck();
    }


    public void enableProtectedItemMaxNumberOfImmediateSubordinates()
    {
        bot.table().getTableItem( 9 ).check();
    }


    public void disableProtectedItemMaxNumberOfImmediateSubordinates()
    {
        bot.table().getTableItem( 9 ).uncheck();
    }


    public void enableProtectedItemRestrictedBy()
    {
        bot.table().getTableItem( 10 ).check();
    }


    public void disableProtectedItemRestrictedBy()
    {
        bot.table().getTableItem( 10 ).uncheck();
    }


    public void enableProtectedItemClasses()
    {
        bot.table().getTableItem( 11 ).check();
    }


    public void disableProtectedItemClasses()
    {
        bot.table().getTableItem( 11 ).uncheck();
    }


    public void activateSourceTab()
    {
        bot.tabItem( "Source" ).activate();
    }


    public void setSource( String source )
    {
        bot.styledText().setText( source );
    }


    public String getSource()
    {
        return bot.styledText().getText();
    }


    public void clickFormatButton()
    {
        super.clickButton( "Format" );
    }


    public void clickCheckSyntaxButtonOk()
    {
        super.clickButton( "Check Syntax" );
        new DialogBot( "Syntax ok" )
        {
        }.clickOkButton();
        activate();
    }


    public void clickCheckSyntaxButtonError()
    {
        String shellText = BotUtils.shell( () -> super.clickButton( "Check Syntax" ), "Syntax Error" ).getText();
        new DialogBot( shellText )
        {
        }.clickOkButton();
        activate();
    }

}
