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
package org.apache.directory.studio.templateeditor.model.parser;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.directory.studio.templateeditor.model.AbstractTemplate;
import org.apache.directory.studio.templateeditor.model.ExtensionPointTemplate;
import org.apache.directory.studio.templateeditor.model.FileTemplate;
import org.apache.directory.studio.templateeditor.model.Template;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateCheckbox;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateComposite;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateDate;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateFileChooser;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateForm;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateImage;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateLabel;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateLink;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateListbox;
import org.apache.directory.studio.templateeditor.model.widgets.TemplatePassword;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateRadioButtons;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateSection;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateSpinner;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateTable;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateTextField;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateWidget;
import org.apache.directory.studio.templateeditor.model.widgets.ValueItem;
import org.apache.directory.studio.templateeditor.model.widgets.WidgetAlignment;
import org.apache.directory.studio.templateeditor.view.preferences.PreferencesFileTemplate;


/**
 * This class is used to read/write the 'connections.xml' file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateIO
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( TemplateIO.class );

    private static final String THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE = Messages
        .getString( "TemplateIO.FileIsNotAValidTemplateFile" ); //$NON-NLS-1$

    // XML Elements
    private static final String ATTRIBUTE_ATTRIBUTETYPE = "attributeType"; //$NON-NLS-1$
    private static final String ATTRIBUTE_CHARACTERSLIMIT = "charactersLimit"; //$NON-NLS-1$
    private static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$
    private static final String ATTRIBUTE_DIGITS = "digits"; //$NON-NLS-1$
    private static final String ATTRIBUTE_DOLLAR_SIGN_IS_NEW_LINE = "dollarSignIsNewLine"; //$NON-NLS-1$
    private static final String ATTRIBUTE_EXTENSIONS = "extensions"; //$NON-NLS-1$
    private static final String ATTRIBUTE_EQUALCOLUMNS = "equalColumns"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ENABLED = "enabled"; //$NON-NLS-1$
    private static final String ATTRIBUTE_EXPANDABLE = "expandable"; //$NON-NLS-1$
    private static final String ATTRIBUTE_EXPANDED = "expanded"; //$NON-NLS-1$
    private static final String ATTRIBUTE_FORMAT = "format"; //$NON-NLS-1$
    private static final String ATTRIBUTE_GRAB_EXCESS_HORIZONTAL_SPACE = "grabExcessHorizontalSpace"; //$NON-NLS-1$
    private static final String ATTRIBUTE_GRAB_EXCESS_VERTICAL_SPACE = "grabExcessVerticalSpace"; //$NON-NLS-1$
    private static final String ATTRIBUTE_HIDDEN = "hidden"; //$NON-NLS-1$
    private static final String ATTRIBUTE_HEIGHT = "height"; //$NON-NLS-1$
    private static final String ATTRIBUTE_HORIZONTAL_ALIGNMENT = "horizontalAlignment"; //$NON-NLS-1$
    private static final String ATTRIBUTE_HORIZONTAL_SPAN = "horizontalSpan"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
    private static final String ATTRIBUTE_IMAGE_HEIGHT = "imageHeight"; //$NON-NLS-1$
    private static final String ATTRIBUTE_IMAGE_WIDTH = "imageWidth"; //$NON-NLS-1$
    private static final String ATTRIBUTE_INCREMENT = "increment"; //$NON-NLS-1$
    private static final String ATTRIBUTE_LABEL = "label"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MAXIMUM = "maximum"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MINIMUM = "minimum"; //$NON-NLS-1$
    private static final String ATTRIBUTE_MULTIPLESELECTION = "multipleSelection"; //$NON-NLS-1$
    private static final String ATTRIBUTE_NUMBEROFCOLUMNS = "numberOfColumns"; //$NON-NLS-1$
    private static final String ATTRIBUTE_NUMBEROFROWS = "numberOfRows"; //$NON-NLS-1$
    private static final String ATTRIBUTE_PAGEINCREMENT = "pageIncrement"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWADDBUTTON = "showAddButton"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWBROWSEBUTTON = "showBrowseButton"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWCLEARBUTTON = "showClearButton"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWDELETEBUTTON = "showDeleteButton"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWEDITBUTTON = "showEditButton"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWICON = "showIcon"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWSAVEASBUTTON = "showSaveAsButton"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHOWSHOWPASSWORDCHECKBOX = "showShowPasswordCheckbox"; //$NON-NLS-1$
    private static final String ATTRIBUTE_TITLE = "title"; //$NON-NLS-1$
    private static final String ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$
    private static final String ATTRIBUTE_VERTICAL_ALIGNMENT = "verticalAlignment"; //$NON-NLS-1$
    private static final String ATTRIBUTE_VERTICAL_SPAN = "verticalSpan"; //$NON-NLS-1$
    private static final String ATTRIBUTE_WIDTH = "width"; //$NON-NLS-1$
    private static final String ELEMENT_AUXILIARIES = "auxiliaries"; //$NON-NLS-1$
    private static final String ELEMENT_AUXILIARY = "auxiliary"; //$NON-NLS-1$
    private static final String ELEMENT_BUTTON = "button"; //$NON-NLS-1$
    private static final String ELEMENT_BUTTONS = "buttons"; //$NON-NLS-1$
    private static final String ELEMENT_CHECKBOX = "checkbox"; //$NON-NLS-1$
    private static final String ELEMENT_CHECKEDVALUE = "checkedValue"; //$NON-NLS-1$
    private static final String ELEMENT_COMPOSITE = "composite"; //$NON-NLS-1$
    private static final String ELEMENT_DATA = "data"; //$NON-NLS-1$
    private static final String ELEMENT_DATE = "date"; //$NON-NLS-1$
    private static final String ELEMENT_FILECHOOSER = "fileChooser"; //$NON-NLS-1$
    private static final String ELEMENT_FORM = "form"; //$NON-NLS-1$
    private static final String ELEMENT_ICON = "icon"; //$NON-NLS-1$
    private static final String ELEMENT_IMAGE = "image"; //$NON-NLS-1$
    private static final String ELEMENT_ITEM = "item"; //$NON-NLS-1$
    private static final String ELEMENT_ITEMS = "items"; //$NON-NLS-1$
    private static final String ELEMENT_LABEL = "label"; //$NON-NLS-1$
    private static final String ELEMENT_LINK = "link"; //$NON-NLS-1$
    private static final String ELEMENT_LISTBOX = "listbox"; //$NON-NLS-1$
    private static final String ELEMENT_OBJECTCLASSES = "objectClasses"; //$NON-NLS-1$
    private static final String ELEMENT_PASSWORD = "password"; //$NON-NLS-1$
    private static final String ELEMENT_RADIOBUTTONS = "radiobuttons"; //$NON-NLS-1$
    private static final String ELEMENT_SECTION = "section"; //$NON-NLS-1$
    private static final String ELEMENT_SPINNER = "spinner"; //$NON-NLS-1$
    private static final String ELEMENT_STRUCTURAL = "structural"; //$NON-NLS-1$
    private static final String ELEMENT_TABLE = "table"; //$NON-NLS-1$
    private static final String ELEMENT_TEMPLATE = "template"; //$NON-NLS-1$
    private static final String ELEMENT_TEXTFIELD = "textfield"; //$NON-NLS-1$
    private static final String ELEMENT_VALUE = "value"; //$NON-NLS-1$
    private static final String ELEMENT_UNCHECKEDVALUE = "uncheckedValue"; //$NON-NLS-1$
    private static final String VALUE_BEGINNING = "beginning"; //$NON-NLS-1$
    private static final String VALUE_CENTER = "center"; //$NON-NLS-1$
    private static final String VALUE_END = "end"; //$NON-NLS-1$
    private static final String VALUE_FALSE = "false"; //$NON-NLS-1$
    private static final String VALUE_FILL = "fill"; //$NON-NLS-1$
    private static final String VALUE_NONE = "none"; //$NON-NLS-1$
    private static final String VALUE_TRUE = "true"; //$NON-NLS-1$


    /**
     * Reads the input stream as a file template
     *
     * @param is
     *      the input stream
     * @return
     *      the template
     * @throws TemplateIOException 
     *      if an error occurs when converting the document
     */
    public static FileTemplate readAsFileTemplate( InputStream is ) throws TemplateIOException
    {
        // Creating the FileTemplate
        FileTemplate template = new FileTemplate();

        // Reading the template
        readTemplate( is, template );

        // Returning the template
        return template;
    }


    /**
     * Reads the input stream as a preferences file template
     *
     * @param is
     *      the input stream
     * @return
     *      the template
     * @throws TemplateIOException 
     *      if an error occurs when converting the document
     */
    public static PreferencesFileTemplate readAsPreferencesFileTemplate( InputStream is ) throws TemplateIOException
    {
        // Creating the PreferencesFileTemplate
        PreferencesFileTemplate template = new PreferencesFileTemplate();

        // Reading the template
        readTemplate( is, template );

        // Returning the template
        return template;
    }


    /**
     * Reads the input stream as a file template
     *
     * @param is
     *      the input stream
     * @return
     *      the template
     * @throws TemplateIOException 
     *      if an error occurs when converting the document
     */
    public static ExtensionPointTemplate readAsExtensionPointTemplate( InputStream is ) throws TemplateIOException
    {
        // Creating the FileTemplate
        ExtensionPointTemplate template = new ExtensionPointTemplate();

        // Reading the template
        readTemplate( is, template );

        // Returning the template
        return template;
    }


    /**
     * Reads the input stream as a file template
    *
    * @param is
    *      the input stream
    * @return
    *      the template
    * @throws TemplateIOException 
    *      if an error occurs when converting the document
    */
    public static void readTemplate( InputStream is, Template template ) throws TemplateIOException
    {
        // Getting the document
        Document document = getDocument( is );

        // Reading the template.
        readTemplate( document.getRootElement(), template );
    }


    /**
     * Gets the document associated with the input stream.
     *
     * @param stream
     *      the input stream
     * @return
     * @throws TemplateIOException
     */
    private static Document getDocument( InputStream is ) throws TemplateIOException
    {
        try
        {
            return ( new SAXReader() ).read( is );
        }
        catch ( DocumentException e )
        {
            throw new TemplateIOException( e.getMessage() );
        }
    }


    /**
     * Reads the template.
     *
     * @param rootElement
     *      the root element
     * @param template
     *      the template
     */
    private static void readTemplate( Element rootElement, Template template ) throws TemplateIOException
    {
        LOG.debug( "Reading the template" ); //$NON-NLS-1$

        // Verifying the root 'template' element
        if ( ( rootElement == null ) || ( !rootElement.getName().equalsIgnoreCase( ELEMENT_TEMPLATE ) ) )
        {
            LOG.error( "Unable to find element: '" + ELEMENT_TEMPLATE + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindElement" ), ELEMENT_TEMPLATE ) ); //$NON-NLS-1$
        }

        // Reading the ID
        Attribute idAttribute = rootElement.attribute( ATTRIBUTE_ID );
        if ( ( idAttribute != null ) && ( idAttribute.getText() != null ) )
        {
            // Verifying if the ID is valid
            if ( AbstractTemplate.isValidId( idAttribute.getText() ) )
            {
                LOG.debug( "ID='" + idAttribute.getText() + "'" ); //$NON-NLS-1$ //$NON-NLS-2$
                template.setId( idAttribute.getText() );
            }
            else
            {
                LOG.error( "Invalid ID attribute: '" + idAttribute.getText() + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
                throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                    + NLS.bind( Messages.getString( "TemplateIO.InvalidIdAttribute" ), idAttribute.getText() ) ); //$NON-NLS-1$
            }
        }
        else
        {
            LOG.error( "Unable to find attribute or attribute empty: '" + ATTRIBUTE_ID + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.AttributeNotFoundOrEmpty" ), ATTRIBUTE_ID ) ); //$NON-NLS-1$
        }

        // Reading the title
        Attribute titleAttribute = rootElement.attribute( ATTRIBUTE_TITLE );
        if ( ( titleAttribute != null ) && ( titleAttribute.getText() != null ) )
        {
            LOG.debug( "Title='" + titleAttribute.getText() + "'" ); //$NON-NLS-1$ //$NON-NLS-2$
            template.setTitle( titleAttribute.getText() );
        }
        else
        {

            LOG.error( "Unable to find attribute or attribute empty: '" + ATTRIBUTE_TITLE + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.AttributeNotFoundOrEmpty" ), ATTRIBUTE_TITLE ) ); //$NON-NLS-1$
        }

        // Reading the object classes
        readObjectClasses( rootElement, template );

        // Reading the form
        readForm( rootElement, template );
    }


    /**
     * Reads the object classes for the template.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readObjectClasses( Element element, Template template ) throws TemplateIOException
    {
        LOG.debug( "Reading the template's object classes" ); //$NON-NLS-1$

        // Reading the 'objectClasses' element
        Element objectClassesElement = element.element( ELEMENT_OBJECTCLASSES );
        if ( objectClassesElement == null )
        {
            LOG.error( "Unable to find element: '" + ELEMENT_OBJECTCLASSES + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindElement" ), ELEMENT_OBJECTCLASSES ) ); //$NON-NLS-1$
        }

        // Reading the 'structural' element
        Element structuralElement = objectClassesElement.element( ELEMENT_STRUCTURAL );
        if ( structuralElement != null )
        {
            String structuralObjectClassText = structuralElement.getText();
            if ( ( structuralObjectClassText != null ) && ( !structuralObjectClassText.equals( "" ) ) ) //$NON-NLS-1$
            {
                template.setStructuralObjectClass( structuralObjectClassText );
            }
        }
        else
        {
            LOG.error( "Unable to find any: '" + ELEMENT_STRUCTURAL + "' element." ); //$NON-NLS-1$ //$NON-NLS-2$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindAnyElement" ), ELEMENT_STRUCTURAL ) ); //$NON-NLS-1$
        }

        // Reading the 'auxiliaries' element
        Element auxliariesElement = objectClassesElement.element( ELEMENT_AUXILIARIES );
        if ( auxliariesElement != null )
        {
            // Reading the auxiliaries object classes
            for ( Iterator<?> i = auxliariesElement.elementIterator( ELEMENT_AUXILIARY ); i.hasNext(); )
            {
                Element auxliaryObjectClassElement = ( Element ) i.next();
                String auxliaryObjectClassText = auxliaryObjectClassElement.getText();
                if ( ( auxliaryObjectClassText != null ) && ( !auxliaryObjectClassText.equals( "" ) ) ) //$NON-NLS-1$
                {
                    template.addAuxiliaryObjectClass( auxliaryObjectClassText );
                }
            }
        }
    }


    /**
     * Reads the form for the template.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readForm( Element element, Template template ) throws TemplateIOException
    {
        LOG.debug( "Reading the template's form" ); //$NON-NLS-1$

        // Reading the 'form' element
        Element formElement = element.element( ELEMENT_FORM );
        if ( formElement == null )
        {
            LOG.error( "Unable to find element: '" + ELEMENT_FORM + "'." ); //$NON-NLS-1$//$NON-NLS-2$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindElement" ), ELEMENT_FORM ) ); //$NON-NLS-1$
        }

        // Creating the form and setting it to the template
        TemplateForm form = new TemplateForm();
        template.setForm( form );

        // Reading the child elements
        for ( Iterator<?> i = formElement.elementIterator(); i.hasNext(); )
        {
            Element childElement = ( Element ) i.next();

            // Getting the name of the element
            String elementName = childElement.getName();
            if ( elementName.equalsIgnoreCase( ELEMENT_COMPOSITE ) )
            {
                readComposite( childElement, form );
            }
            else if ( elementName.equalsIgnoreCase( ELEMENT_SECTION ) )
            {
                readSection( childElement, form );
            }
            else
            {
                throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                    + NLS.bind( Messages.getString( "TemplateIO.ElementNotAllowedAtThisLevel" ), //$NON-NLS-1$
                        new String[]
                            { elementName, ELEMENT_SECTION, ELEMENT_COMPOSITE } ) );
            }

        }

        // Verifying if we've found at least one section
        if ( form.getChildren().size() == 0 )
        {
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindAnyXOrYElement" ), new String[] //$NON-NLS-1$
                    { ELEMENT_SECTION, ELEMENT_COMPOSITE } ) );
        }
    }


    /**
     * Reads a widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readWidget( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        // Getting the name of the element
        String elementName = element.getName();

        // Switching on the various widgets we support
        if ( elementName.equalsIgnoreCase( ELEMENT_CHECKBOX ) )
        {
            readCheckbox( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_COMPOSITE ) )
        {
            readComposite( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_DATE ) )
        {
            readDate( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_FILECHOOSER ) )
        {
            readFileChooser( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_IMAGE ) )
        {
            readImage( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_LABEL ) )
        {
            readLabel( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_LINK ) )
        {
            readLink( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_LISTBOX ) )
        {
            readListbox( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_PASSWORD ) )
        {
            readPassword( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_RADIOBUTTONS ) )
        {
            readRadioButtons( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_SECTION ) )
        {
            readSection( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_SPINNER ) )
        {
            readSpinner( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_TABLE ) )
        {
            readTable( element, parent );
        }
        else if ( elementName.equalsIgnoreCase( ELEMENT_TEXTFIELD ) )
        {
            readTextfield( element, parent );
        }
        // We could not find a widget associated with this name.
        else
        {
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnknownWidget" ), elementName ) ); //$NON-NLS-1$
        }
    }


    /**
     * Reads the widget common properties like 'attributeType' and all position related properties.
     *
     * @param element
     *      the element to read from
     * @param widget
     *      the associated widget
     * @param throwExceptionIfMissingAttributeType
     *      <code>true</code> if the method should throw an exception if the 'attributeType' value is missing,
     *      <code>false</code> if not
     * @param widgetElementName
     *      the name of the XML element of the widget (for debug purposes)
     * @throws TemplateIOException
     *      if the mandatory 'attributeType' value is missing
     */
    private static void readWidgetCommonProperties( Element element, TemplateWidget widget,
        boolean throwExceptionIfMissingAttributeType, String widgetElementName ) throws TemplateIOException
    {
        // Reading the 'attributeType' attribute
        boolean foundAttributeTypeAttribute = readAttributeTypeAttribute( element, widget );
        // If the 'attributeType' attribute does not exist, we throw an 
        // exception
        if ( throwExceptionIfMissingAttributeType && !foundAttributeTypeAttribute )
        {
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindMandatoryAttribute" ), new String[] //$NON-NLS-1$
                    { ATTRIBUTE_ATTRIBUTETYPE, widgetElementName } ) );
        }

        // Reading the 'horizontalAlignment' attribute
        Attribute horizontalAlignmentAttribute = element.attribute( ATTRIBUTE_HORIZONTAL_ALIGNMENT );
        if ( ( horizontalAlignmentAttribute != null ) && ( horizontalAlignmentAttribute.getText() != null ) )
        {
            widget.setHorizontalAlignment( readWidgetAlignmentValue( horizontalAlignmentAttribute.getText() ) );
        }

        // Reading the 'verticalAlignment' attribute
        Attribute verticalAlignmentAttribute = element.attribute( ATTRIBUTE_VERTICAL_ALIGNMENT );
        if ( ( verticalAlignmentAttribute != null ) && ( verticalAlignmentAttribute.getText() != null ) )
        {
            widget.setVerticalAlignment( readWidgetAlignmentValue( verticalAlignmentAttribute.getText() ) );
        }

        // Reading the 'grabExcessHorizontalSpace' attribute
        Attribute grabExcessHorizontalSpaceAttribute = element.attribute( ATTRIBUTE_GRAB_EXCESS_HORIZONTAL_SPACE );
        if ( ( grabExcessHorizontalSpaceAttribute != null ) && ( grabExcessHorizontalSpaceAttribute.getText() != null ) )
        {
            widget.setGrabExcessHorizontalSpace( readBoolean( grabExcessHorizontalSpaceAttribute.getText() ) );
        }

        // Reading the 'grabExcessVerticalSpace' attribute
        Attribute grabExcessVerticalSpaceAttribute = element.attribute( ATTRIBUTE_GRAB_EXCESS_VERTICAL_SPACE );
        if ( ( grabExcessVerticalSpaceAttribute != null ) && ( grabExcessVerticalSpaceAttribute.getText() != null ) )
        {
            widget.setGrabExcessVerticalSpace( readBoolean( grabExcessVerticalSpaceAttribute.getText() ) );
        }

        // Reading the 'horizontalSpan' attribute
        Attribute horizontalSpanAttribute = element.attribute( ATTRIBUTE_HORIZONTAL_SPAN );
        if ( ( horizontalSpanAttribute != null ) && ( horizontalSpanAttribute.getText() != null ) )
        {
            widget.setHorizontalSpan( readInteger( horizontalSpanAttribute.getText() ) );
        }

        // Reading the 'verticalSpan' attribute
        Attribute verticalSpanAttribute = element.attribute( ATTRIBUTE_VERTICAL_SPAN );
        if ( ( verticalSpanAttribute != null ) && ( verticalSpanAttribute.getText() != null ) )
        {
            widget.setVerticalSpan( readInteger( verticalSpanAttribute.getText() ) );
        }

        // Reading the 'width' attribute
        Attribute widthAttribute = element.attribute( ATTRIBUTE_WIDTH );
        if ( ( widthAttribute != null ) && ( widthAttribute.getText() != null ) )
        {
            widget.setImageWidth( readInteger( widthAttribute.getText() ) );
        }

        // Reading the 'height' attribute
        Attribute heightAttribute = element.attribute( ATTRIBUTE_HEIGHT );
        if ( ( heightAttribute != null ) && ( heightAttribute.getText() != null ) )
        {
            widget.setImageHeight( readInteger( heightAttribute.getText() ) );
        }
    }


    /**
     * Reads a widget alignment value.
     *
     * @param text
     *      the widget alignment value
     */
    private static WidgetAlignment readWidgetAlignmentValue( String text ) throws TemplateIOException
    {
        if ( text.equalsIgnoreCase( VALUE_NONE ) )
        {
            return WidgetAlignment.NONE;
        }
        else if ( text.equalsIgnoreCase( VALUE_BEGINNING ) )
        {
            return WidgetAlignment.BEGINNING;
        }
        else if ( text.equalsIgnoreCase( VALUE_CENTER ) )
        {
            return WidgetAlignment.CENTER;
        }
        else if ( text.equalsIgnoreCase( VALUE_END ) )
        {
            return WidgetAlignment.END;
        }
        else if ( text.equalsIgnoreCase( VALUE_FILL ) )
        {
            return WidgetAlignment.FILL;
        }
        else
        {
            String message = NLS.bind( Messages.getString( "TemplateIO.UnableToConvertStringToWidgetAlignmentValue" ), //$NON-NLS-1$
                new String[]
                    { VALUE_NONE, VALUE_BEGINNING, VALUE_CENTER, VALUE_END, VALUE_FILL, text } );
            LOG.error( message );
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" + message ); //$NON-NLS-1$
        }
    }


    /**
     * Reads the attribute type attribute.
     *
     * @param element
     *      the element to read from
     * @param widget
     *      the associated widget
     * @return
     *      <code>true</code> if the attribute type attribute has been found,
     *      <code>false</code> if not
     */
    private static boolean readAttributeTypeAttribute( Element element, TemplateWidget widget )
    {
        // Reading the 'attributeType' attribute
        Attribute attributeTypeAttribute = element.attribute( ATTRIBUTE_ATTRIBUTETYPE );
        if ( ( attributeTypeAttribute != null ) && ( attributeTypeAttribute.getText() != null ) )
        {
            widget.setAttributeType( attributeTypeAttribute.getText() );
            return true;
        }

        return false;
    }


    /**
     * Reads a checkbox widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readCheckbox( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template checkbox" ); //$NON-NLS-1$

        // Creating the checkbox
        TemplateCheckbox templateCheckbox = new TemplateCheckbox( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, templateCheckbox, true, ELEMENT_CHECKBOX );

        // Reading the 'label' attribute
        Attribute labelAttribute = element.attribute( ATTRIBUTE_LABEL );
        if ( ( labelAttribute != null ) && ( labelAttribute.getText() != null ) )
        {
            templateCheckbox.setLabel( labelAttribute.getText() );
        }

        // Reading the 'enabled' attribute
        Attribute enabledAttribute = element.attribute( ATTRIBUTE_ENABLED );
        if ( ( enabledAttribute != null ) && ( enabledAttribute.getText() != null ) )
        {
            templateCheckbox.setEnabled( readBoolean( enabledAttribute.getText() ) );
        }

        // Reading the 'checkedValue' element
        Element checkedValueElement = element.element( ELEMENT_CHECKEDVALUE );
        if ( checkedValueElement != null )
        {
            templateCheckbox.setCheckedValue( checkedValueElement.getText() );
        }

        // Reading the 'uncheckedValue' element
        Element uncheckedValueElement = element.element( ELEMENT_UNCHECKEDVALUE );
        if ( uncheckedValueElement != null )
        {
            templateCheckbox.setUncheckedValue( uncheckedValueElement.getText() );
        }
    }


    /**
     * Reads a composite widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readComposite( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template composite" ); //$NON-NLS-1$

        // Creating the composite
        TemplateComposite templateComposite = new TemplateComposite( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, templateComposite, false, ELEMENT_COMPOSITE );

        // Reading the 'numberOfColumns' attribute
        Attribute numberOfColumnsAttribute = element.attribute( ATTRIBUTE_NUMBEROFCOLUMNS );
        if ( ( numberOfColumnsAttribute != null ) && ( numberOfColumnsAttribute.getText() != null ) )
        {
            templateComposite.setNumberOfColumns( readInteger( numberOfColumnsAttribute.getText() ) );
        }

        // Reading the 'equalColumns' attribute
        Attribute equalColumnsAttribute = element.attribute( ATTRIBUTE_EQUALCOLUMNS );
        if ( ( equalColumnsAttribute != null ) && ( equalColumnsAttribute.getText() != null ) )
        {
            templateComposite.setEqualColumns( readBoolean( equalColumnsAttribute.getText() ) );
        }

        // Reading the elements
        for ( Iterator<?> i = element.elementIterator(); i.hasNext(); )
        {
            Element childElement = ( Element ) i.next();
            readWidget( childElement, templateComposite );
        }
    }


    /**
     * Reads a date widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readDate( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template date" ); //$NON-NLS-1$

        // Creating the file chooser
        TemplateDate date = new TemplateDate( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, date, true, ELEMENT_DATE );

        // Reading the 'format' attribute
        Attribute formatAttribute = element.attribute( ATTRIBUTE_FORMAT );
        if ( ( formatAttribute != null ) && ( formatAttribute.getText() != null ) )
        {
            date.setFormat( formatAttribute.getText() );
        }

        // Reading the 'showEditButton' attribute
        Attribute showEditButtonAttribute = element.attribute( ATTRIBUTE_SHOWEDITBUTTON );
        if ( ( showEditButtonAttribute != null ) && ( showEditButtonAttribute.getText() != null ) )
        {
            date.setShowEditButton( readBoolean( showEditButtonAttribute.getText() ) );
        }
    }


    /**
     * Reads a file chooser widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readFileChooser( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template file chooser" ); //$NON-NLS-1$

        // Creating the file chooser
        TemplateFileChooser fileChooser = new TemplateFileChooser( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, fileChooser, true, ELEMENT_FILECHOOSER );

        // Reading the 'extensions' attribute
        Attribute extensionsAttribute = element.attribute( ATTRIBUTE_EXTENSIONS );
        if ( ( extensionsAttribute != null ) && ( extensionsAttribute.getText() != null ) )
        {
            readFileChooserExtensions( extensionsAttribute, fileChooser );
        }

        // Reading the 'showIcon' attribute
        Attribute showIconAttribute = element.attribute( ATTRIBUTE_SHOWICON );
        if ( ( showIconAttribute != null ) && ( showIconAttribute.getText() != null ) )
        {
            fileChooser.setShowIcon( readBoolean( showIconAttribute.getText() ) );
        }

        // Reading the 'showSaveAsButton' attribute
        Attribute showSaveAsButtonAttribute = element.attribute( ATTRIBUTE_SHOWSAVEASBUTTON );
        if ( ( showSaveAsButtonAttribute != null ) && ( showSaveAsButtonAttribute.getText() != null ) )
        {
            fileChooser.setShowSaveAsButton( readBoolean( showSaveAsButtonAttribute.getText() ) );
        }

        // Reading the 'showClearButton' attribute
        Attribute showClearButtonAttribute = element.attribute( ATTRIBUTE_SHOWCLEARBUTTON );
        if ( ( showClearButtonAttribute != null ) && ( showClearButtonAttribute.getText() != null ) )
        {
            fileChooser.setShowClearButton( readBoolean( showClearButtonAttribute.getText() ) );
        }

        // Reading the 'showBrowseButton' attribute
        Attribute showBrowseButtonAttribute = element.attribute( ATTRIBUTE_SHOWBROWSEBUTTON );
        if ( ( showBrowseButtonAttribute != null ) && ( showBrowseButtonAttribute.getText() != null ) )
        {
            fileChooser.setShowBrowseButton( readBoolean( showBrowseButtonAttribute.getText() ) );
        }

        // Reading the 'icon' element
        Element iconElement = element.element( ELEMENT_ICON );
        if ( iconElement != null )
        {
            fileChooser.setIcon( iconElement.getText() );
        }
    }


    /**
     * Reads the extensions of a file chooser widget.
     *
     * @param extensionsAttribute
     *      the attribute
     * @param fileChooser
     *      the file chooser
     */
    private static void readFileChooserExtensions( Attribute extensionsAttribute, TemplateFileChooser fileChooser )
    {
        // Getting the extensions
        String extensions = extensionsAttribute.getText();

        // Splitting and setting extensions to the file chooser
        for ( String extension : extensions.split( "," ) ) //$NON-NLS-1$
        {
            fileChooser.addExtension( extension );
        }
    }


    /**
     * Reads an image widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readImage( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template image" ); //$NON-NLS-1$

        // Creating the image
        TemplateImage image = new TemplateImage( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, image, false, ELEMENT_IMAGE );

        // Reading the 'showSaveAsButton' attribute
        Attribute showSaveAsButtonAttribute = element.attribute( ATTRIBUTE_SHOWSAVEASBUTTON );
        if ( ( showSaveAsButtonAttribute != null ) && ( showSaveAsButtonAttribute.getText() != null ) )
        {
            image.setShowSaveAsButton( readBoolean( showSaveAsButtonAttribute.getText() ) );
        }

        // Reading the 'showClearButton' attribute
        Attribute showClearButtonAttribute = element.attribute( ATTRIBUTE_SHOWCLEARBUTTON );
        if ( ( showClearButtonAttribute != null ) && ( showClearButtonAttribute.getText() != null ) )
        {
            image.setShowClearButton( readBoolean( showClearButtonAttribute.getText() ) );
        }

        // Reading the 'showBrowseButton' attribute
        Attribute showBrowseButtonAttribute = element.attribute( ATTRIBUTE_SHOWBROWSEBUTTON );
        if ( ( showBrowseButtonAttribute != null ) && ( showBrowseButtonAttribute.getText() != null ) )
        {
            image.setShowBrowseButton( readBoolean( showBrowseButtonAttribute.getText() ) );
        }

        // Reading the 'imageWidth' attribute
        Attribute imageWidthAttribute = element.attribute( ATTRIBUTE_IMAGE_WIDTH );
        if ( ( imageWidthAttribute != null ) && ( imageWidthAttribute.getText() != null ) )
        {
            image.setImageWidth( readInteger( imageWidthAttribute.getText() ) );
        }

        // Reading the 'imageHeight' attribute
        Attribute imageHeightAttribute = element.attribute( ATTRIBUTE_IMAGE_HEIGHT );
        if ( ( imageHeightAttribute != null ) && ( imageHeightAttribute.getText() != null ) )
        {
            image.setImageHeight( readInteger( imageHeightAttribute.getText() ) );
        }

        // Reading the 'data' element
        Element imageDataElement = element.element( ELEMENT_DATA );
        if ( imageDataElement != null )
        {
            image.setImageData( imageDataElement.getText() );
        }
    }


    /**
     * Reads a label widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readLabel( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template label" ); //$NON-NLS-1$

        // Creating the label
        TemplateLabel label = new TemplateLabel( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, label, false, ELEMENT_LABEL );

        // Reading the 'value' attribute
        Attribute valueAttribute = element.attribute( ATTRIBUTE_VALUE );
        if ( ( valueAttribute != null ) && ( valueAttribute.getText() != null ) )
        {
            label.setValue( valueAttribute.getText() );
        }

        // Reading the 'numberOfRows' attribute
        Attribute numberOfRowsAttribute = element.attribute( ATTRIBUTE_NUMBEROFROWS );
        if ( ( numberOfRowsAttribute != null ) && ( numberOfRowsAttribute.getText() != null ) )
        {
            label.setNumberOfRows( readInteger( numberOfRowsAttribute.getText() ) );
        }

        // Reading the 'dollarSignIsNewLine' attribute
        Attribute dollarSignIsNewLineAttribute = element.attribute( ATTRIBUTE_DOLLAR_SIGN_IS_NEW_LINE );
        if ( ( dollarSignIsNewLineAttribute != null ) && ( dollarSignIsNewLineAttribute.getText() != null ) )
        {
            label.setDollarSignIsNewLine( readBoolean( dollarSignIsNewLineAttribute.getText() ) );
        }
    }


    /**
     * Reads a link widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readLink( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template link" ); //$NON-NLS-1$

        // Creating the link
        TemplateLink link = new TemplateLink( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, link, false, ELEMENT_LINK );

        // Reading the 'value' attribute
        Attribute valueAttribute = element.attribute( ATTRIBUTE_VALUE );
        if ( ( valueAttribute != null ) && ( valueAttribute.getText() != null ) )
        {
            link.setValue( valueAttribute.getText() );
        }
    }


    /**
     * Reads a listbox widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readListbox( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template listbox" ); //$NON-NLS-1$

        // Creating the listbox
        TemplateListbox listbox = new TemplateListbox( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, listbox, true, ELEMENT_LISTBOX );

        // Reading the 'multipleSelection' attribute
        Attribute multipleSelectionAttribute = element.attribute( ATTRIBUTE_MULTIPLESELECTION );
        if ( ( multipleSelectionAttribute != null ) && ( multipleSelectionAttribute.getText() != null ) )
        {
            listbox.setMultipleSelection( readBoolean( multipleSelectionAttribute.getText() ) );
        }

        // Reading the 'enabled' attribute
        Attribute enabledAttribute = element.attribute( ATTRIBUTE_ENABLED );
        if ( ( enabledAttribute != null ) && ( enabledAttribute.getText() != null ) )
        {
            listbox.setEnabled( readBoolean( enabledAttribute.getText() ) );
        }

        // Reading the 'items' element
        Element itemsElement = element.element( ELEMENT_ITEMS );
        if ( itemsElement != null )
        {
            // Reading the 'item' elements
            for ( Iterator<?> i = itemsElement.elementIterator( ELEMENT_ITEM ); i.hasNext(); )
            {
                Element itemElement = ( Element ) i.next();
                listbox.addValue( readValueItem( itemElement ) );
            }

            // Verifying if at least one button has been read
            if ( listbox.getItems().size() == 0 )
            {
                throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                    + NLS.bind( Messages.getString( "TemplateIO.UnableToFindAnyElement" ), ELEMENT_ITEM ) ); //$NON-NLS-1$
            }
        }
        else
        {
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindElement" ), ELEMENT_ITEMS ) ); //$NON-NLS-1$
        }
    }


    /**
     * Reads the value item.
     *
     * @param extensionsAttribute
     *      the attribute
     * @param fileChooser
     *      the file chooser
     */
    private static ValueItem readValueItem( Element element )
    {
        ValueItem valueItem = new ValueItem();

        // Reading the 'label' element
        Element labelElement = element.element( ELEMENT_LABEL );
        if ( ( labelElement != null ) && ( labelElement.getText() != null ) )
        {
            valueItem.setLabel( labelElement.getText() );
        }

        // Reading the 'value' element
        Element valueElement = element.element( ELEMENT_VALUE );
        if ( ( valueElement != null ) && ( valueElement.getText() != null ) )
        {
            valueItem.setValue( valueElement.getText() );
        }

        return valueItem;
    }


    /**
     * Reads a section widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readSection( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template section" ); //$NON-NLS-1$

        // Creating the section
        TemplateSection templateSection = new TemplateSection( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, templateSection, false, ELEMENT_SECTION );

        // Reading the 'title' attribute
        Attribute titleAttribute = element.attribute( ATTRIBUTE_TITLE );
        if ( ( titleAttribute != null ) && ( titleAttribute.getText() != null ) )
        {
            templateSection.setTitle( titleAttribute.getText() );
        }

        // Reading the 'description' attribute
        Attribute descriptionAttribute = element.attribute( ATTRIBUTE_DESCRIPTION );
        if ( ( descriptionAttribute != null ) && ( descriptionAttribute.getText() != null ) )
        {
            templateSection.setDescription( descriptionAttribute.getText() );
        }

        // Reading the 'numberOfColumns' attribute
        Attribute numberOfColumnsAttribute = element.attribute( ATTRIBUTE_NUMBEROFCOLUMNS );
        if ( ( numberOfColumnsAttribute != null ) && ( numberOfColumnsAttribute.getText() != null ) )
        {
            templateSection.setNumberOfColumns( readInteger( numberOfColumnsAttribute.getText() ) );
        }

        // Reading the 'equalColumns' attribute
        Attribute equalColumnsAttribute = element.attribute( ATTRIBUTE_EQUALCOLUMNS );
        if ( ( equalColumnsAttribute != null ) && ( equalColumnsAttribute.getText() != null ) )
        {
            templateSection.setEqualColumns( readBoolean( equalColumnsAttribute.getText() ) );
        }

        // Reading the 'expandable' attribute
        Attribute expandableAttribute = element.attribute( ATTRIBUTE_EXPANDABLE );
        if ( ( expandableAttribute != null ) && ( expandableAttribute.getText() != null ) )
        {
            templateSection.setExpandable( readBoolean( expandableAttribute.getText() ) );
        }

        // Reading the 'expanded' attribute
        Attribute expandedAttribute = element.attribute( ATTRIBUTE_EXPANDED );
        if ( ( expandedAttribute != null ) && ( expandedAttribute.getText() != null ) )
        {
            templateSection.setExpanded( readBoolean( expandedAttribute.getText() ) );
        }

        // Reading the elements
        for ( Iterator<?> i = element.elementIterator(); i.hasNext(); )
        {
            Element childElement = ( Element ) i.next();
            readWidget( childElement, templateSection );
        }
    }


    /**
     * Reads a password widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readPassword( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template password" ); //$NON-NLS-1$

        // Creating the password
        TemplatePassword password = new TemplatePassword( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, password, true, ELEMENT_PASSWORD );

        // Reading the 'hidden' attribute
        Attribute hiddenAttribute = element.attribute( ATTRIBUTE_HIDDEN );
        if ( ( hiddenAttribute != null ) && ( hiddenAttribute.getText() != null ) )
        {
            password.setHidden( readBoolean( hiddenAttribute.getText() ) );
        }

        // Reading the 'showEditButton' attribute
        Attribute showEditButtonAttribute = element.attribute( ATTRIBUTE_SHOWEDITBUTTON );
        if ( ( showEditButtonAttribute != null ) && ( showEditButtonAttribute.getText() != null ) )
        {
            password.setShowEditButton( readBoolean( showEditButtonAttribute.getText() ) );
        }

        // Reading the 'showShowPasswordCheckbox' attribute
        Attribute showShowPasswordCheckboxAttribute = element.attribute( ATTRIBUTE_SHOWSHOWPASSWORDCHECKBOX );
        if ( ( showShowPasswordCheckboxAttribute != null ) && ( showShowPasswordCheckboxAttribute.getText() != null ) )
        {
            password.setShowShowPasswordCheckbox( readBoolean( showShowPasswordCheckboxAttribute.getText() ) );
        }
    }


    /**
     * Reads a radio buttons widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readRadioButtons( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template radio buttons" ); //$NON-NLS-1$

        // Creating the radioButtons
        TemplateRadioButtons radioButtons = new TemplateRadioButtons( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, radioButtons, true, ELEMENT_RADIOBUTTONS );

        // Reading the 'enabled' attribute
        Attribute enabledAttribute = element.attribute( ATTRIBUTE_ENABLED );
        if ( ( enabledAttribute != null ) && ( enabledAttribute.getText() != null ) )
        {
            radioButtons.setEnabled( readBoolean( enabledAttribute.getText() ) );
        }

        // Reading the 'buttons' element
        Element buttonsElement = element.element( ELEMENT_BUTTONS );
        if ( buttonsElement != null )
        {
            // Reading the 'button' elements
            for ( Iterator<?> i = buttonsElement.elementIterator( ELEMENT_BUTTON ); i.hasNext(); )
            {
                Element buttonElement = ( Element ) i.next();
                radioButtons.addButton( readValueItem( buttonElement ) );
            }

            // Verifying if at least one button has been read
            if ( radioButtons.getButtons().size() == 0 )
            {
                throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                    + NLS.bind( Messages.getString( "TemplateIO.UnableToFindAnyElement" ), ELEMENT_BUTTON ) ); //$NON-NLS-1$
            }
        }
        else
        {
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToFindElement" ), ELEMENT_BUTTONS ) ); //$NON-NLS-1$
        }
    }


    /**
     * Reads a spinner widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readSpinner( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template spinner" ); //$NON-NLS-1$

        // Creating the spinner
        TemplateSpinner spinner = new TemplateSpinner( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, spinner, true, ELEMENT_SPINNER );

        // Reading the 'minimum' attribute
        Attribute minimumAttribute = element.attribute( ATTRIBUTE_MINIMUM );
        if ( ( minimumAttribute != null ) && ( minimumAttribute.getText() != null ) )
        {
            spinner.setMinimum( readInteger( minimumAttribute.getText() ) );
        }

        // Reading the 'maximum' attribute
        Attribute maximumAttribute = element.attribute( ATTRIBUTE_MAXIMUM );
        if ( ( maximumAttribute != null ) && ( maximumAttribute.getText() != null ) )
        {
            spinner.setMaximum( readInteger( maximumAttribute.getText() ) );
        }

        // Reading the 'increment' attribute
        Attribute incrementAttribute = element.attribute( ATTRIBUTE_INCREMENT );
        if ( ( incrementAttribute != null ) && ( incrementAttribute.getText() != null ) )
        {
            spinner.setIncrement( readInteger( incrementAttribute.getText() ) );
        }

        // Reading the 'pageIncrement' attribute
        Attribute pageIncrementAttribute = element.attribute( ATTRIBUTE_PAGEINCREMENT );
        if ( ( pageIncrementAttribute != null ) && ( pageIncrementAttribute.getText() != null ) )
        {
            spinner.setPageIncrement( readInteger( pageIncrementAttribute.getText() ) );
        }

        // Reading the 'digits' attribute
        Attribute digitsAttribute = element.attribute( ATTRIBUTE_DIGITS );
        if ( ( digitsAttribute != null ) && ( digitsAttribute.getText() != null ) )
        {
            spinner.setDigits( readInteger( digitsAttribute.getText() ) );
        }
    }


    /**
     * Reads a table widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readTable( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template table" ); //$NON-NLS-1$

        // Creating the table
        TemplateTable table = new TemplateTable( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, table, true, ELEMENT_TABLE );

        // Reading the 'showAddButton' attribute
        Attribute showAddButtonAttribute = element.attribute( ATTRIBUTE_SHOWADDBUTTON );
        if ( ( showAddButtonAttribute != null ) && ( showAddButtonAttribute.getText() != null ) )
        {
            table.setShowAddButton( readBoolean( showAddButtonAttribute.getText() ) );
        }

        // Reading the 'showEditButton' attribute
        Attribute showEditButtonAttribute = element.attribute( ATTRIBUTE_SHOWEDITBUTTON );
        if ( ( showEditButtonAttribute != null ) && ( showEditButtonAttribute.getText() != null ) )
        {
            table.setShowEditButton( readBoolean( showEditButtonAttribute.getText() ) );
        }

        // Reading the 'showDeleteButton' attribute
        Attribute showDeleteButtonAttribute = element.attribute( ATTRIBUTE_SHOWDELETEBUTTON );
        if ( ( showDeleteButtonAttribute != null ) && ( showDeleteButtonAttribute.getText() != null ) )
        {
            table.setShowDeleteButton( readBoolean( showDeleteButtonAttribute.getText() ) );
        }
    }


    /**
     * Reads a text field widget.
     *
     * @param element
     *      the element
     * @param template
     *      the template
     * @throws TemplateIOException
     */
    private static void readTextfield( Element element, TemplateWidget parent ) throws TemplateIOException
    {
        LOG.debug( "Reading a template textfield" ); //$NON-NLS-1$

        // Creating the text field
        TemplateTextField textField = new TemplateTextField( parent );

        // Reading the widget's common properties
        readWidgetCommonProperties( element, textField, true, ELEMENT_TEXTFIELD );

        // Reading the 'numberOfRows' attribute
        Attribute numberOfRowsAttribute = element.attribute( ATTRIBUTE_NUMBEROFROWS );
        if ( ( numberOfRowsAttribute != null ) && ( numberOfRowsAttribute.getText() != null ) )
        {
            textField.setNumberOfRows( readInteger( numberOfRowsAttribute.getText() ) );
        }

        // Reading the 'charactersLimit' attribute
        Attribute charactersLimitAttribute = element.attribute( ATTRIBUTE_CHARACTERSLIMIT );
        if ( ( charactersLimitAttribute != null ) && ( charactersLimitAttribute.getText() != null ) )
        {
            textField.setCharactersLimit( readInteger( charactersLimitAttribute.getText() ) );
        }

        // Reading the 'dollarSignIsNewLine' attribute
        Attribute dollarSignIsNewLineAttribute = element.attribute( ATTRIBUTE_DOLLAR_SIGN_IS_NEW_LINE );
        if ( ( dollarSignIsNewLineAttribute != null ) && ( dollarSignIsNewLineAttribute.getText() != null ) )
        {
            textField.setDollarSignIsNewLine( readBoolean( dollarSignIsNewLineAttribute.getText() ) );
        }
    }


    /**
     * Reads a boolean.
     *
     * @param text
     *      the boolean as a string
     */
    private static boolean readBoolean( String text ) throws TemplateIOException
    {
        if ( text.equalsIgnoreCase( VALUE_TRUE ) )
        {
            return true;
        }
        else if ( text.equalsIgnoreCase( VALUE_FALSE ) )
        {
            return false;
        }
        else
        {
            LOG.error( "Unable to convert this string to a boolean ('" + VALUE_TRUE + "' or '" + VALUE_FALSE + "'): '" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + text + "'." ); //$NON-NLS-1$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToConvertStringToBoolean" ), new String[] //$NON-NLS-1$
                    { VALUE_TRUE, VALUE_FALSE, text } ) );
        }
    }


    /**
     * Reads an integer.
     *
     * @param text
     *      the integer as a string
     */
    private static int readInteger( String text ) throws TemplateIOException
    {
        try
        {
            return Integer.parseInt( text );
        }
        catch ( NumberFormatException e )
        {
            LOG.error( "Unable to convert this string to an integer: '" + text + "'." ); //$NON-NLS-1$ //$NON-NLS-2$
            throw new TemplateIOException( THE_FILE_DOES_NOT_SEEM_TO_BE_A_VALID_TEMPLATE_FILE + "\n" //$NON-NLS-1$
                + NLS.bind( Messages.getString( "TemplateIO.UnableToConvertStringToInteger" ), text ) ); //$NON-NLS-1$
        }
    }


    /**
     * Saves the template using the writer.
     *
     * @param template
     *      the connections
     * @param stream
     *      the OutputStream
     * @throws IOException
     *      if an I/O error occurs
     */
    public static void save( Template template, OutputStream stream ) throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        writeTemplate( document, template );

        // Writing the file to disk
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$
        XMLWriter writer = new XMLWriter( stream, outformat );
        writer.write( document );
        writer.flush();
        writer.close();
    }


    /**
     * Writes the template.
     *
     * @param document
     *      the document to write into
     * @param template
     *      the template
     */
    private static void writeTemplate( Document document, Template template )
    {
        // Creating the root element
        Element rootElement = document.addElement( ELEMENT_TEMPLATE );

        // Writing the ID
        rootElement.addAttribute( ATTRIBUTE_ID, template.getId() );

        // Writing the title
        rootElement.addAttribute( ATTRIBUTE_TITLE, template.getTitle() );

        // Writing the object classes
        writeObjectClasses( rootElement, template );

        // Writing the form
        writeForm( rootElement, template );
    }


    /**
     * Writes the object classes.
     *
     * @param element
     *      the parent element
     * @param template
     *      the template
     */
    private static void writeObjectClasses( Element element, Template template )
    {
        // Creating the 'objectClasses' element
        Element objectClassesElement = element.addElement( ELEMENT_OBJECTCLASSES );

        // Creating the 'structural' element
        objectClassesElement.addElement( ELEMENT_STRUCTURAL ).setText( template.getStructuralObjectClass() );

        List<String> auxiliaryObjectClasses = template.getAuxiliaryObjectClasses();
        if ( ( auxiliaryObjectClasses != null ) & ( auxiliaryObjectClasses.size() > 0 ) )
        {
            // Creating the 'auxiliaries' element
            Element auxiliariesElement = objectClassesElement.addElement( ELEMENT_AUXILIARIES );

            // Creating each 'auxiliary' element
            for ( String auxliaryObjectClass : template.getAuxiliaryObjectClasses() )
            {
                auxiliariesElement.addElement( ELEMENT_AUXILIARY ).setText( auxliaryObjectClass );
            }
        }
    }


    /**
     * Writes the form.
     *
     * @param element
     *      the parent element
     * @param template
     *      the template
     */
    private static void writeForm( Element element, Template template )
    {
        // Creating the 'form' element
        Element formElement = element.addElement( ELEMENT_FORM );

        // Getting the form
        TemplateForm form = template.getForm();

        // Creating each child element
        for ( TemplateWidget widget : form.getChildren() )
        {
            writeWidget( formElement, widget );
        }
    }


    /**
     * Writes a widget.
     *
     * @param element
     *      the parent element
     * @param widget
     *      the widget
     */
    private static void writeWidget( Element element, TemplateWidget widget )
    {
        // Switching on the various widgets we support
        if ( widget instanceof TemplateCheckbox )
        {
            writeCheckbox( element, ( TemplateCheckbox ) widget );
        }
        else if ( widget instanceof TemplateComposite )
        {
            writeComposite( element, ( TemplateComposite ) widget );
        }
        else if ( widget instanceof TemplateDate )
        {
            writeDate( element, ( TemplateDate ) widget );
        }
        else if ( widget instanceof TemplateFileChooser )
        {
            writeFileChooser( element, ( TemplateFileChooser ) widget );
        }
        else if ( widget instanceof TemplateImage )
        {
            writeImage( element, ( TemplateImage ) widget );
        }
        else if ( widget instanceof TemplateLabel )
        {
            writeLabel( element, ( TemplateLabel ) widget );
        }
        else if ( widget instanceof TemplateLink )
        {
            writeLink( element, ( TemplateLink ) widget );
        }
        else if ( widget instanceof TemplateListbox )
        {
            writeListbox( element, ( TemplateListbox ) widget );
        }
        else if ( widget instanceof TemplateSection )
        {
            writeSection( element, ( TemplateSection ) widget );
        }
        else if ( widget instanceof TemplatePassword )
        {
            writePassword( element, ( TemplatePassword ) widget );
        }
        else if ( widget instanceof TemplateRadioButtons )
        {
            writeRadioButtons( element, ( TemplateRadioButtons ) widget );
        }
        else if ( widget instanceof TemplateSpinner )
        {
            writeSpinner( element, ( TemplateSpinner ) widget );
        }
        else if ( widget instanceof TemplateTable )
        {
            writeTable( element, ( TemplateTable ) widget );
        }
        else if ( widget instanceof TemplateTextField )
        {
            writeTextfield( element, ( TemplateTextField ) widget );
        }
        // We could not find a correct widget type.
        else
        {
            // Unsupported widget type
        }
    }


    /**
     * Writes the widget's common properties like 'attributeType' and all position related properties.
     *
     * @param element
     *      the element
     * @param widget
     *      the widget
     */
    private static void writeWidgetCommonProperties( Element element, TemplateWidget widget )
    {
        // Creating the 'attributeType' attribute
        writeAttributeTypeAttribute( element, widget );

        // Creating the 'horizontalAlignment' attribute
        if ( widget.getHorizontalAlignment() != TemplateWidget.DEFAULT_HORIZONTAL_ALIGNMENT )
        {
            element.addAttribute( ATTRIBUTE_HORIZONTAL_ALIGNMENT, getWidgetAlignmentValue( widget
                .getHorizontalAlignment() ) );
        }

        // Creating the 'verticalAlignment' attribute
        if ( widget.getVerticalAlignment() != TemplateWidget.DEFAULT_VERTICAL_ALIGNMENT )
        {
            element
                .addAttribute( ATTRIBUTE_VERTICAL_ALIGNMENT, getWidgetAlignmentValue( widget.getVerticalAlignment() ) );
        }

        // Creating the 'grabExcessHorizontalSpace' attribute
        if ( widget.isGrabExcessHorizontalSpace() != TemplateWidget.DEFAULT_GRAB_EXCESS_HORIZONTAL_SPACE )
        {
            element.addAttribute( ATTRIBUTE_GRAB_EXCESS_HORIZONTAL_SPACE, "" + widget.isGrabExcessHorizontalSpace() ); //$NON-NLS-1$
        }

        // Creating the 'grabExcessVerticalSpace' attribute
        if ( widget.isGrabExcessVerticalSpace() != TemplateWidget.DEFAULT_GRAB_EXCESS_VERTICAL_SPACE )
        {
            element.addAttribute( ATTRIBUTE_GRAB_EXCESS_VERTICAL_SPACE, "" + widget.isGrabExcessVerticalSpace() ); //$NON-NLS-1$
        }

        // Creating the 'horizontalSpan' attribute
        if ( widget.getHorizontalSpan() != TemplateWidget.DEFAULT_HORIZONTAL_SPAN )
        {
            element.addAttribute( ATTRIBUTE_HORIZONTAL_SPAN, "" + widget.getHorizontalSpan() ); //$NON-NLS-1$
        }

        // Creating the 'verticalSpan' attribute
        if ( widget.getVerticalSpan() != TemplateWidget.DEFAULT_VERTICAL_SPAN )
        {
            element.addAttribute( ATTRIBUTE_VERTICAL_SPAN, "" + widget.getVerticalSpan() ); //$NON-NLS-1$
        }

        // Creating the 'width' attribute
        if ( widget.getImageWidth() != TemplateWidget.DEFAULT_SIZE )
        {
            element.addAttribute( ATTRIBUTE_WIDTH, "" + widget.getImageWidth() ); //$NON-NLS-1$
        }

        // Creating the 'height' attribute
        if ( widget.getImageHeight() != TemplateWidget.DEFAULT_SIZE )
        {
            element.addAttribute( ATTRIBUTE_HEIGHT, "" + widget.getImageHeight() ); //$NON-NLS-1$
        }
    }


    /**
     * Gets the String value associated with the given widget alignment.
     *
     * @param alignment
     *      the widget alignment
     * @return
     *      the String value associated with the given widget alignment
     */
    private static String getWidgetAlignmentValue( WidgetAlignment alignment )
    {
        switch ( alignment )
        {
            case NONE:
                return VALUE_NONE;
            case BEGINNING:
                return VALUE_BEGINNING;
            case CENTER:
                return VALUE_CENTER;
            case END:
                return VALUE_END;
            case FILL:
                return VALUE_FILL;
            default:
                return VALUE_NONE;
        }
    }


    /**
     * Writes the attribute type attribute of a widget.
     *
     * @param element
     *      the element
     * @param widget
     *      the widget
     */
    private static void writeAttributeTypeAttribute( Element element, TemplateWidget widget )
    {
        String attributeType = widget.getAttributeType();
        if ( ( attributeType != null ) && ( !attributeType.equals( "" ) ) ) //$NON-NLS-1$
        {
            element.addAttribute( ATTRIBUTE_ATTRIBUTETYPE, attributeType );
        }
    }


    /**
     * Writes a checkbox widget.
     *
     * @param element
     *      the parent element
     * @param checkbox
     *      the checkbox widget
     */
    private static void writeCheckbox( Element element, TemplateCheckbox checkbox )
    {
        // Creating the 'checkbox' element
        Element checkboxElement = element.addElement( ELEMENT_CHECKBOX );

        // Creating the widget's common properties
        writeWidgetCommonProperties( checkboxElement, checkbox );

        // Creating the 'label' attribute
        String label = ( String ) checkbox.getLabel();
        if ( ( label != null ) && ( !( label.equals( TemplateCheckbox.DEFAULT_LABEL ) ) ) )
        {
            checkboxElement.addAttribute( ATTRIBUTE_LABEL, checkbox.getLabel() );
        }

        // Creating the 'enabled' attribute
        if ( checkbox.isEnabled() != TemplateCheckbox.DEFAULT_ENABLED )
        {
            checkboxElement.addAttribute( ATTRIBUTE_ENABLED, "" + checkbox.isEnabled() ); //$NON-NLS-1$
        }

        // Creating the 'checkedValue' element (if necessary)
        String checkedValue = ( String ) checkbox.getCheckedValue();
        if ( ( checkedValue != null ) && ( !( checkedValue.equals( TemplateCheckbox.DEFAULT_CHECKED_VALUE ) ) ) )
        {
            Element checkedValueElement = checkboxElement.addElement( ELEMENT_CHECKEDVALUE );
            checkedValueElement.setText( checkedValue );
        }

        // Creating the 'uncheckedValue' element
        String uncheckedValue = ( String ) checkbox.getUncheckedValue();
        if ( ( uncheckedValue != null ) && ( !( uncheckedValue.equals( TemplateCheckbox.DEFAULT_UNCHECKED_VALUE ) ) ) )
        {
            Element uncheckedValueElement = checkboxElement.addElement( ELEMENT_UNCHECKEDVALUE );
            uncheckedValueElement.setText( uncheckedValue );
        }
    }


    /**
     * Writes a composite widget.
     *
     * @param element
     *      the parent element
     * @param composite
     *      the composite widget
     */
    private static void writeComposite( Element element, TemplateComposite composite )
    {
        // Creating the 'composite' element
        Element compositeElement = element.addElement( ELEMENT_COMPOSITE );

        // Creating the widget's common properties
        writeWidgetCommonProperties( compositeElement, composite );

        // Creating the 'numberOfColumns' attribute
        if ( composite.getNumberOfColumns() != TemplateComposite.DEFAULT_NUMBER_OF_COLUMNS )
        {
            compositeElement.addAttribute( ATTRIBUTE_NUMBEROFCOLUMNS, "" + composite.getNumberOfColumns() ); //$NON-NLS-1$
        }

        // Creating the 'equalColumns' attribute
        if ( composite.isEqualColumns() != TemplateComposite.DEFAULT_EQUAL_COLUMNS )
        {
            compositeElement.addAttribute( ATTRIBUTE_EQUALCOLUMNS, "" + composite.isEqualColumns() ); //$NON-NLS-1$
        }

        // Creating the children
        List<TemplateWidget> children = composite.getChildren();
        if ( ( children != null ) && ( children.size() > 0 ) )
        {
            for ( TemplateWidget child : children )
            {
                writeWidget( compositeElement, child );
            }
        }
    }


    /**
     * Writes a date widget.
     *
     * @param element
     *      the parent element
     * @param date
     *      the date widget
     */
    private static void writeDate( Element element, TemplateDate date )
    {
        // Creating the 'date' element
        Element dateElement = element.addElement( ELEMENT_DATE );

        // Creating the widget's common properties
        writeWidgetCommonProperties( dateElement, date );

        // Creating the 'showEditButton' attribute
        if ( date.isShowEditButton() != TemplateDate.DEFAULT_SHOW_EDIT_BUTTON )
        {
            dateElement.addAttribute( ATTRIBUTE_SHOWEDITBUTTON, convert( date.isShowEditButton() ) );
        }

        // Creating the 'format' element
        String format = date.getFormat();
        if ( ( format != null ) && ( !( format.equals( TemplateDate.DEFAULT_FORMAT ) ) ) )
        {
            dateElement.addAttribute( ATTRIBUTE_FORMAT, format );
        }
    }


    /**
     * Writes a file chooser widget.
     *
     * @param element
     *      the parent element
     * @param fileChooser
     *      the file chooser widget
     */
    private static void writeFileChooser( Element element, TemplateFileChooser fileChooser )
    {
        // Creating the 'fileChooser' element
        Element fileChooserElement = element.addElement( ELEMENT_FILECHOOSER );

        // Creating the widget's common properties
        writeWidgetCommonProperties( fileChooserElement, fileChooser );

        // Creating the 'extensions' attribute (if necessary)
        Set<String> extensions = fileChooser.getExtensions();
        if ( ( extensions != null ) && ( extensions.size() > 0 ) )
        {
            // Creating the string containing the extensions value (all the 
            // extensions are concatenated and split with a ',' character). 
            StringBuilder sb = new StringBuilder();
            for ( String extension : extensions )
            {
                sb.append( extension );
                sb.append( "," ); //$NON-NLS-1$
            }
            // Removing the last ',' character
            if ( sb.length() > 0 )
            {
                sb.deleteCharAt( sb.length() - 1 );
            }

            // Creating the 'extensions' attribute
            fileChooserElement.addAttribute( ATTRIBUTE_EXTENSIONS, sb.toString() );
        }

        // Creating the 'showIcon' attribute
        if ( fileChooser.isShowIcon() != TemplateFileChooser.DEFAULT_SHOW_ICON )
        {
            fileChooserElement.addAttribute( ATTRIBUTE_SHOWICON, convert( fileChooser.isShowIcon() ) );
        }

        // Creating the 'showSaveAsButton' attribute
        if ( fileChooser.isShowClearButton() != TemplateFileChooser.DEFAULT_SHOW_SAVE_AS_BUTTON )
        {
            fileChooserElement.addAttribute( ATTRIBUTE_SHOWSAVEASBUTTON, convert( fileChooser.isShowSaveAsButton() ) );
        }

        // Creating the 'showClearButton' attribute
        if ( fileChooser.isShowClearButton() != TemplateFileChooser.DEFAULT_SHOW_CLEAR_BUTTON )
        {
            fileChooserElement.addAttribute( ATTRIBUTE_SHOWCLEARBUTTON, convert( fileChooser.isShowClearButton() ) );
        }

        // Creating the 'showBrowseButton' attribute
        if ( fileChooser.isShowBrowseButton() != TemplateFileChooser.DEFAULT_SHOW_BROWSE_BUTTON )
        {
            fileChooserElement.addAttribute( ATTRIBUTE_SHOWBROWSEBUTTON, convert( fileChooser.isShowBrowseButton() ) );
        }

        // Creating the 'icon' element
        String icon = ( String ) fileChooser.getIcon();
        if ( ( icon != null ) && ( !( icon.equals( TemplateFileChooser.DEFAULT_ICON ) ) ) )
        {
            fileChooserElement.addElement( ELEMENT_ICON ).setText( icon );
        }
    }


    /**
     * Writes an image widget.
     *
     * @param element
     *      the parent element
     * @param image
     *      the image widget
     */
    private static void writeImage( Element element, TemplateImage image )
    {
        // Creating the 'image' element
        Element imageElement = element.addElement( ELEMENT_IMAGE );

        // Creating the widget's common properties
        writeWidgetCommonProperties( imageElement, image );

        // Creating the 'showSaveAsButton' attribute
        if ( image.isShowSaveAsButton() != TemplateImage.DEFAULT_SHOW_SAVE_AS_BUTTON )
        {
            imageElement.addAttribute( ATTRIBUTE_SHOWSAVEASBUTTON, convert( image.isShowSaveAsButton() ) );
        }

        // Creating the 'showClearButton' attribute
        if ( image.isShowClearButton() != TemplateImage.DEFAULT_SHOW_CLEAR_BUTTON )
        {
            imageElement.addAttribute( ATTRIBUTE_SHOWCLEARBUTTON, convert( image.isShowClearButton() ) );
        }

        // Creating the 'showChooseButton' attribute
        if ( image.isShowBrowseButton() != TemplateImage.DEFAULT_SHOW_BROWSE_BUTTON )
        {
            imageElement.addAttribute( ATTRIBUTE_SHOWBROWSEBUTTON, convert( image.isShowBrowseButton() ) );
        }

        // Creating the 'imageWidth' attribute
        if ( image.getImageWidth() != TemplateWidget.DEFAULT_SIZE )
        {
            imageElement.addAttribute( ATTRIBUTE_IMAGE_WIDTH, "" + image.getImageWidth() ); //$NON-NLS-1$
        }

        // Creating the 'imageHeight' attribute
        if ( image.getImageHeight() != TemplateWidget.DEFAULT_SIZE )
        {
            imageElement.addAttribute( ATTRIBUTE_IMAGE_HEIGHT, "" + image.getImageHeight() ); //$NON-NLS-1$
        }

        // Creating the 'data' element
        String imageData = ( String ) image.getImageData();
        if ( ( imageData != null ) && ( !( imageData.equals( TemplateImage.DEFAULT_IMAGE_DATA ) ) ) )
        {
            imageElement.addElement( ELEMENT_DATA ).setText( imageData );
        }
    }


    /**
     * Writes a label widget.
     *
     * @param element
     *      the parent element
     * @param label
     *      the label widget
     */
    private static void writeLabel( Element element, TemplateLabel label )
    {
        // Creating the 'label' element
        Element labelElement = element.addElement( ELEMENT_LABEL );

        // Creating the widget's common properties
        writeWidgetCommonProperties( labelElement, label );

        // Creating the 'value' attribute
        String value = label.getValue();
        if ( ( value != null ) && ( !( value.equals( TemplateLabel.DEFAULT_VALUE ) ) ) )
        {
            labelElement.addAttribute( ATTRIBUTE_VALUE, value );
        }

        // Creating the 'numberOfRows' attribute
        if ( label.getNumberOfRows() != TemplateLabel.DEFAULT_NUMBER_OF_ROWS )
        {
            labelElement.addAttribute( ATTRIBUTE_NUMBEROFROWS, "" + label.getNumberOfRows() ); //$NON-NLS-1$
        }

        // Creating the 'dollarSignIsNewLine' attribute
        if ( label.isDollarSignIsNewLine() != TemplateLabel.DEFAULT_DOLLAR_SIGN_IS_NEW_LINE )
        {
            labelElement.addAttribute( ATTRIBUTE_DOLLAR_SIGN_IS_NEW_LINE, "" + label.isDollarSignIsNewLine() ); //$NON-NLS-1$
        }
    }


    /**
     * Writes a link widget.
     *
     * @param element
     *      the parent element
     * @param link
     *      the link widget
     */
    private static void writeLink( Element element, TemplateLink link )
    {
        // Creating the 'link' element
        Element linkElement = element.addElement( ELEMENT_LINK );

        // Creating the widget's common properties
        writeWidgetCommonProperties( linkElement, link );

        // Creating the 'value' attribute
        String value = link.getValue();
        if ( ( value != null ) && ( !( value.equals( TemplateLink.DEFAULT_VALUE ) ) ) )
        {
            linkElement.addAttribute( ATTRIBUTE_VALUE, value );
        }
    }


    /**
     * Writes a lisbox widget.
     *
     * @param element
     *      the parent element
     * @param listbox
     */
    private static void writeListbox( Element element, TemplateListbox listbox )
    {
        // Creating the 'listbox' element
        Element listboxElement = element.addElement( ELEMENT_LISTBOX );

        // Creating the widget's common properties
        writeWidgetCommonProperties( listboxElement, listbox );

        // Creating the 'enabled' attribute
        if ( listbox.isEnabled() != TemplateListbox.DEFAULT_ENABLED )
        {
            listboxElement.addAttribute( ATTRIBUTE_ENABLED, "" + listbox.isEnabled() ); //$NON-NLS-1$
        }

        // Creating the 'multipleSelection' attribute
        if ( listbox.isMultipleSelection() != TemplateListbox.DEFAULT_MULTIPLE_SELECTION )
        {
            listboxElement.addAttribute( ATTRIBUTE_MULTIPLESELECTION, convert( listbox.isMultipleSelection() ) );
        }

        // Creating the 'items' element
        List<ValueItem> items = listbox.getItems();
        if ( ( items != null ) && ( items.size() > 0 ) )
        {
            Element itemsElement = listboxElement.addElement( ELEMENT_ITEMS );

            // Creating the 'item' elements
            for ( ValueItem item : items )
            {
                Element itemElement = itemsElement.addElement( ELEMENT_ITEM );
                writeValueItem( itemElement, item );
            }
        }
    }


    /**
     * Writes a value item.
     *
     * @param element
     *      the parent element
     * @param item
     *      the value item
     */
    private static void writeValueItem( Element element, ValueItem item )
    {
        // Creating the 'label' element
        String itemLabel = item.getLabel();
        if ( ( itemLabel != null ) && ( !itemLabel.equals( "" ) ) ) //$NON-NLS-1$
        {
            element.addElement( ELEMENT_LABEL ).setText( itemLabel );
        }

        // Creating the 'value' element
        String itemValue = ( String ) item.getValue();
        if ( ( itemValue != null ) && ( !itemValue.equals( "" ) ) ) //$NON-NLS-1$
        {
            element.addElement( ELEMENT_VALUE ).setText( itemValue );
        }
    }


    /**
     * Writes a password widget.
     *
     * @param element
     *      the parent element
     * @param password
     *      the password widget
     */
    private static void writePassword( Element element, TemplatePassword password )
    {
        // Creating the 'password' element
        Element passwordElement = element.addElement( ELEMENT_PASSWORD );

        // Creating the widget's common properties
        writeWidgetCommonProperties( passwordElement, password );

        // Creating the 'hidden' attribute
        if ( password.isHidden() != TemplatePassword.DEFAULT_HIDDEN )
        {
            passwordElement.addAttribute( ATTRIBUTE_HIDDEN, convert( password.isHidden() ) );
        }

        // Creating the 'showEditButton' attribute
        if ( password.isShowEditButton() != TemplatePassword.DEFAULT_SHOW_EDIT_BUTTON )
        {
            passwordElement.addAttribute( ATTRIBUTE_SHOWEDITBUTTON, convert( password.isShowEditButton() ) );
        }

        // Creating the 'showShowPasswordCheckbox' attribute
        if ( password.isShowShowPasswordCheckbox() != TemplatePassword.DEFAULT_SHOW_PASSWORD_CHECKBOX )
        {
            passwordElement.addAttribute( ATTRIBUTE_SHOWSHOWPASSWORDCHECKBOX, convert( password
                .isShowShowPasswordCheckbox() ) );
        }
    }


    /**
     * Writes a radio buttons widget.
     *
     * @param element
     *      the parent element
     * @param radioButtons
     *      the radio buttons widget
     */
    private static void writeRadioButtons( Element element, TemplateRadioButtons radioButtons )
    {
        // Creating the 'radioButtons' element
        Element radioButtonsElement = element.addElement( ELEMENT_RADIOBUTTONS );

        // Creating the widget's common properties
        writeWidgetCommonProperties( element, radioButtons );

        // Creating the 'enabled' attribute
        if ( radioButtons.isEnabled() != TemplateRadioButtons.DEFAULT_ENABLED )
        {
            radioButtonsElement.addAttribute( ATTRIBUTE_ENABLED, "" + radioButtons.isEnabled() ); //$NON-NLS-1$
        }

        // Creating the 'buttons' element
        List<ValueItem> buttons = radioButtons.getButtons();
        if ( ( buttons != null ) && ( buttons.size() > 0 ) )
        {
            Element buttonsElement = radioButtonsElement.addElement( ELEMENT_BUTTONS );

            for ( ValueItem button : buttons )
            {
                Element buttonElement = buttonsElement.addElement( ELEMENT_BUTTON );
                writeValueItem( buttonElement, button );
            }
        }
    }


    /**
     * Writes a section widget.
     *
     * @param element
     *      the parent element
     * @param section
     *      the section widget
     */
    private static void writeSection( Element element, TemplateSection section )
    {
        // Creating the 'section' element
        Element sectionElement = element.addElement( ELEMENT_SECTION );

        // Creating the widget's common properties
        writeWidgetCommonProperties( sectionElement, section );

        // Creating the 'title' attribute
        String title = section.getTitle();
        if ( ( title != null ) && ( !( title.equals( TemplateSection.DEFAULT_TITLE ) ) ) )
        {
            sectionElement.addAttribute( ATTRIBUTE_TITLE, title );
        }

        // Creating the 'description' attribute
        String description = section.getDescription();
        if ( ( description != null ) && ( !( description.equals( TemplateSection.DEFAULT_DESCRIPTION ) ) ) )
        {
            sectionElement.addAttribute( ATTRIBUTE_DESCRIPTION, description );
        }

        // Creating the 'numberOfColumns' attribute
        if ( section.getNumberOfColumns() != TemplateSection.DEFAULT_NUMBER_OF_COLUMNS )
        {
            sectionElement.addAttribute( ATTRIBUTE_NUMBEROFCOLUMNS, "" + section.getNumberOfColumns() ); //$NON-NLS-1$
        }

        // Creating the 'equalColumns' attribute
        if ( section.isEqualColumns() != TemplateSection.DEFAULT_EQUAL_COLUMNS )
        {
            sectionElement.addAttribute( ATTRIBUTE_EQUALCOLUMNS, "" + section.isEqualColumns() ); //$NON-NLS-1$
        }

        // Creating the 'expandable' attribute
        if ( section.isExpandable() != TemplateSection.DEFAULT_EXPANDABLE )
        {
            sectionElement.addAttribute( ATTRIBUTE_EXPANDABLE, "" + section.isExpandable() ); //$NON-NLS-1$
        }

        // Creating the 'expanded' attribute
        if ( section.isExpanded() != TemplateSection.DEFAULT_EXPANDED )
        {
            sectionElement.addAttribute( ATTRIBUTE_EXPANDED, "" + section.isExpanded() ); //$NON-NLS-1$
        }

        // Creating the children
        List<TemplateWidget> children = section.getChildren();
        if ( ( children != null ) && ( children.size() > 0 ) )
        {
            for ( TemplateWidget child : children )
            {
                writeWidget( sectionElement, child );
            }
        }
    }


    /**
     * Writes a spinner widget.
     *
     * @param element
     *      the parent element
     * @param spinner
     *      the spinner widget
     */
    private static void writeSpinner( Element element, TemplateSpinner spinner )
    {
        // Creating the 'spinner' element
        Element spinnerElement = element.addElement( ELEMENT_SPINNER );

        // Creating the widget's common properties
        writeWidgetCommonProperties( spinnerElement, spinner );

        // Reading the 'minimum' attribute
        if ( spinner.getMinimum() != TemplateSpinner.DEFAULT_MINIMUM )
        {
            spinnerElement.addAttribute( ATTRIBUTE_MINIMUM, "" + spinner.getMinimum() ); //$NON-NLS-1$
        }

        // Reading the 'maximum' attribute
        if ( spinner.getMaximum() != TemplateSpinner.DEFAULT_MAXIMUM )
        {
            spinnerElement.addAttribute( ATTRIBUTE_MAXIMUM, "" + spinner.getMaximum() ); //$NON-NLS-1$
        }

        // Reading the 'increment' attribute
        if ( spinner.getIncrement() != TemplateSpinner.DEFAULT_INCREMENT )
        {
            spinnerElement.addAttribute( ATTRIBUTE_INCREMENT, "" + spinner.getIncrement() ); //$NON-NLS-1$
        }

        // Reading the 'pageIncrement' attribute
        if ( spinner.getPageIncrement() != TemplateSpinner.DEFAULT_PAGE_INCREMENT )
        {
            spinnerElement.addAttribute( ATTRIBUTE_PAGEINCREMENT, "" + spinner.getPageIncrement() ); //$NON-NLS-1$
        }

        // Reading the 'digits' attribute
        if ( spinner.getDigits() != TemplateSpinner.DEFAULT_DIGITS )
        {
            spinnerElement.addAttribute( ATTRIBUTE_DIGITS, "" + spinner.getDigits() ); //$NON-NLS-1$
        }
    }


    /**
     * Writes the table widget.
     *
     * @param element
     *      the parent element
     * @param table
     *      the table widget
     */
    private static void writeTable( Element element, TemplateTable table )
    {
        // Creating the 'table' element
        Element tableElement = element.addElement( ELEMENT_TABLE );

        // Creating the widget's common properties
        writeWidgetCommonProperties( tableElement, table );

        // Creating the 'showAddButton' attribute
        if ( table.isShowAddButton() != TemplateTable.DEFAULT_SHOW_ADD_BUTTON )
        {
            tableElement.addAttribute( ATTRIBUTE_SHOWADDBUTTON, convert( table.isShowAddButton() ) );
        }

        // Creating the 'showEditButton' attribute
        if ( table.isShowEditButton() != TemplateTable.DEFAULT_SHOW_EDIT_BUTTON )
        {
            tableElement.addAttribute( ATTRIBUTE_SHOWEDITBUTTON, convert( table.isShowEditButton() ) );
        }

        // Creating the 'showDeleteButton' attribute
        if ( table.isShowDeleteButton() != TemplateTable.DEFAULT_SHOW_DELETE_BUTTON )
        {
            tableElement.addAttribute( ATTRIBUTE_SHOWDELETEBUTTON, convert( table.isShowDeleteButton() ) );
        }
    }


    /**
     * Writes a text field widget.
     *
     * @param element
     *      the parent element
     * @param textField
     *      the text field widget
     */
    private static void writeTextfield( Element element, TemplateTextField textField )
    {
        // Creating the 'textField' element
        Element textFieldElement = element.addElement( ELEMENT_TEXTFIELD );

        // Creating the widget's common properties
        writeWidgetCommonProperties( textFieldElement, textField );

        // Creating the 'numberOfRows' attribute
        if ( textField.getNumberOfRows() != TemplateTextField.DEFAULT_NUMBER_OF_ROWS )
        {
            textFieldElement.addAttribute( ATTRIBUTE_NUMBEROFROWS, "" + textField.getNumberOfRows() ); //$NON-NLS-1$
        }

        // Creating the 'charactersLimit' attribute
        if ( textField.getCharactersLimit() != TemplateTextField.DEFAULT_CHARACTERS_LIMIT )
        {
            textFieldElement.addAttribute( ATTRIBUTE_CHARACTERSLIMIT, "" + textField.getCharactersLimit() ); //$NON-NLS-1$
        }

        // Creating the 'dollarSignIsNewLine' attribute
        if ( textField.isDollarSignIsNewLine() != TemplateTextField.DEFAULT_DOLLAR_SIGN_IS_NEW_LINE )
        {
            textFieldElement.addAttribute( ATTRIBUTE_DOLLAR_SIGN_IS_NEW_LINE, "" + textField.isDollarSignIsNewLine() ); //$NON-NLS-1$
        }
    }


    /**
     * Converts a boolean.
     *
     * @param bool
     *      the boolean
     * @return
     *      its String representation
     */
    private static String convert( boolean bool )
    {
        if ( bool )
        {
            return VALUE_TRUE;
        }
        else
        {
            return VALUE_FALSE;
        }
    }
}