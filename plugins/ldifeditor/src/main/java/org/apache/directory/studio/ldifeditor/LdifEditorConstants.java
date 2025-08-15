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
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class LdifEditorConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private LdifEditorConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = LdifEditorConstants.class.getPackage().getName();

    public static final String ACTION_ID_EDIT_RECORD = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "Cmd_EditRecord_id" ); //$NON-NLS-1$

    public static final String ACTION_ID_EDIT_ATTRIBUTE_DESCRIPTION = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "Cmd_EditAttributeDescription_id" ); //$NON-NLS-1$

    public static final String ACTION_ID_FORMAT_LDIF_DOCUMENT = 
        "org.apache.directory.studio.ldifeditor.editor.actions.FormatLdifDocumentAction"; //$NON-NLS-1$

    public static final String ACTION_ID_FORMAT_LDIF_RECORD = 
        "org.apache.directory.studio.ldifeditor.editor.actions.FormatLdifRecordAction"; //$NON-NLS-1$

    public static final String ACTION_ID_EXECUTE_LDIF = 
        "org.apache.directory.studio.ldifeditor.editor.ExecuteLdifAction"; //$NON-NLS-1$

    public static final String NEW_WIZARD_NEW_LDIF_FILE = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "NewWizard_NewLdifFileWizard_id" ); //$NON-NLS-1$

    public static final String EDITOR_LDIF_EDITOR = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "Editor_LdifEditor_id" ); //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP = "ldifEditorFormatterAutoWrap"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_ENABLE = "ldifEditorFoldingEnable"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS = "ldifEditorFoldingInitiallyFoldComments"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS = "ldifEditoroldingInitiallyFoldRecords"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES = "ldifEditorFoldingInitiallyFoldWrappedLines"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK = "ldifEditorDoubleClickUserLdifDoubleClick"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO = "ldifEditorCodeAssistInsertSingleProposalAuto"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION = "ldifEditorCodeAssistEnableAutoActivation"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY = "ldifEditorCodeAssistAutoActivationDelay"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC = "ldifEditorCodeAssistInsertAttributeInModSpec"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_OPTIONS_UPDATEIFENTRYEXISTS = "ldifEditorOptionsUpdateIfEntryExists"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_OPTIONS_CONTINUEONERROR = "ldifEditorOptionsContinueOnError"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX = "_RGB"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX = "_STYLE"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT = "ldifEditorSyntaxComment"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD = "ldifEditorSyntaxKeyword"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_DN = "ldifEditorSyntaxDn"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE = "ldifEditorSyntaxAttribute"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE = "ldifEditorSyntaxValueType"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_VALUE = "ldifEditorSyntaxValue"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD = "ldifEditorSyntaxChangetypeAdd"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY = "ldifEditorSyntaxChangetypeModify"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE = "ldifEditorSyntaxChangetypeDelete"; //$NON-NLS-1$

    public static final String PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN = "ldifEditorSyntaxChangetypeModdn"; //$NON-NLS-1$

    public static final String PREFERENCEPAGEID_LDIFEDITOR = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "PrefPage_LdifEditorPreferencePage_id" ); //$NON-NLS-1$

    public static final String PREFERENCEPAGEID_LDIFEDITOR_CONTENTASSIST = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_LdifEditorContentAssistPreferencePage_id" ); //$NON-NLS-1$

    public static final String PREFERENCEPAGEID_LDIFEDITOR_SYNTAXCOLORING = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_LdifEditorSyntaxColoringPreferencePage_id" ); //$NON-NLS-1$

    public static final String PREFERENCEPAGEID_LDIFEDITOR_TEMPLATES = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "PrefPage_LdifEditorTemplatesPreferencePage_id" ); //$NON-NLS-1$

    public static final String LDIF_FILE_TEMPLATE_ID = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "CtxType_LdifFile_id" ); //$NON-NLS-1$

    public static final String LDIF_ATTR_VAL_RECORD_TEMPLATE_ID = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "CtxType_LdifAttributeValueRecord_id" ); //$NON-NLS-1$

    public static final String LDIF_MODIFICATION_RECORD_TEMPLATE_ID = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "CtxType_LdifModificationRecord_id" ); //$NON-NLS-1$

    public static final String LDIF_MODIFICATION_ITEM_TEMPLATE_ID = LdifEditorActivator.getDefault()
        .getPluginProperties().getString( "CtxType_LdifModificationItem_id" ); //$NON-NLS-1$

    public static final String LDIF_MODDN_RECORD_TEMPLATE_ID = LdifEditorActivator.getDefault().getPluginProperties()
        .getString( "CtxType_LdifModdnRecord_id" ); //$NON-NLS-1$

    public static final String IMG_LDIF_ADD = "resources/icons/ldif_add.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_MODIFY = "resources/icons/ldif_modify.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_DELETE = "resources/icons/ldif_delete.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_RENAME = "resources/icons/ldif_rename.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_ATTRIBUTE = "resources/icons/ldif_attribute.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_VALUE = "resources/icons/ldif_value.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_MOD_ADD = "resources/icons/ldif_mod_add.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_MOD_REPLACE = "resources/icons/ldif_mod_replace.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_MOD_DELETE = "resources/icons/ldif_mod_delete.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_COMMENT = "resources/icons/ldif_comment.png"; //$NON-NLS-1$

    public static final String IMG_LDIF_DN = "resources/icons/ldif_dn.png"; //$NON-NLS-1$

    public static final String IMG_ENTRY = "resources/icons/entry.png"; //$NON-NLS-1$

    public static final String IMG_TEMPLATE = "resources/icons/template.png"; //$NON-NLS-1$

    public static final String IMG_BROWSER_LDIFEDITOR = "resources/icons/ldifeditor.png"; //$NON-NLS-1$

    public static final String IMG_LDIFEDITOR_NEW = "resources/icons/ldifeditor_new.png"; //$NON-NLS-1$

    public static final String IMG_EXECUTE = "resources/icons/execute.png"; //$NON-NLS-1$

    public static final String LDIF_PARTITIONING = "org.apache.directory.studio.ldifeditor.LdifPartitioning"; //$NON-NLS-1$

    public static final String CONTENTASSIST_ACTION = "org.apache.directory.studio.ldapbrowser.ContentAssist"; //$NON-NLS-1$

    public static final String PREFERENCEPAGEID_TEXTFORMATS = 
        "org.apache.directory.studio.ldapbrowser.preferences.TextFormatsPreferencePage"; //$NON-NLS-1$
}
