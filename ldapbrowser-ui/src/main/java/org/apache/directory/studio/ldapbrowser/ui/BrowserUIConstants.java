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

package org.apache.directory.studio.ldapbrowser.ui;


/**
 * This class contains all the constants used by the Browser UI Plugin
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface BrowserUIConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = BrowserUIPlugin.getDefault().getPluginProperties().getString( "Plugin_id" ); //$NON-NLS-1$

    public static final String DN = "DN"; //$NON-NLS-1$

    public static final String PREFERENCE_BROWSER_LINK_WITH_EDITOR = "browserLinkWithEditor"; //$NON-NLS-1$
    public static final String PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN = "searchResultEditorShowDn"; //$NON-NLS-1$
    public static final String PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS = "searchResultEditorShowLinks"; //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_MAIN = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "PrefPage_MainPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_ATTRIBUTES = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "PrefPage_AttributesPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_BINARYATTRIBUTES = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "PrefPage_BinaryAttributesAndSyntaxesPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_BROWSER = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "PrefPage_BrowserPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_ENTRYEDITOR = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "PrefPage_EntryEditorPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_SEARCHRESULTEDITOR = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "PrefPage_SearchResultEditorPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_MODIFICATIONLOGS = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "PrefPage_ModificationLogsPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_SEARCHLOGS = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "PrefPage_SearchLogsPreferencePage_id" ); //$NON-NLS-1$
    public static final String PREFERENCEPAGEID_TEXTFORMATS = BrowserUIPlugin.getDefault().getPluginProperties()
    .getString( "PrefPage_TextFormatsPreferencePage_id" ); //$NON-NLS-1$

    public static final String IMG_LINK_WITH_EDITOR = "resources/icons/link_with_editor.gif"; //$NON-NLS-1$
    public static final String IMG_BATCH = "resources/icons/batch.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT = "resources/icons/import.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT = "resources/icons/export.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_DSML_WIZARD = "resources/icons/import_dsml_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_DSML_WIZARD = "resources/icons/export_dsml_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_LDIF_WIZARD = "resources/icons/import_ldif_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_LDIF_WIZARD = "resources/icons/export_ldif_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_CONNECTIONS_WIZARD = "resources/icons/import_connections_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_CONNECTIONS_WIZARD = "resources/icons/export_connections_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_CSV_WIZARD = "resources/icons/import_csv_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_CSV_WIZARD = "resources/icons/export_csv_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_XLS_WIZARD = "resources/icons/import_xls_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_XLS_WIZARD = "resources/icons/export_xls_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_DSML = "resources/icons/import_dsml.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_DSML = "resources/icons/export_dsml.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_LDIF = "resources/icons/import_ldif.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_LDIF = "resources/icons/export_ldif.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_CONNECTIONS = "resources/icons/import_connections.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_CONNECTIONS = "resources/icons/export_connections.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_CSV = "resources/icons/import_csv.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_CSV = "resources/icons/export_csv.gif"; //$NON-NLS-1$
    public static final String IMG_IMPORT_XLS = "resources/icons/import_xls.gif"; //$NON-NLS-1$
    public static final String IMG_EXPORT_XLS = "resources/icons/export_xls.gif"; //$NON-NLS-1$
    public static final String IMG_BROWSER_CONNECTIONVIEW = "resources/icons/browser_connectionview.gif"; //$NON-NLS-1$
    public static final String IMG_BROWSER_BROWSERVIEW = "resources/icons/browser_browserview.gif"; //$NON-NLS-1$
    public static final String IMG_DIT = "resources/icons/dit.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY = "resources/icons/entry_default.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_ROOT = "resources/icons/entry_root.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_DC = "resources/icons/entry_dc.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_ORG = "resources/icons/entry_org.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_PERSON = "resources/icons/entry_person.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_GROUP = "resources/icons/entry_group.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_REF = "resources/icons/entry_ref.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_ALIAS = "resources/icons/entry_alias.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_ADD = "resources/icons/entry_add.gif"; //$NON-NLS-1$
    public static final String IMG_LOCATE_DN_IN_DIT = "resources/icons/locate_dn_in_dit.gif"; //$NON-NLS-1$
    public static final String IMG_LOCATE_SEARCHRESULT_IN_DIT = "resources/icons/locate_searchresult_in_dit.gif"; //$NON-NLS-1$
    public static final String IMG_LOCATE_BOOKMARK_IN_DIT = "resources/icons/locate_bookmark_in_dit.gif"; //$NON-NLS-1$
    public static final String IMG_LOCATE_ENTRY_IN_DIT = "resources/icons/locate_entry_in_dit.gif"; //$NON-NLS-1$
    public static final String IMG_OPEN_SEARCHRESULT = "resources/icons/open_searchresult.gif"; //$NON-NLS-1$
    public static final String IMG_BROWSER_ENTRYEDITOR = "resources/icons/browser_entryeditor.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE = "resources/icons/attribute.gif"; //$NON-NLS-1$
    public static final String IMG_VALUE = "resources/icons/value.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_LDIF = "resources/icons/copy_ldif.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_LDIF_USER = "resources/icons/copy_ldif_user.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_LDIF_OPERATIONAL = "resources/icons/copy_ldif_operational.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_LDIF_SEARCHRESULT = "resources/icons/copy_ldif_searchresult.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_CSV = "resources/icons/copy_csv.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_CSV_USER = "resources/icons/copy_csv_user.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_CSV_OPERATIONAL = "resources/icons/copy_csv_operational.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_CSV_SEARCHRESULT = "resources/icons/copy_csv_searchresult.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_BASE64 = "resources/icons/copy_base64.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_HEX = "resources/icons/copy_hex.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_UTF8 = "resources/icons/copy_raw.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_DN = "resources/icons/copy_dn.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_URL = "resources/icons/copy_url.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_ATT = "resources/icons/copy_att.gif"; //$NON-NLS-1$
    public static final String IMG_COPY_TABLE = "resources/icons/copy_table.gif"; //$NON-NLS-1$
    public static final String IMG_TABLE = "resources/icons/table.gif"; //$NON-NLS-1$
    public static final String IMG_SORT_NONE = "resources/icons/sort_none.gif"; //$NON-NLS-1$
    public static final String IMG_FILTER_EQUALS = "resources/icons/filter_equals.gif"; //$NON-NLS-1$
    public static final String IMG_FILTER_AND = "resources/icons/filter_and.gif"; //$NON-NLS-1$
    public static final String IMG_FILTER_OR = "resources/icons/filter_or.gif"; //$NON-NLS-1$
    public static final String IMG_FILTER_NOT = "resources/icons/filter_not.gif"; //$NON-NLS-1$
    public static final String IMG_BROWSER_SEARCHRESULTEDITOR = "resources/icons/browser_searchresulteditor.gif"; //$NON-NLS-1$
    public static final String IMG_SEARCHES = "resources/icons/searches.gif"; //$NON-NLS-1$
    public static final String IMG_SEARCH = "resources/icons/search.gif"; //$NON-NLS-1$
    public static final String IMG_SEARCH_NEW = "resources/icons/search_new.gif"; //$NON-NLS-1$
    public static final String IMG_SEARCH_UNPERFORMED = "resources/icons/search_unperformed.gif"; //$NON-NLS-1$
    public static final String IMG_BOOKMARKS = "resources/icons/bookmarks.gif"; //$NON-NLS-1$
    public static final String IMG_BOOKMARK = "resources/icons/bookmark.gif"; //$NON-NLS-1$
    public static final String IMG_BOOKMARK_ADD = "resources/icons/bookmark_add.gif"; //$NON-NLS-1$
    public static final String IMG_MARK = "resources/icons/mark.gif"; //$NON-NLS-1$
    public static final String IMG_BROWSER_SCHEMABROWSEREDITOR = "resources/icons/browser_schemabrowsereditor.gif"; //$NON-NLS-1$
    public static final String IMG_DEFAULT_SCHEMA = "resources/icons/defaultschema.gif"; //$NON-NLS-1$
    public static final String IMG_ATD = "resources/icons/atd.png"; //$NON-NLS-1$
    public static final String IMG_OCD = "resources/icons/ocd.png"; //$NON-NLS-1$
    public static final String IMG_MRD = "resources/icons/mrd.png"; //$NON-NLS-1$
    public static final String IMG_MRUD = "resources/icons/mrud.png"; //$NON-NLS-1$
    public static final String IMG_LSD = "resources/icons/lsd.png"; //$NON-NLS-1$
    public static final String IMG_MRD_EQUALITY = "resources/icons/mrd_equality.png"; //$NON-NLS-1$
    public static final String IMG_MRD_SUBSTRING = "resources/icons/mrd_substring.png"; //$NON-NLS-1$
    public static final String IMG_MRD_ORDERING = "resources/icons/mrd_ordering.png"; //$NON-NLS-1$
    public static final String IMG_OVR_FILTERED = "resources/icons/ovr16/filtered.gif"; //$NON-NLS-1$
    public static final String IMG_OVR_SEARCHRESULT = "resources/icons/ovr16/searchresult.gif"; //$NON-NLS-1$
    public static final String IMG_OVR_ERROR = "resources/icons/ovr16/error.gif"; //$NON-NLS-1$
    public static final String IMG_OVR_WARNING = "resources/icons/ovr16/warning.gif"; //$NON-NLS-1$
    public static final String IMG_OVR_MARK = "resources/icons/ovr16/mark.gif"; //$NON-NLS-1$
    public static final String IMG_NEXT = "resources/icons/next.gif"; //$NON-NLS-1$
    public static final String IMG_PREVIOUS = "resources/icons/previous.gif"; //$NON-NLS-1$
    public static final String IMG_REFRESH = "resources/icons/refresh.gif"; //$NON-NLS-1$
    public static final String IMG_CLEAR = "resources/icons/clear.gif"; //$NON-NLS-1$

    public static final String CMD_LOCATE_IN_DIT = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "Cmd_LocateInDit_id" ); //$NON-NLS-1$
    public static final String CMD_OPEN_SEARCH_RESULT = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "Cmd_OpenSearchResult_id" ); //$NON-NLS-1$

    public static final String PERSPECTIVE_LDAP = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "Perspective_LdapPerspective_id" ); //$NON-NLS-1$
    public static final String PERSPECTIVE_SCHEMA_EDITOR = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "Perspective_SchemaEditor_id" ); //$NON-NLS-1$

    public static final String EDITOR_ENTRY_EDITOR = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "Editor_EntryEditor_id" ); //$NON-NLS-1$
    public static final String EDITOR_SCHEMA_BROWSER = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "Editor_SchemaBrowser_id" ); //$NON-NLS-1$
    public static final String EDITOR_SEARCH_RESULT = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "Editor_SearchResultEditor_id" ); //$NON-NLS-1$

    public static final String SEARCH_PAGE_LDAP_SEARCH = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "SearchPage_LdapSearch_id" ); //$NON-NLS-1$

    public static final String VIEW_BROWSER_VIEW = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "View_BrowserView_id" ); //$NON-NLS-1$
    public static final String VIEW_CONNECTION_VIEW = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "View_ConnectionView_id" ); //$NON-NLS-1$
    public static final String VIEW_MODIFICATION_LOGS_VIEW = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "View_ModificationLogsView_id" ); //$NON-NLS-1$
    public static final String VIEW_SEARCH_LOGS_VIEW = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "View_SearchLogsView_id" ); //$NON-NLS-1$

    public static final String WIZARD_BATCH_OPERATION = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "NewWizard_BatchOperationWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_EXPORT_CONNECTIONS = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "Wizard_ExportConnections_id" ); //$NON-NLS-1$
    public static final String WIZARD_EXPORT_CSV = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "ExportWizard_ExportCsvWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_EXPORT_DSML = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "ExportWizard_ExportDdsmlWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_EXPORT_EXCEL = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "ExportWizard_ExportExcelWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_EXPORT_LDIF = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "ExportWizard_ExportLdifWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_IMPORT_CONNECTIONS = BrowserUIPlugin.getDefault().getPluginProperties()
        .getString( "Wizard_ImportConnections_id" ); //$NON-NLS-1$
    public static final String WIZARD_IMPORT_DSML = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "ImportWizard_ImportDsmlWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_IMPORT_LDIF = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "ImportWizard_ImportLdifWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_NEW_BOOKMARK = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "NewWizard_NewBookmarkWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_NEW_SEARCH = BrowserUIPlugin.getDefault().getPluginProperties().getString(
        "NewWizard_NewSearchWizard_id" ); //$NON-NLS-1$
}
