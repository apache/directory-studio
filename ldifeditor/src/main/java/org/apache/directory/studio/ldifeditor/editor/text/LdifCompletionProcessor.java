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

package org.apache.directory.studio.ldifeditor.editor.text;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.apache.directory.studio.ldifeditor.editor.ILdifEditor;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.LdifInvalidPart;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifInvalidContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifSepContainer;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;


public class LdifCompletionProcessor extends TemplateCompletionProcessor
{

    // private final static String DN = "dn: ";
    private final static String CT_ADD = "changetype: add" + BrowserCoreConstants.LINE_SEPARATOR;

    private final static String CT_MODIFY = "changetype: modify" + BrowserCoreConstants.LINE_SEPARATOR;

    private final static String CT_DELETE = "changetype: delete" + BrowserCoreConstants.LINE_SEPARATOR;

    private final static String CT_MODDN = "changetype: moddn" + BrowserCoreConstants.LINE_SEPARATOR;

    private final static String MD_NEWRDN = "newrdn: ";

    private final static String MD_DELETEOLDRDN_TRUE = "deleteoldrdn: 1";

    // private final static String MD_DELETEOLDRDN_FALSE = "deleteoldrdn:
    // 0";
    private final static String MD_NEWSUPERIOR = "newsuperior: ";

    private final ILdifEditor editor;

    private final ContentAssistant contentAssistant;


