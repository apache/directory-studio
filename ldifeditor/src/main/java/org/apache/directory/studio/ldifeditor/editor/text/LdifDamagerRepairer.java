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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.LdifEditorConstants;
import org.apache.directory.studio.ldifeditor.editor.ILdifEditor;
import org.apache.directory.studio.ldifparser.model.LdifEOFPart;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.LdifInvalidPart;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDeloldrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifLineBase;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewsuperiorLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifVersionLine;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


public class LdifDamagerRepairer implements IPresentationDamager, IPresentationRepairer
{

    private ILdifEditor editor;


    // private IDocument document;

    public LdifDamagerRepairer( ILdifEditor editor )
    {
        super();
        this.editor = editor;
        // this.document = null;
    }


    public void setDocument( IDocument document )
    {
        // this.document = document;
    }


    public IRegion getDamageRegion( ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged )
    {
        return partition;
    }


    public void createPresentation( TextPresentation presentation, ITypedRegion damage )
    {

        LdifFile ldifModel = this.editor.getLdifModel();
        LdifContainer[] allContainers = ldifModel.getContainers();
        List containerList = new ArrayList();
        for ( int i = 0; i < allContainers.length; i++ )
        {
            LdifContainer container = allContainers[i];
            Region containerRegion = new Region( container.getOffset(), container.getLength() );
            if ( TextUtilities.overlaps( containerRegion, damage ) )
            {
                containerList.add( container );
            }
        }
        LdifContainer[] containers = ( LdifContainer[] ) containerList
            .toArray( new LdifContainer[containerList.size()] );
        this.highlight( containers, presentation, damage );

        // LdifFile ldifModel = this.editor.getLdifModel();
        // System.out.println(ldifModel.toRawString());
        // LdifContainer[] allContainers = ldifModel.getContainers();
        // this.highlight(allContainers, presentation, null);

    }

    private Map textAttributeKeyToValueMap;


    private TextAttribute geTextAttribute( String key )
    {
        IPreferenceStore store = LdifEditorActivator.getDefault().getPreferenceStore();

        RGB rgb = PreferenceConverter.getColor( store, key
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX );
        int style = store.getInt( key + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX );

        if ( textAttributeKeyToValueMap != null )
        {
            if ( textAttributeKeyToValueMap.containsKey( key
                + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX ) )
            {
                rgb = ( RGB ) textAttributeKeyToValueMap.get( key
                    + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX );
            }
            if ( textAttributeKeyToValueMap.containsKey( key
                + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX ) )
            {
                style = ( ( Integer ) textAttributeKeyToValueMap.get( key
                    + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX ) ).intValue();
            }
        }

        Color color = LdifEditorActivator.getDefault().getColor( rgb );
        TextAttribute textAttribute = new TextAttribute( color, null, style );
        return textAttribute;
    }


    /**
     * Overwrites the style set in preference store
     * 
     * @param key
     *                the key
     *                LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_xxx +
     *                LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX
     *                ore
     *                LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX
     * @param newValue
     *                RGB object or Integer object
     */
    public void setTextAttribute( String key, RGB rgb, int style )
    {
        if ( textAttributeKeyToValueMap == null )
        {
            textAttributeKeyToValueMap = new HashMap();
        }
        textAttributeKeyToValueMap.put( key + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, rgb );
        textAttributeKeyToValueMap.put( key + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX,
            new Integer( style ) );
    }


