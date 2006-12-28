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

package org.apache.directory.ldapstudio.browser.ui;


public interface BrowserUIConstants
{

    public static final int HISTORYSIZE = 20;

    public static final String DN = "DN";

    public static final String ACTION_ID_EDIT_VALUE = "org.apache.directory.ldapstudio.browser.action.editValue";

    public static final String ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION = "org.apache.directory.ldapstudio.browser.action.editAttributeDescription";

    public static final String ACTION_ID_EDIT_RECORD = "org.apache.directory.ldapstudio.browser.action.editRecord";

    public static final String PREFERENCE_TIME_LIMIT = "timeLimit";

    public static final String PREFERENCE_COUNT_LIMIT = "countLimit";

    public static final String PREFERENCE_SYNTAX_VALUEPROVIDER_RELATIONS = "syntaxValueProviderRelations";

    public static final String PREFERENCE_ATTRIBUTE_VALUEPROVIDER_RELATIONS = "attributeValueProviderRelations";

    public static final String FILTER_TEMPLATE_ID = "org.apache.directory.ldapstudio.browser.ui.templates.filter";

    public static final String LDIF_FILE_TEMPLATE_ID = "org.apache.directory.ldapstudio.browser.ui.templates.ldifFile";

    public static final String LDIF_ATTR_VAL_RECORD_TEMPLATE_ID = "org.apache.directory.ldapstudio.browser.ui.templates.ldifAttrValRecord";

    public static final String LDIF_MODIFICATION_RECORD_TEMPLATE_ID = "org.apache.directory.ldapstudio.browser.ui.templates.ldifModificationRecord";

    public static final String LDIF_MODIFICATION_ITEM_TEMPLATE_ID = "org.apache.directory.ldapstudio.browser.ui.templates.ldifModificationItem";

    public static final String LDIF_MODDN_RECORD_TEMPLATE_ID = "org.apache.directory.ldapstudio.browser.ui.templates.ldifModdnRecord";

    public static final String PREFERENCE_OBJECTCLASS_COLOR = "objectClassColor";

    public static final String PREFERENCE_OBJECTCLASS_FONT = "objectClassFont";

    public static final String PREFERENCE_MUSTATTRIBUTE_COLOR = "mustAttributeColor";

    public static final String PREFERENCE_MUSTATTRIBUTE_FONT = "mustAttributeFont";

    public static final String PREFERENCE_MAYATTRIBUTE_COLOR = "mayAttributeColor";

    public static final String PREFERENCE_MAYATTRIBUTE_FONT = "mayAttributeFont";

    public static final String PREFERENCE_OPERATIONALATTRIBUTE_COLOR = "operationalAttributeColor";

    public static final String PREFERENCE_OPERATIONALATTRIBUTE_FONT = "operationalAttributeFont";

    public static final String PREFERENCE_WARNING_FONT = "warningFont";

    public static final String PREFERENCE_WARNING_COLOR = "warningColor";

    public static final String PREFERENCE_ERROR_FONT = "errorFont";

    public static final String PREFERENCE_ERROR_COLOR = "errorColor";

    public static final String PREFERENCE_QUICKFILTER_BACKGROUND_COLOR = "quickfilterBackgroundColor";

    public static final String PREFERENCE_QUICKFILTER_FOREGROUND_COLOR = "quickfilterForegroundColor";

    public static final String PREFERENCE_QUICKFILTER_FONT = "quickfilterFont";

    public static final String PREFERENCE_SHOW_RAW_VALUES = "showRawValues";

    public static final String PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER = "formatTableAttributeDelimiter";

    public static final String PREFERENCE_FORMAT_TABLE_VALUEDELIMITER = "formatTableValueDelimiter";

    public static final String PREFERENCE_FORMAT_TABLE_QUOTECHARACTER = "formatTableQuoteCharacter";

    public static final String PREFERENCE_FORMAT_TABLE_LINESEPARATOR = "formatTableLineSeparator";

    public static final String PREFERENCE_FORMAT_TABLE_BINARYENCODING = "formatTableBinaryEncoding";

    public static final String PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES = "browserExpandBaseEntries";

    public static final String PREFERENCE_BROWSER_ENABLE_FOLDING = "browserEnableFolding";

