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


public class NewAttributeWizardBot extends WizardBot
{

    public NewAttributeWizardBot()
    {
        super( "New Attribute" );
    }


    public void typeAttributeType( String text )
    {
        bot.comboBox().setText( text );
    }


    public void setLanguageTag( String lang, String country )
    {
        bot.comboBox( 0 ).setText( lang );
        bot.comboBox( 1 ).setText( country );
    }


    public void selectBinaryOption()
    {
        bot.checkBox().select();
    }


    public DnEditorDialogBot clickFinishButtonExpectingDnEditor()
    {
        clickFinishButton();
        return new DnEditorDialogBot();
    }


    public PasswordEditorDialogBot clickFinishButtonExpectingPasswordEditor()
    {
        clickFinishButton();
        return new PasswordEditorDialogBot();
    }


    public ImageEditorDialogBot clickFinishButtonExpectingImageEditor()
    {
        clickFinishButton();
        return new ImageEditorDialogBot();
    }


    public CertificateEditorDialogBot clickFinishButtonExpectingCertificateEditor()
    {
        clickFinishButton();
        return new CertificateEditorDialogBot();
    }


    public HexEditorDialogBot clickFinishButtonExpectingHexEditor()
    {
        clickFinishButton();
        return new HexEditorDialogBot();
    }


    public AddressEditorDialogBot clickFinishButtonExpectingAddressEditor()
    {
        clickFinishButton();
        return new AddressEditorDialogBot();
    }

}