    private void highlight( LdifContainer[] containers, TextPresentation presentation, ITypedRegion damage )
    {

        // TextAttribute DEFAULT_TEXT_ATTRIBUTE = new
        // TextAttribute(Activator.getDefault().getColor(new RGB(0, 0,
        // 0)));

        TextAttribute COMMENT_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT );
        TextAttribute KEYWORD_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD );
        TextAttribute DN_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_DN );
        TextAttribute ATTRIBUTE_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE );
        TextAttribute VALUETYPE_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE );
        TextAttribute VALUE_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUE );
        TextAttribute ADD_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD );
        TextAttribute MODIFY_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY );
        TextAttribute DELETE_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE );
        TextAttribute MODDN_TEXT_ATTRIBUTE = geTextAttribute( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN );

        for ( int z = 0; z < containers.length; z++ )
        {

            LdifContainer container = containers[z];

            LdifPart[] parts = container.getParts();

            for ( int i = 0; i < parts.length; i++ )
            {

                // int offset = damage.getOffset() + parts[i].getOffset();
                int offset = parts[i].getOffset();

                if ( parts[i] instanceof LdifLineBase )
                {
                    LdifLineBase line = ( LdifLineBase ) parts[i];

                    // String debug = line.getClass().getName() +
                    // "("+line.getOffset()+","+line.getLength()+"):
                    // "+line.toString();
                    // debug = debug.replaceAll("\n", "\\\\n");
                    // debug = debug.replaceAll("\r", "\\\\r");
                    // System.out.println(debug);

                    if ( line instanceof LdifVersionLine )
                    {
                        this.addStyleRange( presentation, offset, line.getLength(), KEYWORD_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifCommentLine )
                    {
                        this.addStyleRange( presentation, offset, line.getLength(), COMMENT_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifDnLine )
                    {
                        LdifDnLine dnLine = ( LdifDnLine ) line;
                        int dnSpecLength = dnLine.getRawDnSpec().length();
                        int valueTypeLength = dnLine.getRawValueType().length();
                        int dnLength = dnLine.getRawDn().length();
                        this.addStyleRange( presentation, offset, dnSpecLength, DN_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + dnSpecLength, valueTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + dnSpecLength + valueTypeLength, dnLength,
                            DN_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifAttrValLine )
                    {
                        LdifAttrValLine attrValLine = ( LdifAttrValLine ) line;
                        int attributeNameLength = attrValLine.getRawAttributeDescription().length();
                        int valueTypeLength = attrValLine.getRawValueType().length();
                        int valueLength = attrValLine.getRawValue().length();
                        this.addStyleRange( presentation, offset, attributeNameLength, ATTRIBUTE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + attributeNameLength, valueTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + attributeNameLength + valueTypeLength, valueLength,
                            VALUE_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifChangeTypeLine )
                    {
                        LdifChangeTypeLine changeTypeLine = ( LdifChangeTypeLine ) line;
                        int changeTypeSpecLength = changeTypeLine.getRawChangeTypeSpec().length();
                        int valueTypeLength = changeTypeLine.getRawValueType().length();
                        int changeTypeLength = changeTypeLine.getRawChangeType().length();
                        this.addStyleRange( presentation, offset, changeTypeSpecLength, KEYWORD_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + changeTypeSpecLength, valueTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );

                        if ( container instanceof LdifChangeAddRecord )
                        {
                            this.addStyleRange( presentation, offset + changeTypeSpecLength + valueTypeLength,
                                changeTypeLength, ADD_TEXT_ATTRIBUTE );
                        }
                        else if ( container instanceof LdifChangeModifyRecord )
                        {
                            this.addStyleRange( presentation, offset + changeTypeSpecLength + valueTypeLength,
                                changeTypeLength, MODIFY_TEXT_ATTRIBUTE );
                        }
                        else if ( container instanceof LdifChangeModDnRecord )
                        {
                            this.addStyleRange( presentation, offset + changeTypeSpecLength + valueTypeLength,
                                changeTypeLength, MODDN_TEXT_ATTRIBUTE );
                        }
                        else if ( container instanceof LdifChangeDeleteRecord )
                        {
                            this.addStyleRange( presentation, offset + changeTypeSpecLength + valueTypeLength,
                                changeTypeLength, DELETE_TEXT_ATTRIBUTE );
                        }
                    }
                    else if ( line instanceof LdifNewrdnLine )
                    {
                        LdifNewrdnLine newrdnLine = ( LdifNewrdnLine ) line;
                        int newrdnSpecLength = newrdnLine.getRawNewrdnSpec().length();
                        int valueTypeLength = newrdnLine.getRawValueType().length();
                        int newrdnLength = newrdnLine.getRawNewrdn().length();
                        this.addStyleRange( presentation, offset, newrdnSpecLength, KEYWORD_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + newrdnSpecLength, valueTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + newrdnSpecLength + valueTypeLength, newrdnLength,
                            VALUE_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifDeloldrdnLine )
                    {
                        LdifDeloldrdnLine deleteoldrdnLine = ( LdifDeloldrdnLine ) line;
                        int deleteoldrdnSpecLength = deleteoldrdnLine.getRawDeleteOldrdnSpec().length();
                        int valueTypeLength = deleteoldrdnLine.getRawValueType().length();
                        int deleteoldrdnLength = deleteoldrdnLine.getRawDeleteOldrdn().length();
                        this.addStyleRange( presentation, offset, deleteoldrdnSpecLength, KEYWORD_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + deleteoldrdnSpecLength, valueTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + deleteoldrdnSpecLength + valueTypeLength,
                            deleteoldrdnLength, VALUE_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifNewsuperiorLine )
                    {
                        LdifNewsuperiorLine newsuperiorLine = ( LdifNewsuperiorLine ) line;
                        int newsuperiorSpecLength = newsuperiorLine.getRawNewSuperiorSpec().length();
                        int valueTypeLength = newsuperiorLine.getRawValueType().length();
                        int newsuperiorLength = newsuperiorLine.getRawNewSuperiorDn().length();
                        this.addStyleRange( presentation, offset, newsuperiorSpecLength, KEYWORD_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + newsuperiorSpecLength, valueTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + newsuperiorSpecLength + valueTypeLength,
                            newsuperiorLength, VALUE_TEXT_ATTRIBUTE );
                    }
                    // else if(line instanceof LdifDeloldrdnLine) {
                    // this.addStyleRange(presentation, offset,
                    // line.getLength(), MODTYPE_TEXT_ATTRIBUTE);
                    // }
                    // else if(line instanceof LdifNewsuperiorLine) {
                    // this.addStyleRange(presentation, offset,
                    // line.getLength(), MODTYPE_TEXT_ATTRIBUTE);
                    // }
                    else if ( line instanceof LdifModSpecTypeLine )
                    {
                        LdifModSpecTypeLine modSpecTypeLine = ( LdifModSpecTypeLine ) line;
                        int modTypeLength = modSpecTypeLine.getRawModType().length();
                        int valueTypeLength = modSpecTypeLine.getRawValueType().length();
                        int attributeDescriptionLength = modSpecTypeLine.getRawAttributeDescription().length();
                        this.addStyleRange( presentation, offset, modTypeLength, KEYWORD_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + modTypeLength, valueTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + modTypeLength + valueTypeLength,
                            attributeDescriptionLength, ATTRIBUTE_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifModSpecSepLine )
                    {
                        this.addStyleRange( presentation, offset, line.getLength(), VALUETYPE_TEXT_ATTRIBUTE );
                    }
                    else if ( line instanceof LdifControlLine )
                    {
                        LdifControlLine controlLine = ( LdifControlLine ) line;
                        int controlSpecLength = controlLine.getRawControlSpec().length();
                        int controlTypeLength = controlLine.getRawControlType().length();
                        int oidLength = controlLine.getRawOid().length();
                        int critLength = controlLine.getRawCriticality().length();
                        int valueTypeLength = controlLine.getRawControlValueType().length();
                        int valueLength = controlLine.getRawControlValue().length();
                        this.addStyleRange( presentation, offset, controlSpecLength, KEYWORD_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + controlSpecLength, controlTypeLength,
                            VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + controlSpecLength + controlTypeLength, oidLength,
                            ATTRIBUTE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + controlSpecLength + controlTypeLength + oidLength,
                            critLength, KEYWORD_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + controlSpecLength + controlTypeLength + oidLength
                            + critLength, valueTypeLength, VALUETYPE_TEXT_ATTRIBUTE );
                        this.addStyleRange( presentation, offset + controlSpecLength + controlTypeLength + oidLength
                            + critLength + valueTypeLength, valueLength, VALUE_TEXT_ATTRIBUTE );
                    }
                    else
                    {
                        // this.addStyleRange(presentation, offset,
                        // line.getLength(), DEFAULT_TEXT_ATTRIBUTE);
                    }
                }
                else if ( parts[i] instanceof LdifModSpec )
                {
                    LdifModSpec modSpec = ( LdifModSpec ) parts[i];
                    this.highlight( new LdifContainer[]
                        { modSpec }, presentation, damage );

                }
                else if ( parts[i] instanceof LdifInvalidPart )
                {
                    // LdifUnknownPart unknownPart =
                    // (LdifUnknownPart)parts[i];
                    // this.addStyleRange(presentation, offset,
                    // unknownPart.getLength(), UNKNOWN_TEXT_ATTRIBUTE);
                    // this.addStyleRange(presentation, offset,
                    // parts[i].getLength(), DEFAULT_TEXT_ATTRIBUTE);
                }
                else if ( parts[i] instanceof LdifEOFPart )
                {
                    // ignore
                }
                else
                {
                    // TODO
                    System.out.println( "LdifDamagerRepairer: Unspecified Token: " + parts[i].getClass() ); //$NON-NLS-1$
                }

            }
        }

    }


    private void addStyleRange( TextPresentation presentation, int offset, int length, TextAttribute textAttribute )
    {
        if ( offset >= 0 && length > 0 )
        {
            StyleRange range = new StyleRange( offset, length, textAttribute.getForeground(), textAttribute
                .getBackground(), textAttribute.getStyle() & ( SWT.BOLD | SWT.ITALIC ) );
            range.underline = ( textAttribute.getStyle() & TextAttribute.UNDERLINE ) != 0;
            range.strikeout = ( textAttribute.getStyle() & TextAttribute.STRIKETHROUGH ) != 0;
            presentation.addStyleRange( range );
        }
    }

}