    public LdifCompletionProcessor( ILdifEditor editor, ContentAssistant contentAssistant )
    {
        this.editor = editor;
        this.contentAssistant = contentAssistant;
    }


    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {

        IPreferenceStore store = LdifEditorActivator.getDefault().getPreferenceStore();
        contentAssistant.enableAutoInsert( store
            .getBoolean( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO ) );
        contentAssistant.enableAutoActivation( store
            .getBoolean( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION ) );
        contentAssistant.setAutoActivationDelay( store
            .getInt( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY ) );

        List proposalList = new ArrayList();

        LdifFile model = editor.getLdifModel();
        LdifContainer container = LdifFile.getContainer( model, offset );
        LdifContainer innerContainer = container != null ? LdifFile.getInnerContainer( container, offset ) : null;
        LdifPart part = container != null ? LdifFile.getContainerContent( container, offset ) : null;
        int documentLine = -1;
        int documentLineOffset = -1;
        String prefix = "";
        try
        {
            documentLine = viewer.getDocument().getLineOfOffset( offset );
            documentLineOffset = viewer.getDocument().getLineOffset( documentLine );
            prefix = viewer.getDocument().get( documentLineOffset, offset - documentLineOffset );
        }
        catch ( BadLocationException e )
        {
        }
        // TemplateContextType contextType = getContextType(viewer, new
        // Region(offset, 0));

        // Add context dependend template proposals
        ICompletionProposal[] templateProposals = super.computeCompletionProposals( viewer, offset );
        if ( templateProposals != null )
        {
            proposalList.addAll( Arrays.asList( templateProposals ) );
        }

        // changetype: xxx
        if ( container instanceof LdifRecord )
        {
            LdifRecord record = ( LdifRecord ) container;
            LdifPart[] parts = record.getParts();
            if ( parts.length > 1 && ( !( parts[1] instanceof LdifChangeTypeLine ) || !parts[1].isValid() ) )
            {
                if ( CT_ADD.startsWith( prefix ) )
                    proposalList.add( new CompletionProposal( CT_ADD, offset - prefix.length(), prefix.length(), CT_ADD
                        .length(), LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_ADD ), CT_ADD
                        .substring( 0, CT_ADD.length() - BrowserCoreConstants.LINE_SEPARATOR.length() ), null, null ) );
                if ( CT_MODIFY.startsWith( prefix ) )
                    proposalList.add( new CompletionProposal( CT_MODIFY, offset - prefix.length(), prefix.length(),
                        CT_MODIFY.length(),
                        LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MODIFY ), CT_MODIFY
                            .substring( 0, CT_MODIFY.length() - BrowserCoreConstants.LINE_SEPARATOR.length() ), null,
                        null ) );
                if ( CT_DELETE.startsWith( prefix ) )
                    proposalList.add( new CompletionProposal( CT_DELETE, offset - prefix.length(), prefix.length(),
                        CT_DELETE.length(),
                        LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_DELETE ), CT_DELETE
                            .substring( 0, CT_DELETE.length() - BrowserCoreConstants.LINE_SEPARATOR.length() ), null,
                        null ) );
                if ( CT_MODDN.startsWith( prefix ) )
                    proposalList.add( new CompletionProposal( CT_MODDN, offset - prefix.length(), prefix.length(),
                        CT_MODDN.length(), LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_RENAME ),
                        CT_MODDN.substring( 0, CT_MODDN.length() - BrowserCoreConstants.LINE_SEPARATOR.length() ),
                        null, null ) );
            }

        }

        // changetype: modify
        if ( container instanceof LdifChangeModDnRecord )
        {
            LdifChangeModDnRecord record = ( LdifChangeModDnRecord ) container;
            if ( ( record.getNewrdnLine() == null || !record.getNewrdnLine().isValid() )
                && MD_NEWRDN.startsWith( prefix ) )
            {
                proposalList.add( new CompletionProposal( MD_NEWRDN, offset - prefix.length(), prefix.length(),
                    MD_NEWRDN.length(), null, null, null, null ) );
            }
            if ( ( record.getDeloldrdnLine() == null || !record.getDeloldrdnLine().isValid() )
                && MD_DELETEOLDRDN_TRUE.startsWith( prefix ) )
            {
                proposalList.add( new CompletionProposal( MD_DELETEOLDRDN_TRUE, offset - prefix.length(), prefix
                    .length(), MD_DELETEOLDRDN_TRUE.length(), null, null, null, null ) );
            }
            if ( ( record.getNewsuperiorLine() == null || !record.getNewsuperiorLine().isValid() )
                && MD_NEWSUPERIOR.startsWith( prefix ) )
            {
                proposalList.add( new CompletionProposal( MD_NEWSUPERIOR, offset - prefix.length(), prefix.length(),
                    MD_NEWSUPERIOR.length(), null, null, null, null ) );
            }
        }

        // modspecs
        if ( innerContainer instanceof LdifModSpec )
        {
            LdifModSpec modSpec = ( LdifModSpec ) innerContainer;
            String att = modSpec.getModSpecType().getRawAttributeDescription();
            if ( att != null && att.startsWith( prefix ) )
            {
                proposalList.add( new CompletionProposal( att, offset - prefix.length(), prefix.length(), att.length(),
                    null, null, null, null ) );
            }
        }

        // attribute descriptions
        if ( container instanceof LdifContentRecord || container instanceof LdifChangeAddRecord )
        {

            if ( part instanceof LdifInvalidPart
                || part instanceof LdifAttrValLine
                || ( part instanceof LdifSepLine && ( container instanceof LdifContentRecord || container instanceof LdifChangeAddRecord ) ) )
            {

                String rawAttributeDescription = prefix;
                String rawValueType = "";

                if ( part instanceof LdifAttrValLine )
                {
                    LdifAttrValLine line = ( LdifAttrValLine ) part;
                    rawAttributeDescription = line.getRawAttributeDescription();
                    rawValueType = line.getRawValueType();
                }

                if ( offset <= part.getOffset() + rawAttributeDescription.length() )
                {
                    Schema schema = editor.getConnection() != null ? editor.getConnection().getSchema()
                        : Schema.DEFAULT_SCHEMA;
                    String[] attributeNames = schema.getAttributeTypeDescriptionNames();
                    Arrays.sort( attributeNames );
                    for ( int a = 0; a < attributeNames.length; a++ )
                    {
                        if ( rawAttributeDescription.length() == 0
                            || attributeNames[a].toLowerCase().startsWith( rawAttributeDescription.toLowerCase() ) )
                        {

                            String proposal = attributeNames[a];

                            if ( rawValueType.length() == 0 )
                            {
                                if ( schema.getAttributeTypeDescription( proposal ).isBinary() )
                                {
                                    proposal += ":: ";
                                }
                                else
                                {
                                    proposal += ": ";
                                }
                            }

                            proposalList
                                .add( new CompletionProposal( proposal, offset - rawAttributeDescription.length(),
                                    rawAttributeDescription.length(), proposal.length() ) );
                        }
                    }
                }
            }
        }

        // comment
        boolean commentOnly = false;
        if ( documentLineOffset == offset )
        {
            commentOnly = proposalList.isEmpty();
            proposalList.add( new CompletionProposal( "# ", offset, 0, 2, LdifEditorActivator.getDefault().getImage(
                LdifEditorConstants.IMG_LDIF_COMMENT ), "# - Comment", null, null ) );
        }

        // adjust auto-insert
        this.contentAssistant.enableAutoInsert( !commentOnly );

        ICompletionProposal[] proposals = ( ICompletionProposal[] ) proposalList.toArray( new ICompletionProposal[0] );
        return proposals;

    }


    protected String extractPrefix( ITextViewer viewer, int offset )
    {

        IDocument document = viewer.getDocument();
        if ( offset > document.getLength() )
            return ""; //$NON-NLS-1$

        try
        {
            int documentLine = viewer.getDocument().getLineOfOffset( offset );
            int documentLineOffset = viewer.getDocument().getLineOffset( documentLine );
            String prefix = viewer.getDocument().get( documentLineOffset, offset - documentLineOffset );
            return prefix;
        }
        catch ( BadLocationException e )
        {
            return ""; //$NON-NLS-1$
        }
    }


    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return null;
    }


    public char[] getCompletionProposalAutoActivationCharacters()
    {

        char[] chars = new char[53];
        for ( int i = 0; i < 26; i++ )
            chars[i] = ( char ) ( 'a' + i );
        for ( int i = 0; i < 26; i++ )
            chars[i + 26] = ( char ) ( 'A' + i );
        chars[52] = ':';

        return chars;
    }


    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }


    public String getErrorMessage()
    {
        return null;
    }


    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }


    protected Template[] getTemplates( String contextTypeId )
    {
        Template[] templates = LdifEditorActivator.getDefault().getLdifTemplateStore().getTemplates( contextTypeId );
        return templates;
    }


    protected TemplateContextType getContextType( ITextViewer viewer, IRegion region )
    {

        int offset = region.getOffset();

        LdifFile model = editor.getLdifModel();
        LdifContainer container = LdifFile.getContainer( model, offset );
        LdifContainer innerContainer = container != null ? LdifFile.getInnerContainer( container, offset ) : null;
        LdifPart part = container != null ? LdifFile.getContainerContent( container, offset ) : null;
        int documentLine = -1;
        int documentLineOffset = -1;
        String prefix = "";
        try
        {
            documentLine = viewer.getDocument().getLineOfOffset( offset );
            documentLineOffset = viewer.getDocument().getLineOffset( documentLine );
            prefix = viewer.getDocument().get( documentLineOffset, offset - documentLineOffset );
        }
        catch ( BadLocationException e )
        {
        }

        // FILE
        if ( container == null && innerContainer == null && part == null )
        {
            return LdifEditorActivator.getDefault().getLdifTemplateContextTypeRegistry().getContextType(
                LdifEditorConstants.LDIF_FILE_TEMPLATE_ID );
        }
        if ( container instanceof LdifSepContainer && innerContainer == null && part instanceof LdifSepLine )
        {
            return LdifEditorActivator.getDefault().getLdifTemplateContextTypeRegistry().getContextType(
                LdifEditorConstants.LDIF_FILE_TEMPLATE_ID );
        }
        if ( ( container instanceof LdifInvalidContainer && part instanceof LdifInvalidPart && "d".equals( prefix ) )
            || ( container instanceof LdifContentRecord && part instanceof LdifInvalidPart && "dn".equals( prefix ) )
            || ( container instanceof LdifContentRecord && part instanceof LdifInvalidPart && "dn:".equals( prefix ) ) )
        {
            return LdifEditorActivator.getDefault().getLdifTemplateContextTypeRegistry().getContextType(
                LdifEditorConstants.LDIF_FILE_TEMPLATE_ID );
        }

        // MODIFICATION RECORD
        if ( container instanceof LdifChangeModifyRecord && innerContainer == null
            && ( part instanceof LdifSepLine || part instanceof LdifInvalidPart ) )
        {
            return LdifEditorActivator.getDefault().getLdifTemplateContextTypeRegistry().getContextType(
                LdifEditorConstants.LDIF_MODIFICATION_RECORD_TEMPLATE_ID );
        }

        // MODIFICATION ITEM
        if ( container instanceof LdifChangeModifyRecord && innerContainer instanceof LdifModSpec )
        {
            return LdifEditorActivator.getDefault().getLdifTemplateContextTypeRegistry().getContextType(
                LdifEditorConstants.LDIF_MODIFICATION_ITEM_TEMPLATE_ID );
        }

        // MODDN RECORD
        if ( container instanceof LdifChangeModDnRecord && innerContainer == null
            && ( part instanceof LdifSepLine || part instanceof LdifInvalidPart ) )
        {
            return LdifEditorActivator.getDefault().getLdifTemplateContextTypeRegistry().getContextType(
                LdifEditorConstants.LDIF_MODDN_RECORD_TEMPLATE_ID );
        }

        // TemplateContextType contextType =
        // Activator.getDefault().getContextTypeRegistry().getContextType(LdifEditorConstants.LDIF_FILE_TEMPLATE_ID);
        // TemplateContextType contextType =
        // Activator.getDefault().getContextTypeRegistry().getContextType(LdifEditorConstants.LDIF_MODIFICATION_RECORD_TEMPLATE_ID);

        return null;

    }


    protected Image getImage( Template template )
    {

        if ( template.getPattern().indexOf( "add: " ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MOD_ADD );
        }
        else if ( template.getPattern().indexOf( "replace: " ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MOD_REPLACE );
        }
        else if ( template.getPattern().indexOf( "delete: " ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MOD_DELETE );
        }

        else if ( template.getPattern().indexOf( "changetype: add" ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_ADD );
        }
        else if ( template.getPattern().indexOf( "changetype: modify" ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_MODIFY );
        }
        else if ( template.getPattern().indexOf( "changetype: delete" ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_DELETE );
        }
        else if ( template.getPattern().indexOf( "changetype: moddn" ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_LDIF_RENAME );
        }
        else if ( template.getPattern().indexOf( "dn: " ) > -1 )
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_ENTRY );
        }

        else
        {
            return LdifEditorActivator.getDefault().getImage( LdifEditorConstants.IMG_TEMPLATE );
        }

    }

}
