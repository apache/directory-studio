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
        "Plugin_id" ); //$NON-NLS-1$

    public static final int HISTORYSIZE = 20;

    public static final String CONTEXT_WINDOWS = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Ctx_LdapBrowserWindows_id" ); //$NON-NLS-1$
    public static final String CONTEXT_DIALOGS = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Ctx_LdapBrowserDialogs_id" ); //$NON-NLS-1$

    public static final String ACTION_ID_EDIT_VALUE = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "Cmd_EditValue_id" ); //$NON-NLS-1$
    public static final String ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION = BrowserCommonActivator.getDefault()
        .getPluginProperties().getString( "Cmd_EditAttributeDescription_id" ); //$NON-NLS-1$
    public static final String ACTION_ID_EDIT_RECORD = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "Cmd_EditRecord_id" ); //$NON-NLS-1$

    public static final String DIALOGSETTING_KEY_RECENT_FILE_PATH = "recentFilePath"; //$NON-NLS-1$
    public static final String DIALOGSETTING_KEY_FILE_HISTORY = "fileHistory"; //$NON-NLS-1$
    public static final String DIALOGSETTING_KEY_RETURNING_ATTRIBUTES_HISTORY = "returningAttributesHistory"; //$NON-NLS-1$
    public static final String DIALOGSETTING_KEY_SEARCH_FILTER_HISTORY = "searchFilterHistory"; //$NON-NLS-1$
    public static final String DIALOGSETTING_KEY_DN_HISTORY = "dnHistory"; //$NON-NLS-1$
    public static final String DIALOGSETTING_KEY_HOST_HISTORY = "hostHistory"; //$NON-NLS-1$
    public static final String DIALOGSETTING_KEY_PORT_HISTORY = "portHistory"; //$NON-NLS-1$

    public static final String FILTER_TEMPLATE_ID = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "CtxType_LdapFilter_Template_id" ); //$NON-NLS-1$

    public static final String EXTENSION_POINT_VALUE_EDITORS = BrowserCommonActivator.getDefault()
        .getPluginProperties().getString( "ExtensionPoint_ValueEditors_id" ); //$NON-NLS-1$

    public static final String PREFERENCE_TIME_LIMIT = "timeLimit"; //$NON-NLS-1$
    public static final String PREFERENCE_COUNT_LIMIT = "countLimit"; //$NON-NLS-1$
    public static final String PREFERENCE_SYNTAX_VALUEPEDITOR_RELATIONS = "syntaxValueProviderRelations"; //$NON-NLS-1$
    public static final String PREFERENCE_ATTRIBUTE_VALUEEDITOR_RELATIONS = "attributeValueProviderRelations"; //$NON-NLS-1$
    public static final String PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER = "formatTableAttributeDelimiter"; //$NON-NLS-1$
    public static final String PREFERENCE_FORMAT_TABLE_VALUEDELIMITER = "formatTableValueDelimiter"; //$NON-NLS-1$
    public static final String PREFERENCE_FORMAT_TABLE_QUOTECHARACTER = "formatTableQuoteCharacter"; //$NON-NLS-1$
    public static final String PREFERENCE_FORMAT_TABLE_LINESEPARATOR = "formatTableLineSeparator"; //$NON-NLS-1$
    public static final String PREFERENCE_FORMAT_TABLE_BINARYENCODING = "formatTableBinaryEncoding"; //$NON-NLS-1$
    public static final String PREFERENCE_SHOW_RAW_VALUES = "showRawValues"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SORT_BY = "browserSortBy"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SORT_ORDER = "browserSortOrder"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SORT_LIMIT = "browserSortLimit"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST = "browserLeafEntriesFirst"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_CONTAINER_ENTRIES_FIRST = "browserContainerEntriesFirst"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_META_ENTRIES_LAST = "browserMetaEntriesLast"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SHOW_QUICK_SEARCH = "browserShowQuickSearch"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SHOW_DIT = "browserShowDIT"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SHOW_SEARCHES = "browserShowSearches"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SHOW_BOOKMARKS = "browserShowBookmarks"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SHOW_DIRECTORY_META_ENTRIES = "browserShowDirectoryMetaEntries"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_ENABLE_FOLDING = "browserEnableFolding"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_FOLDING_SIZE = "browserFoldingSize"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES = "browserExpandBaseEntries"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_ENTRY_LABEL = "browserEntryLabel"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_ENTRY_ABBREVIATE = "browserEntryAbbreviate"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH = "browserentryAbbreviateMaxLength"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_LABEL = "browserSearchResultLabel"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE = "browserWearchResultAbbreviate"; //$NON-NLS-1$
    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH = "browserSearchResultAbbreviateMaxLength"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_AUTOSAVE_SINGLE_TAB = "entryeditorAutoSaveSingleTab"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_AUTOSAVE_MULTI_TAB = "entryeditorAutoSaveMultiTab"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING = "entryeditorEnableFolding"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD = "entryeditorFoldingThreshold"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_AUTO_EXPAND_FOLDED_ATTRIBUTES = "entryeditorAutoExpandFoldedAttributes"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST = "entryeditorObjectClassAndMustAttributesFirst"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST = "entryeditorOperationalAttributesLast"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY = "entryeditorDefaultSortBy"; //$NON-NLS-1$
    public static final String PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER = "entryeditorDefaultSortOrder"; //$NON-NLS-1$
    public static final String PREFERENCE_OBJECTCLASS_COLOR = "objectClassColor"; //$NON-NLS-1$
    public static final String PREFERENCE_OBJECTCLASS_FONT = "objectClassFont"; //$NON-NLS-1$
    public static final String PREFERENCE_MUSTATTRIBUTE_COLOR = "mustAttributeColor"; //$NON-NLS-1$
    public static final String PREFERENCE_MUSTATTRIBUTE_FONT = "mustAttributeFont"; //$NON-NLS-1$
    public static final String PREFERENCE_MAYATTRIBUTE_COLOR = "mayAttributeColor"; //$NON-NLS-1$
    public static final String PREFERENCE_MAYATTRIBUTE_FONT = "mayAttributeFont"; //$NON-NLS-1$
    public static final String PREFERENCE_OPERATIONALATTRIBUTE_COLOR = "operationalAttributeColor"; //$NON-NLS-1$
    public static final String PREFERENCE_OPERATIONALATTRIBUTE_FONT = "operationalAttributeFont"; //$NON-NLS-1$

    public static final int SHOW_DN = 0;
    public static final int SHOW_RDN = 1;
    public static final int SHOW_RDN_VALUE = 2;

    public static final String PREFERENCEPAGEID_VALUEEDITORS = BrowserCommonActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_ValueEditorsPreferencePage_id" ); //$NON-NLS-1$

    public static final String IMG_TEMPLATE = "resources/icons/template.gif"; //$NON-NLS-1$
    public static final String IMG_CLEAR = "resources/icons/clear.gif"; //$NON-NLS-1$
    public static final String IMG_HEXEDITOR = "resources/icons/hexeditor.gif"; //$NON-NLS-1$
    public static final String IMG_TEXTEDITOR = "resources/icons/texteditor.gif"; //$NON-NLS-1$
    public static final String IMG_INPLACE_TEXTEDITOR = "resources/icons/inplace_texteditor.gif"; //$NON-NLS-1$
    public static final String IMG_MULTIVALUEDEDITOR = "resources/icons/multivaluededitor.gif"; //$NON-NLS-1$
    public static final String IMG_DNEDITOR = "resources/icons/dneditor.gif"; //$NON-NLS-1$
    public static final String IMG_PULLDOWN = "resources/icons/pulldown.gif"; //$NON-NLS-1$
    public static final String IMG_SORT = "resources/icons/sort.gif"; //$NON-NLS-1$
    public static final String IMG_DIT = "resources/icons/dit.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY = "resources/icons/entry_default.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_EDITOR = "resources/icons/entry_editor.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_ROOT = "resources/icons/entry_root.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_DC = "resources/icons/entry_dc.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_ORG = "resources/icons/entry_org.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_PERSON = "resources/icons/entry_person.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_GROUP = "resources/icons/entry_group.gif"; //$NON-NLS-1$
    public static final String IMG_ENTRY_REF = "resources/icons/entry_ref.png"; //$NON-NLS-1$
    public static final String IMG_ENTRY_ALIAS = "resources/icons/entry_alias.png"; //$NON-NLS-1$
    public static final String IMG_SEARCHES = "resources/icons/searches.gif"; //$NON-NLS-1$
    public static final String IMG_SEARCH = "resources/icons/search.gif"; //$NON-NLS-1$
    public static final String IMG_QUICKSEARCH = "resources/icons/quicksearch.gif"; //$NON-NLS-1$
    public static final String IMG_SUBTREE = "resources/icons/subtree.gif"; //$NON-NLS-1$
    public static final String IMG_SEARCH_UNPERFORMED = "resources/icons/search_unperformed.gif"; //$NON-NLS-1$
    public static final String IMG_BOOKMARKS = "resources/icons/bookmarks.gif"; //$NON-NLS-1$
    public static final String IMG_BOOKMARK = "resources/icons/bookmark.gif"; //$NON-NLS-1$
    public static final String IMG_BROWSER_SCHEMABROWSEREDITOR = "resources/icons/browser_schemabrowsereditor.gif"; //$NON-NLS-1$
    public static final String IMG_CONNECTION_ADD = "resources/icons/connection_add.gif"; //$NON-NLS-1$
    public static final String IMG_CONNECTION_CONNECTED = "resources/icons/connection_connected.gif"; //$NON-NLS-1$
    public static final String IMG_CONNECTION_DISCONNECTED = "resources/icons/connection_disconnected.gif"; //$NON-NLS-1$
    public static final String IMG_CONNECTION_CONNECT = "resources/icons/connection_connect.gif"; //$NON-NLS-1$
    public static final String IMG_CONNECTION_DISCONNECT = "resources/icons/connection_disconnect.gif"; //$NON-NLS-1$
    public static final String IMG_CONNECTION_WIZARD = "resources/icons/connection_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_REFRESH = "resources/icons/refresh.gif"; //$NON-NLS-1$
    public static final String IMG_FILTER_DIT = "resources/icons/filter_dit.gif"; //$NON-NLS-1$
    public static final String IMG_FILTER_EDITOR = "resources/icons/filtereditor.gif"; //$NON-NLS-1$
    public static final String IMG_PARENT = "resources/icons/parent.gif"; //$NON-NLS-1$
    public static final String IMG_UNFILTER_DIT = "resources/icons/unfilter_dit.gif"; //$NON-NLS-1$
    public static final String IMG_FILTER = "resources/icons/filter.gif"; //$NON-NLS-1$
    public static final String IMG_SORT_ASCENDING = "resources/icons/sort_ascending.gif"; //$NON-NLS-1$
    public static final String IMG_SORT_DESCENDING = "resources/icons/sort_descending.gif"; //$NON-NLS-1$
    public static final String IMG_VALUE_ADD = "resources/icons/value_add.gif"; //$NON-NLS-1$
    public static final String IMG_ATTRIBUTE_ADD = "resources/icons/attribute_add.gif"; //$NON-NLS-1$
    public static final String IMG_DELETE_ALL = "resources/icons/delete_all.gif"; //$NON-NLS-1$
    public static final String IMG_ATD = "resources/icons/atd.png"; //$NON-NLS-1$
    public static final String IMG_LSD = "resources/icons/lsd.png"; //$NON-NLS-1$
    public static final String IMG_OCD = "resources/icons/ocd.png"; //$NON-NLS-1$
    public static final String IMG_OCD_ABSTRACT = "resources/icons/ocd_abstract.gif"; //$NON-NLS-1$
    public static final String IMG_OCD_AUXILIARY = "resources/icons/ocd_auxiliary.gif"; //$NON-NLS-1$
    public static final String IMG_OCD_STRUCTURAL = "resources/icons/ocd_structural.gif"; //$NON-NLS-1$
    public static final String IMG_MRD = "resources/icons/mrd.png"; //$NON-NLS-1$
    public static final String IMG_ENTRY_WIZARD = "resources/icons/entry_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_TOP = "resources/icons/top.gif"; //$NON-NLS-1$
    public static final String IMG_NEXT = "resources/icons/next.gif"; //$NON-NLS-1$
    public static final String IMG_PREVIOUS = "resources/icons/previous.gif"; //$NON-NLS-1$
    public static final String IMG_RENAME = "resources/icons/rename.gif"; //$NON-NLS-1$

    public static final String IMG_SYNTAX_CHECKER = "resources/icons/syntax_checker.png"; //$NON-NLS-1$
    public static final String IMG_COMPARATOR = "resources/icons/comparator.png"; //$NON-NLS-1$
    public static final String IMG_NORMALIZER = "resources/icons/normalizer.png"; //$NON-NLS-1$
    public static final String IMG_DIT_CONTENT_RULE = "resources/icons/dit_content_rule.png"; //$NON-NLS-1$
    public static final String IMG_DIT_STRUCTURE_RULE = "resources/icons/dit_structure_rule.png"; //$NON-NLS-1$
    public static final String IMG_NAME_FORM = "resources/icons/name_form.png"; //$NON-NLS-1$

    public static final String CMD_ADD_ATTRIBUTE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_AddAttribute_id" ); //$NON-NLS-1$
    public static final String CMD_ADD_VALUE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_AddValue_id" ); //$NON-NLS-1$
    public static final String CMD_OPEN_SEARCH_RESULT = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "Cmd_OpenSearchResult_id" ); //$NON-NLS-1$
    public static final String CMD_COPY = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Copy_id" ); //$NON-NLS-1$
    public static final String CMD_PASTE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Paste_id" ); //$NON-NLS-1$
    public static final String CMD_DELETE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Delete_id" ); //$NON-NLS-1$
    public static final String CMD_PROPERTIES = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Properties_id" ); //$NON-NLS-1$
    public static final String CMD_FIND = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "Cmd_Find_id" ); //$NON-NLS-1$

    public static final String PROP_VALUE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Value_id" ); //$NON-NLS-1$
    public static final String PROP_ATTRIBUTE = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Attribute_id" ); //$NON-NLS-1$
    public static final String PROP_SEARCH = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Search_id" ); //$NON-NLS-1$
    public static final String PROP_BOOKMARK = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Bookmark_id" ); //$NON-NLS-1$
    public static final String PROP_ENTRY = BrowserCommonActivator.getDefault().getPluginProperties().getString(
        "PropPage_Entry_id" ); //$NON-NLS-1$

    public static final String DND_ENTRY_TRANSFER = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "DnD_EntryTransfer" ); //$NON-NLS-1$
    public static final String DND_SEARCH_TRANSFER = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "DnD_SearchTransfer" ); //$NON-NLS-1$
    public static final String DND_VALUES_TRANSFER = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "DnD_ValuesTransfer" ); //$NON-NLS-1$

    public static final String WIZARD_ATTRIBUTE_WIZARD = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "Wizard_AttributeWizard_id" ); //$NON-NLS-1$

    public static final String WIZARD_NEW_ENTRY_WIZARD = BrowserCommonActivator.getDefault().getPluginProperties()
        .getString( "NewWizard_NewEntryWizard_id" ); //$NON-NLS-1$
    public static final String WIZARD_NEW_CONTEXT_ENTRY_WIZARD = BrowserCommonActivator.getDefault()
        .getPluginProperties().getString( "NewWizard_NewContextEntryWizard_id" ); //$NON-NLS-1$
}