    public static final String PREFERENCE_BROWSER_FOLDING_SIZE = "browserFoldingSize";

    public static final String PREFERENCE_BROWSER_ENTRY_LABEL = "browserEntryLabel";

    public static final String PREFERENCE_BROWSER_ENTRY_ABBREVIATE = "browserEntryAbbreviate";

    public static final String PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH = "browserentryAbbreviateMaxLength";

    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_LABEL = "browserSearchResultLabel";

    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE = "browserWearchResultAbbreviate";

    public static final String PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH = "browserSearchResultAbbreviateMaxLength";

    public static final String PREFERENCE_BROWSER_SHOW_DIT = "browserShowDIT";

    public static final String PREFERENCE_BROWSER_SHOW_SEARCHES = "browserShowSearches";

    public static final String PREFERENCE_BROWSER_SHOW_BOOKMARKS = "browserShowBookmarks";

    public static final String PREFERENCE_BROWSER_SHOW_DIRECTORY_META_ENTRIES = "browserShowDirectoryMetaEntries";

    public static final String PREFERENCE_BROWSER_SORT_BY = "browserSortBy";

    public static final String PREFERENCE_BROWSER_SORT_ORDER = "browserSortOrder";

    public static final String PREFERENCE_BROWSER_SORT_LIMIT = "browserSortLimit";

    public static final String PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST = "browserLeafEntriesFirst";

    public static final String PREFERENCE_BROWSER_META_ENTRIES_LAST = "browserMetaEntriesLast";

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

    public static final String PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN = "searchResultEditorShowDn";

    public static final String PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS = "searchResultEditorShowLinks";

    public static final String PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP = "ldifEditorFormatterAutoWrap";

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_ENABLE = "ldifEditorFoldingEnable";

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS = "ldifEditorFoldingInitiallyFoldComments";

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS = "ldifEditoroldingInitiallyFoldRecords";

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES = "ldifEditorFoldingInitiallyFoldWrappedLines";

    public static final String PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK = "ldifEditorDoubleClickUserLdifDoubleClick";

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO = "ldifEditorCodeAssistInsertSingleProposalAuto";

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION = "ldifEditorCodeAssistEnableAutoActivation";

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY = "ldifEditorCodeAssistAutoActivationDelay";

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC = "ldifEditorCodeAssistInsertAttributeInModSpec";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX = "_RGB";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX = "_STYLE";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT = "ldifEditorSyntaxComment";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD = "ldifEditorSyntaxKeyword";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_DN = "ldifEditorSyntaxDn";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE = "ldifEditorSyntaxAttribute";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE = "ldifEditorSyntaxValueType";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_VALUE = "ldifEditorSyntaxValue";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD = "ldifEditorSyntaxChangetypeAdd";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY = "ldifEditorSyntaxChangetypeModify";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE = "ldifEditorSyntaxChangetypeDelete";

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN = "ldifEditorSyntaxChangetypeModdn";

    public static final String PREFERENCEPAGEID_MAIN = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.MainPreferencePage";

    public static final String PREFERENCEPAGEID_ATTRIBUTES = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.AttributesPreferencePage";

    public static final String PREFERENCEPAGEID_BINARYATTRIBUTES = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.BinaryAttributesAndSyntaxesPreferencePage";

    public static final String PREFERENCEPAGEID_VALUEEDITORS = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.ValueEditorsPreferencePage";

    public static final String PREFERENCEPAGEID_BROWSER = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.BrowserPreferencePage";

    public static final String PREFERENCEPAGEID_ENTRYEDITOR = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.EntryEditorPreferencePage";

    public static final String PREFERENCEPAGEID_SEARCHRESULTEDITOR = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.SearchResultEditorPreferencePage";

    public static final String PREFERENCEPAGEID_LDIFEDITOR = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.LdifEditorPreferencePage";

    public static final String PREFERENCEPAGEID_LDIFEDITOR_CONTENTASSIST = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.LdifEditorContentAssistPreferencePage";

    public static final String PREFERENCEPAGEID_LDIFEDITOR_SYNTAXCOLORING = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.LdifEditorSyntaxColoringPreferencePage";

