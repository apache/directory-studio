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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.search.SearchPageWrapper;


/**
 * This class implements the From Page of the DSML Export Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportDsmlFromWizardPage extends ExportBaseFromWizardPage
{

    /**
     * Creates a new instance of ExportDsmlFromWizardPage.
     *
     * @param pageName
     *          the name of the page
     * @param wizard
     *          the wizard the page is attached to
     */
    public ExportDsmlFromWizardPage( String pageName, ExportBaseWizard wizard )
    {
        super( pageName, wizard, new SearchPageWrapper(
            SearchPageWrapper.NAME_INVISIBLE
                | SearchPageWrapper.ALLATTRIBUTES_VISIBLE
                | SearchPageWrapper.OPERATIONALATTRIBUTES_VISIBLE
                | ( ( wizard.getSearch().getReturningAttributes() == null || wizard.getSearch()
                    .getReturningAttributes().length == 0 ) ? SearchPageWrapper.ALLATTRIBUTES_CHECKED
                    : SearchPageWrapper.NONE ) ) );
        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_EXPORT_DSML_WIZARD ) );
    }
}
