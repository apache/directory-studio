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
package org.apache.directory.studio.ldifeditor;



/**
 * Constants for the LDIF editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface LdifEditorConstants
{
    /** The plug-in ID */
    public static final String PLUGIN_ID = LdifEditorActivator.getDefault().getPluginProperties().getString(
        "Plugin_id" );

    public static final String ACTION_ID_EDIT_RECORD = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "Cmd_EditRecord_id" );

    public static final String ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "Cmd_EditAttributeDescription_id" );

    public static final String ACTION_ID_FORMAT_LDIF_DOCUMENT = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "Cmd_FormatLdifDocument_id" );

    public static final String ACTION_ID_FORMAT_LDIF_RECORD = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "Cmd_FormatLdifRecord_id" );

    public static final String ACTION_ID_EXECUTE_LDIF = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "Cmd_ExecuteLdif_id" );

    public static final String NEW_WIZARD_NEW_LDIF_FILE = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "NewWizard_NewLdifFileWizard_id" );

    public static final String EDITOR_LDIF_EDITOR = LdifEditorActivator.getDefault().getPluginProperties().getString(
        "Editor_LdifEditor_id" );

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

    public static final String PREFERENCE_LDIFEDITOR_OPTIONS_UPDATEIFENTRYEXISTS = "ldifEditorOptionsUpdateIfEntryExists";

    public static final String PREFERENCE_LDIFEDITOR_OPTIONS_CONTINUEONERROR = "ldifEditorOptionsContinueOnError";

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

    public static final String PREFERENCEPAGEID_LDIFEDITOR = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "PrefPage_LdifEditorPreferencePage_id" );

    public static final String PREFERENCEPAGEID_LDIFEDITOR_CONTENTASSIST = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_LdifEditorContentAssistPreferencePage_id" );

    public static final String PREFERENCEPAGEID_LDIFEDITOR_SYNTAXCOLORING = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_LdifEditorSyntaxColoringPreferencePage_id" );

    public static final String PREFERENCEPAGEID_LDIFEDITOR_TEMPLATES = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_LdifEditorTemplatesPreferencePage_id" );

    public static final String LDIF_FILE_TEMPLATE_ID = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "CtxType_LdifFile_id" );

    public static final String LDIF_ATTR_VAL_RECORD_TEMPLATE_ID = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "CtxType_LdifAttributeValueRecord_id" );

    public static final String LDIF_MODIFICATION_RECORD_TEMPLATE_ID = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "CtxType_LdifModificationRecord_id" );

    public static final String LDIF_MODIFICATION_ITEM_TEMPLATE_ID = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "CtxType_LdifModificationItem_id" );

    public static final String LDIF_MODDN_RECORD_TEMPLATE_ID = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "CtxType_LdifModdnRecord_id" );

    public static final String IMG_LDIF_ADD = "resources/icons/ldif_add.gif";

    public static final String IMG_LDIF_MODIFY = "resources/icons/ldif_modify.gif";

    public static final String IMG_LDIF_DELETE = "resources/icons/ldif_delete.gif";

    public static final String IMG_LDIF_RENAME = "resources/icons/ldif_rename.gif";

    public static final String IMG_LDIF_ATTRIBUTE = "resources/icons/ldif_attribute.gif";

    public static final String IMG_LDIF_VALUE = "resources/icons/ldif_value.gif";

    public static final String IMG_LDIF_MOD_ADD = "resources/icons/ldif_mod_add.gif";

    public static final String IMG_LDIF_MOD_REPLACE = "resources/icons/ldif_mod_replace.gif";

    public static final String IMG_LDIF_MOD_DELETE = "resources/icons/ldif_mod_delete.gif";

    public static final String IMG_LDIF_COMMENT = "resources/icons/ldif_comment.gif";

    public static final String IMG_LDIF_DN = "resources/icons/ldif_dn.gif";

    public static final String IMG_ENTRY = "resources/icons/entry.gif";

    public static final String IMG_TEMPLATE = "resources/icons/template.gif";

    public static final String IMG_BROWSER_LDIFEDITOR = "resources/icons/browser_ldifeditor.gif";

    public static final String IMG_LDIFEDITOR_NEW = "resources/icons/ldifeditor_new.gif";

    public static final String IMG_EXECUTE = "resources/icons/execute.gif";

    public static final String LDIF_PARTITIONING = LdifEditorActivator.getDefault().getPluginProperties().getString(
        "Ldif_Partitioning_id" );

    public static final String CONTENTASSIST_ACTION = LdifEditorActivator.getDefault().getPluginProperties().getString(
        "Action_ContentAssist_id" );

    public static final String PREFERENCEPAGEID_TEXTFORMATS = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "PrefPage_TextFormatsPreferencePage_id" );
}