    public static final String PREFERENCEPAGEID_LDIFEDITOR_TEMPLATES = "org.apache.directory.ldapstudio.browser.ui.dialogs.preferences.TemplatesPreferencePage";

    public static final String DIALOGSETTING_KEY_RETURNING_ATTRIBUTES_HISTORY = "returningAttributesHistory";

    public static final String DIALOGSETTING_KEY_SEARCH_FILTER_HISTORY = "searchFilterHistory";

    public static final String DIALOGSETTING_KEY_DN_HISTORY = "dnHistory";

    public static final String DIALOGSETTING_KEY_HOST_HISTORY = "hostHistory";

    public static final String DIALOGSETTING_KEY_PORT_HISTORY = "portHistory";

    public static final String DIALOGSETTING_KEY_RECENT_FILE_PATH = "recentFilePath";

    public static final String DIALOGSETTING_KEY_FILE_HISTORY = "fileHistory";

    public static final String IMG_TEMPLATE = "icons/template.gif";

    public static final String IMG_PULLDOWN = "icons/pulldown.gif";

    public static final String IMG_CLEAR = "icons/clear.gif";

    public static final String IMG_REFRESH = "icons/refresh.gif";

    public static final String IMG_COLLAPSEALL = "icons/collapseall.gif";

    public static final String IMG_EXPANDALL = "icons/expandall.gif";

    public static final String IMG_PARENT = "icons/parent.gif";

    public static final String IMG_BATCH = "icons/batch.gif";

    public static final String IMG_IMPORT = "icons/import.gif";

    public static final String IMG_EXPORT = "icons/export.gif";
    
    public static final String IMG_IMPORT_DSML_WIZARD = "icons/import_dsml_wizard.gif";
    
    public static final String IMG_EXPORT_DSML_WIZARD = "icons/export_dsml_wizard.gif";

    public static final String IMG_IMPORT_LDIF_WIZARD = "icons/import_ldif_wizard.gif";

    public static final String IMG_EXPORT_LDIF_WIZARD = "icons/export_ldif_wizard.gif";

    public static final String IMG_IMPORT_CSV_WIZARD = "icons/import_csv_wizard.gif";

    public static final String IMG_EXPORT_CSV_WIZARD = "icons/export_csv_wizard.gif";

    public static final String IMG_IMPORT_XLS_WIZARD = "icons/import_xls_wizard.gif";

    public static final String IMG_EXPORT_XLS_WIZARD = "icons/export_xls_wizard.gif";

    public static final String IMG_IMPORT_DSML = "icons/import_dsml.gif";
    
    public static final String IMG_EXPORT_DSML = "icons/export_dsml.gif";
    
    public static final String IMG_IMPORT_LDIF = "icons/import_ldif.gif";

    public static final String IMG_EXPORT_LDIF = "icons/export_ldif.gif";

    public static final String IMG_IMPORT_CSV = "icons/import_csv.gif";

    public static final String IMG_EXPORT_CSV = "icons/export_csv.gif";

    public static final String IMG_IMPORT_XLS = "icons/import_xls.gif";

    public static final String IMG_EXPORT_XLS = "icons/export_xls.gif";

    public static final String IMG_BROWSER_CONNECTIONVIEW = "icons/browser_connectionview.gif";

    public static final String IMG_CONNECTION_ADD = "icons/connection_add.gif";

    public static final String IMG_CONNECTION_WIZARD = "icons/connection_wizard.gif";

    public static final String IMG_CONNECTION_CONNECTED = "icons/connection_connected.gif";

    public static final String IMG_CONNECTION_DISCONNECTED = "icons/connection_disconnected.gif";

    public static final String IMG_CONNECTION_CONNECT = "icons/connection_connect.gif";

    public static final String IMG_CONNECTION_DISCONNECT = "icons/connection_disconnect.gif";

    public static final String IMG_BROWSER_BROWSERVIEW = "icons/browser_browserview.gif";

    public static final String IMG_DIT = "icons/dit.gif";

    public static final String IMG_ENTRY = "icons/entry_default.gif";

    public static final String IMG_ENTRY_ROOT = "icons/entry_root.gif";

    public static final String IMG_ENTRY_DC = "icons/entry_dc.gif";

    public static final String IMG_ENTRY_ORG = "icons/entry_org.gif";

