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


import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.utils.JobWatcher;


public class NewEntryWizardBot extends WizardBot
{
    private static final String TITLE = "New Entry";

    private EntryEditorWidgetBot widgetBot;

    public NewEntryWizardBot()
    {
        super( TITLE );
        this.widgetBot = new EntryEditorWidgetBot( bot );
    }


    @Override
    public void clickFinishButton()
    {
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__create_entry_name_1 );
        super.clickFinishButton();
        watcher.waitUntilDone();
    }


    public void selectCreateEntryFromScratch()
    {
        bot.radio( "Create entry from scratch" ).click();
    }


    public void addObjectClasses( String... objectClasses )
    {
        bot.table( 0 ).select( objectClasses );
        bot.button( "Add" ).click();
    }


    public boolean isObjectClassSelected( String objectClass )
    {
        return bot.table( 1 ).containsItem( objectClass );
    }


    public void clickAddRdnButton( int number )
    {
        int index = number - 1;
        bot.button( "  +   ", index ).click();
    }


    public void setRdnValue( int number, String text )
    {
        int index = number - 1;
        bot.text( index ).setText( text );
    }


    public void setRdnType( int number, String text )
    {
        int index = number - 1 + 1; // the parent field is also an combo box
        bot.comboBox( index ).setText( text );
    }


    public void typeValueAndFinish( String value )
    {
        widgetBot.isVisisble();
        widgetBot.typeValueAndFinish( value, false );
    }


    public NewAttributeWizardBot openNewAttributeWizard()
    {
        widgetBot.isVisisble();
        return widgetBot.openNewAttributeWizard();
    }


    public EditAttributeWizardBot editAttribute( String attributeType, String value )
    {
        widgetBot.isVisisble();
        return widgetBot.editAttribute( attributeType, value );
    }


    public void editValue( String attributeType, String value )
    {
        widgetBot.isVisisble();
        widgetBot.editValue( attributeType, value );
    }


    public void cancelEditValue()
    {
        widgetBot.isVisisble();
        widgetBot.cancelEditValue();
    }


    public String getDnPreview()
    {
        while ( true )
        {
            String text = bot.text( 1 ).getText();

            if ( Dn.isValid( text ) )
            {
                return text;
            }
        }
    }


    public ReferralDialogBot clickFinishButtonExpectingReferralDialog()
    {
        clickButton( "Finish" );
        return new ReferralDialogBot();
    }

}
