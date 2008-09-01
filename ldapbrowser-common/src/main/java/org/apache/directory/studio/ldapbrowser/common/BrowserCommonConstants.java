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
package org.apache.directory.studio.ldapbrowser.common;


public interface BrowserCommonConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Plugin_id" );

    public static final int HISTORYSIZE = 20;

    public static final String CONTEXT_WINDOWS = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Ctx_LdapBrowserWindows_id" );
    public static final String CONTEXT_DIALOGS = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Ctx_LdapBrowserDialogs_id" );

    public static final String ACTION_ID_EDIT_VALUE = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "Cmd_EditValue_id" );
    public static final String ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION = BrowserCommonActivator.getDefault()
        .getPluginProperties().getString( "Cmd_EditAttributeDescription_id" );
    public static final String ACTION_ID_EDIT_RECORD = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "Cmd_EditRecord_id" );

    public static final String DIALOGSETTING_KEY_RECENT_FILE_PATH = "recentFilePath";
    public static final String DIALOGSETTING_KEY_FILE_HISTORY = "fileHistory";
    public static final String DIALOGSETTING_KEY_RETURNING_ATTRIBUTES_HISTORY = "returningAttributesHistory";
    public static final String DIALOGSETTING_KEY_SEARCH_FILTER_HISTORY = "searchFilterHistory";
    public static final String DIALOGSETTING_KEY_DN_HISTORY = "dnHistory";
    public static final String DIALOGSETTING_KEY_HOST_HISTORY = "hostHistory";
    public static final String DIALOGSETTING_KEY_PORT_HISTORY = "portHistory";

    public static final String FILTER_TEMPLATE_ID = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "CtxType_LdapFilter_Template_id" );

    public static final String EXTENSION_POINT_VALUE_EDITORS = BrowserCommonActivator.getDefault()
        .getPluginProperties().getString( "ExtensionPoint_ValueEditors_id" );

    public static final String PREFERENCE_TIME_LIMIT = "timeLimit";
    public static final String PREFERENCE_COUNT_LIMIT = "countLimit";
    public static final String PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS = "syntaxValueProviderRelations";
    public static final String PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS = "attributeValueProviderRelations";
    public static final String PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER = "formatTableAttributeDelimiter";
    public static final String PREFERENCE_FORMAT_TABLE_VALUEDELIMITER = "formatTableValueDelimiter";
    public static final String PREFERENCE_FORMAT_TABLE_QUOTECHARACTER = "formatTableQuoteCharacter";
    public static final String PREFERENCE_FORMAT_TABLE_LINESEPARATOR = "formatTableLineSeparator";
    public static final String PREFERENCE_FORMAT_TABLE_BINARYENCODING = "formatTableBinaryEncoding";
    public static final String PREFERENCE_SHOW_RAW_VALUES = "showRawValues";
    public static final String PREFERENCE_BROWSER_SORT_BY = "browserSortBy";
    public static final String PREFERENCE_BROWSER_SORT_ORDER = "browserSortOrder";
    public static final String PREFERENCE_BROWSER_SORT_LIMIT = "browserSortLimit";
    public static final String PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST = "browserLeafEntriesFirst";
    public static final String PREFERENCE_BROWSER_CONTAINER_ENTRIES_FIRST = "browserContainerEntriesFirst";
    public static final String PREFERENCE_BROWSER_META_ENTRIES_LAST = "browserMetaEntriesLast";
    public static final String PREFERENCE_BROWSER_SHOW_DIT = "browserShowDIT";
    public static final String PREFERENCE_BROWSER_SHOW_SEARCHES = "browserShowSearches";
    public static final String PREFERENCE_BROWSER_SHOW_BOOKMARKS = "browserShowBookmarks";
    public static final String PREFERENCE_BROWSER_SHOW_DIRECTORY_META_ENTRIES = "browserShowDirectoryMetaEntries";
    public static final String PREFERENCE_BROWSER_ENABLE_FOLDING = "browserEnableFolding";
    public static final String PREFERENCE_BROWSER_FOLDING_SIZE = "browserFoldingSize";
    public static final String PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES = "browserExpandBaseEntries";
    public static final String PREFERENCE_BROWSER_ENTRY_LABEL = "browserEntryLabel";
    public static final String PREFERENCE_BROWSER_ENTRY_ABBREVIATE = "browserEntryAbbreviate";
    public static final String PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH = "browserentryAbbreviateMaxLength";
    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_LABEL = "browserSearchResultLabel";
    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE = "browserWearchResultAbbreviate";
    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH = "browserSearchResultAbbreviateMaxLength";
    public static final String PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES = "entryeditorShowMayAttributes";
    public static final String PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES = "entryeditorShowMustAttributes";
    public static final String PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES = "entryeditorShowObjectClassAttribute";
    public static final String PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES = "entryeditorShowOperationalAttributes";
    public static final String PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING = "entryeditorEnableFolding";
    public static final String PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD = "entryeditorFoldingThreshold";
    public static final String PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST = "entryeditorObjectClassAndMustAttributesFirst";
    public static final String PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST = "entryeditorOperationalAttributesLast";
    public static final String PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY = "entryeditorDefaultSortBy";
    public static final String PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER = "entryeditorDefaultSortOrder";
    public static final String PREFERENCE_OBJECTCLASS_COLOR = "objectClassColor";
    public static final String PREFERENCE_OBJECTCLASS_FONT = "objectClassFont";
    public static final String PREFERENCE_MUSTATTRIBUTE_COLOR = "mustAttributeColor";
    public static final String PREFERENCE_MUSTATTRIBUTE_FONT = "mustAttributeFont";
    public static final String PREFERENCE_MAYATTRIBUTE_COLOR = "mayAttributeColor";
    public static final String PREFERENCE_MAYATTRIBUTE_FONT = "mayAttributeFont";
    public static final String PREFERENCE_OPERATIONALATTRIBUTE_COLOR = "operationalAttributeColor";
    public static final String PREFERENCE_OPERATIONALATTRIBUTE_FONT = "operationalAttributeFont";
    public static final String PREFERENCE_QUICKFILTER_BACKGROUND_COLOR = "quickfilterBackgroundColor";
    public static final String PREFERENCE_QUICKFILTER_FOREGROUND_COLOR = "quickfilterForegroundColor";
    public static final String PREFERENCE_QUICKFILTER_FONT = "quickfilterFont";
    public static final String PREFERENCE_ERROR_FONT = "errorFont";
    public static final String PREFERENCE_ERROR_COLOR = "errorColor";
    public static final String PREFERENCE_WARNING_FONT = "warningFont";
    public static final String PREFERENCE_WARNING_COLOR = "warningColor";
    public static final int SHOW_DN = 0;
    public static final int SHOW_RDN = 1;
    public static final int SHOW_RDN_VALUE = 2;

    public static final String PREFERENCEPAGEID_VALUEEDITORS = BrowserCommonActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_ValueEditorsPreferencePage_id" );

    public static final String IMG_TEMPLATE = "resources/icons/template.gif";
    public static final String IMG_CLEAR = "resources/icons/clear.gif";
    public static final String IMG_HEXEDITOR = "resources/icons/hexeditor.gif";
    public static final String IMG_TEXTEDITOR = "resources/icons/texteditor.gif";
    public static final String IMG_INPLACE_TEXTEDITOR = "resources/icons/inplace_texteditor.gif";
    public static final String IMG_MULTIVALUEDEDITOR = "resources/icons/multivaluededitor.gif";
    public static final String IMG_PULLDOWN = "resources/icons/pulldown.gif";
    public static final String IMG_SORT = "resources/icons/sort.gif";
    public static final String IMG_DIT = "resources/icons/dit.gif";
    public static final String IMG_ENTRY = "resources/icons/entry_default.gif";
    public static final String IMG_ENTRY_ROOT = "resources/icons/entry_root.gif";
    public static final String IMG_ENTRY_DC = "resources/icons/entry_dc.gif";
    public static final String IMG_ENTRY_ORG = "resources/icons/entry_org.gif";
    public static final String IMG_ENTRY_PERSON = "resources/icons/entry_person.gif";
    public static final String IMG_ENTRY_GROUP = "resources/icons/entry_group.gif";
    public static final String IMG_ENTRY_REF = "resources/icons/entry_ref.gif";
    public static final String IMG_ENTRY_ALIAS = "resources/icons/entry_alias.gif";
    public static final String IMG_SEARCHES = "resources/icons/searches.gif";
    public static final String IMG_SEARCH = "resources/icons/search.gif";
    public static final String IMG_SEARCH_UNPERFORMED = "resources/icons/search_unperformed.gif";
    public static final String IMG_BOOKMARKS = "resources/icons/bookmarks.gif";
    public static final String IMG_BOOKMARK = "resources/icons/bookmark.gif";
    public static final String IMG_BROWSER_SCHEMABROWSEREDITOR = "resources/icons/browser_schemabrowsereditor.gif";
    public static final String IMG_CONNECTION_ADD = "resources/icons/connection_add.gif";
    public static final String IMG_CONNECTION_CONNECTED = "resources/icons/connection_connected.gif";
    public static final String IMG_CONNECTION_DISCONNECTED = "resources/icons/connection_disconnected.gif";
    public static final String IMG_CONNECTION_CONNECT = "resources/icons/connection_connect.gif";
    public static final String IMG_CONNECTION_DISCONNECT = "resources/icons/connection_disconnect.gif";
    public static final String IMG_CONNECTION_WIZARD = "resources/icons/connection_wizard.gif";
    public static final String IMG_REFRESH = "resources/icons/refresh.gif";
    public static final String IMG_COLLAPSEALL = "resources/icons/collapseall.gif";
    public static final String IMG_FILTER_DIT = "resources/icons/filter_dit.gif";
    public static final String IMG_FILTER_EDITOR = "resources/icons/filtereditor.gif";
    public static final String IMG_PARENT = "resources/icons/parent.gif";
    public static final String IMG_UNFILTER_DIT = "resources/icons/unfilter_dit.gif";
    public static final String IMG_FILTER = "resources/icons/filter.gif";
    public static final String IMG_SORT_ASCENDING = "resources/icons/sort_ascending.gif";
    public static final String IMG_SORT_DESCENDING = "resources/icons/sort_descending.gif";
    public static final String IMG_VALUE_ADD = "resources/icons/value_add.gif";
    public static final String IMG_ATTRIBUTE_ADD = "resources/icons/attribute_add.gif";
    public static final String IMG_DELETE_ALL = "resources/icons/delete_all.gif";
    public static final String IMG_ATD = "resources/icons/atd.png";
    public static final String IMG_OCD = "resources/icons/ocd.png";
    public static final String IMG_OCD_ABSTRACT = "resources/icons/ocd_abstract.gif";
    public static final String IMG_OCD_AUXILIARY = "resources/icons/ocd_auxiliary.gif";
    public static final String IMG_OCD_STRUCTURAL = "resources/icons/ocd_structural.gif";
    public static final String IMG_MRD = "resources/icons/mrd.png";
    public static final String IMG_ENTRY_WIZARD = "resources/icons/entry_wizard.gif";

    public static final String CMD_ADD_ATTRIBUTE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_AddAttribute_id" );
    public static final String CMD_ADD_VALUE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_AddValue_id" );
    public static final String CMD_OPEN_SEARCH_RESULT = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "Cmd_OpenSearchResult_id" );
    public static final String CMD_COPY = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Copy_id" );
    public static final String CMD_PASTE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Paste_id" );
    public static final String CMD_DELETE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Delete_id" );
    public static final String CMD_PROPERTIES = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Properties_id" );
    public static final String CMD_FIND = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Find_id" );

    public static final String PROP_VALUE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Value_id" );
    public static final String PROP_ATTRIBUTE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Attribute_id" );
    public static final String PROP_SEARCH = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Search_id" );
    public static final String PROP_BOOKMARK = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Bookmark_id" );
    public static final String PROP_ENTRY = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Entry_id" );

    public static final String DND_ENTRY_TRANSFER = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "DnD_EntryTransfer" );
    public static final String DND_SEARCH_TRANSFER = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "DnD_SearchTransfer" );
    public static final String DND_VALUES_TRANSFER = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "DnD_ValuesTransfer" );

}