    public static final String IMG_ENTRY_PERSON = "icons/entry_person.gif";

    public static final String IMG_ENTRY_GROUP = "icons/entry_group.gif";

    public static final String IMG_ENTRY_REF = "icons/entry_ref.gif";

    public static final String IMG_ENTRY_ALIAS = "icons/entry_alias.gif";

    public static final String IMG_ENTRY_ADD = "icons/entry_add.gif";

    public static final String IMG_ENTRY_WIZARD = "icons/entry_wizard.gif";

    public static final String IMG_LOCATE_DN_IN_DIT = "icons/locate_dn_in_dit.gif";

    public static final String IMG_LOCATE_SEARCHRESULT_IN_DIT = "icons/locate_searchresult_in_dit.gif";

    public static final String IMG_LOCATE_BOOKMARK_IN_DIT = "icons/locate_bookmark_in_dit.gif";

    public static final String IMG_LOCATE_ENTRY_IN_DIT = "icons/locate_entry_in_dit.gif";

    public static final String IMG_OPEN_SEARCHRESULT = "icons/open_searchresult.gif";

    public static final String IMG_BROWSER_ENTRYEDITOR = "icons/browser_entryeditor.gif";

    public static final String IMG_ATTRIBUTE = "icons/attribute.gif";

    public static final String IMG_ATTRIBUTE_ADD = "icons/attribute_add.gif";

    public static final String IMG_VALUE = "icons/value.gif";

    public static final String IMG_VALUE_ADD = "icons/value_add.gif";

    public static final String IMG_COPY_LDIF = "icons/copy_ldif.gif";

    public static final String IMG_COPY_LDIF_USER = "icons/copy_ldif_user.gif";

    public static final String IMG_COPY_LDIF_OPERATIONAL = "icons/copy_ldif_operational.gif";

    public static final String IMG_COPY_LDIF_SEARCHRESULT = "icons/copy_ldif_searchresult.gif";

    public static final String IMG_COPY_CSV = "icons/copy_csv.gif";

    public static final String IMG_COPY_CSV_USER = "icons/copy_csv_user.gif";

    public static final String IMG_COPY_CSV_OPERATIONAL = "icons/copy_csv_operational.gif";

    public static final String IMG_COPY_CSV_SEARCHRESULT = "icons/copy_csv_searchresult.gif";

    public static final String IMG_COPY_BASE64 = "icons/copy_base64.gif";

    public static final String IMG_COPY_HEX = "icons/copy_hex.gif";

    public static final String IMG_COPY_UTF8 = "icons/copy_raw.gif";

    public static final String IMG_COPY_DN = "icons/copy_dn.gif";

    public static final String IMG_COPY_URL = "icons/copy_url.gif";

    public static final String IMG_COPY_ATT = "icons/copy_att.gif";

    public static final String IMG_COPY_TABLE = "icons/copy_table.gif";

    public static final String IMG_TABLE = "icons/table.gif";

    public static final String IMG_DELETE_ALL = "icons/delete_all.gif";

    public static final String IMG_SORT = "icons/sort.gif";

    public static final String IMG_SORT_NONE = "icons/sort_none.gif";

    public static final String IMG_SORT_ASCENDING = "icons/sort_ascending.gif";

    public static final String IMG_SORT_DESCENDING = "icons/sort_descending.gif";

    public static final String IMG_FILTER = "icons/filter.gif";

    public static final String IMG_FILTER_DIT = "icons/filter_dit.gif";

    public static final String IMG_UNFILTER_DIT = "icons/unfilter_dit.gif";

    public static final String IMG_FILTER_EQUALS = "icons/filter_equals.gif";

    public static final String IMG_FILTER_AND = "icons/filter_and.gif";

    public static final String IMG_FILTER_OR = "icons/filter_or.gif";

    public static final String IMG_FILTER_NOT = "icons/filter_not.gif";

    public static final String IMG_BROWSER_SEARCHRESULTEDITOR = "icons/browser_searchresulteditor.gif";

    public static final String IMG_SEARCHES = "icons/searches.gif";

    public static final String IMG_SEARCH = "icons/search.gif";

    public static final String IMG_SEARCH_NEW = "icons/search_new.gif";

