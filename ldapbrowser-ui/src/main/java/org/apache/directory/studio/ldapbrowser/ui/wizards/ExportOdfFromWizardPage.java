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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import org.apache.directory.studio.ldapbrowser.common.widgets.search.SearchPageWrapper;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;


/**
 * This class implements the page used to select the data to export to ODF.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportOdfFromWizardPage extends ExportBaseFromWizardPage
{

    /**
     * Creates a new instance of ExportOdfFromWizardPage using a 
     * {@link SearchPageWrapper} with
     * <ul> 
     * <li>hidden name
     * <li>visible and checked return DN checkbox
     * <li>visible all attributes checkbox
     * <li>visible operational attributes checkbox
     * </ul> 
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public ExportOdfFromWizardPage( String pageName, ExportBaseWizard wizard )
    {
        super( pageName, wizard, new SearchPageWrapper(
            SearchPageWrapper.NAME_INVISIBLE
                | SearchPageWrapper.RETURN_DN_VISIBLE
                | SearchPageWrapper.RETURN_DN_CHECKED
                | SearchPageWrapper.RETURN_ALLATTRIBUTES_VISIBLE
                | SearchPageWrapper.RETURN_OPERATIONALATTRIBUTES_VISIBLE
                | ( ( wizard.getSearch().getReturningAttributes() == null || wizard.getSearch()
                    .getReturningAttributes().length == 0 ) ? SearchPageWrapper.RETURN_ALLATTRIBUTES_CHECKED
                    : SearchPageWrapper.NONE ) ) );
        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_EXPORT_ODF_WIZARD ) );
    }


    /**
     * Checks if the DNs should be exported.
     * 
     * @return true, if the DNs should be exported
     */
    public boolean isExportDn()
    {
        return spw.isReturnDn();
    }

}