    public static final String IMG_SEARCH_UNPERFORMED = "icons/search_unperformed.gif";

    public static final String IMG_BOOKMARKS = "icons/bookmarks.gif";

    public static final String IMG_BOOKMARK = "icons/bookmark.gif";

    public static final String IMG_BOOKMARK_ADD = "icons/bookmark_add.gif";

    public static final String IMG_MARK = "icons/mark.gif";

    public static final String IMG_BROWSER_SCHEMABROWSEREDITOR = "icons/browser_schemabrowsereditor.gif";

    public static final String IMG_DEFAULT_SCHEMA = "icons/defaultschema.gif";

    public static final String IMG_HEXEDITOR = "icons/hexeditor.gif";

    public static final String IMG_EHEPHEXEDITOR = "icons/ehep.gif";

    public static final String IMG_IMAGEEDITOR = "icons/imageeditor.gif";

    public static final String IMG_ADDRESSEDITOR = "icons/addresseditor.gif";

    public static final String IMG_TEXTEDITOR = "icons/texteditor.gif";

    public static final String IMG_DNEDITOR = "icons/dneditor.gif";

    public static final String IMG_PASSWORDEDITOR = "icons/passwordeditor.gif";

    public static final String IMG_INPLACE_TEXTEDITOR = "icons/inplace_texteditor.gif";

    public static final String IMG_INPLACE_OCEDITOR = "icons/inplace_oceditor.gif";

    public static final String IMG_INPLACE_GENERALIZEDTIMEEDITOR = "icons/inplace_generalizedtimeeditor.gif";

    public static final String IMG_MULTIVALUEDEDITOR = "icons/multivaluededitor.gif";

    public static final String IMG_BROWSER_LDIFEDITOR = "icons/browser_ldifeditor.gif";

    public static final String IMG_LDIFEDITOR_NEW = "icons/ldifeditor_new.gif";

    public static final String IMG_LDIF_ADD = "icons/ldif_add.gif";

    public static final String IMG_LDIF_MODIFY = "icons/ldif_modify.gif";

    public static final String IMG_LDIF_DELETE = "icons/ldif_delete.gif";

    public static final String IMG_LDIF_RENAME = "icons/ldif_rename.gif";

    public static final String IMG_LDIF_ATTRIBUTE = "icons/ldif_attribute.gif";

    public static final String IMG_LDIF_VALUE = "icons/ldif_value.gif";

    public static final String IMG_LDIF_MOD_ADD = "icons/ldif_mod_add.gif";

    public static final String IMG_LDIF_MOD_REPLACE = "icons/ldif_mod_replace.gif";

    public static final String IMG_LDIF_MOD_DELETE = "icons/ldif_mod_delete.gif";

    public static final String IMG_LDIF_COMMENT = "icons/ldif_comment.gif";

    public static final String IMG_LDIF_DN = "icons/ldif_dn.gif";

    public static final String IMG_ATD = "icons/atd.gif";

    public static final String IMG_OCD = "icons/ocd.gif";

    public static final String IMG_MRD = "icons/mrd.gif";

    public static final String IMG_MRUD = "icons/mrud.gif";

    public static final String IMG_LSD = "icons/lsd.gif";

    public static final String IMG_MRD_EQUALITY = "icons/mrd_equality.gif";

    public static final String IMG_MRD_SUBSTRING = "icons/mrd_substring.gif";

    public static final String IMG_MRD_ORDERING = "icons/mrd_ordering.gif";

    public static final String IMG_OVR_FILTERED = "icons/ovr16/filtered.gif";

    public static final String IMG_OVR_SEARCHRESULT = "icons/ovr16/searchresult.gif";

    public static final String IMG_OVR_ERROR = "icons/ovr16/error.gif";

    public static final String IMG_OVR_WARNING = "icons/ovr16/warning.gif";

    public static final String IMG_OVR_MARK = "icons/ovr16/mark.gif";

    public static final String IMG_EXECUTE = "icons/execute.gif";

    public static final String IMG_NEXT = "icons/next.gif";

    public static final String IMG_PREVIOUS = "icons/previous.gif";

    public static final int SHOW_DN = 0;

    public static final int SHOW_RDN = 1;

    public static final int SHOW_RDN_VALUE = 2;

}
