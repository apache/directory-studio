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


import junit.framework.TestCase;

import org.apache.directory.studio.templateeditor.model.FileTemplate;
import org.apache.directory.studio.templateeditor.model.parser.TemplateIO;
import org.apache.directory.studio.templateeditor.model.parser.TemplateIOException;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateCheckbox;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateComposite;
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
import org.apache.directory.studio.templateeditor.model.widgets.ValueItem;


/**
 * This class is used test the {@link TemplateIO} class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateIOTest extends TestCase
{
    /**
     * Tests the parser with a minimal template file.
     */
    public void testReadTemplateMinimalTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_minimal.xml" ) //$NON-NLS-1$
                .openStream() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking if a template has been created
        assertNotNull( template );

        // Checking the title
        assertEquals( "id", template.getId() ); //$NON-NLS-1$

        // Checking the title
        assertEquals( "Template Title", template.getTitle() ); //$NON-NLS-1$

        // Checking the objectClasses
        assertNotNull( template.getStructuralObjectClass() );
        assertNotNull( template.getAuxiliaryObjectClasses() );
        assertEquals( 0, template.getAuxiliaryObjectClasses().size() );

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );
    }


    /**
     * Tests the parser with a minimal template file.
     */
    public void testReadTemplateMinimalWithCompositeTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_minimal_with_composite.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking if a template has been created
        assertNotNull( template );

        // Checking the title
        assertEquals( "id", template.getId() ); //$NON-NLS-1$

        // Checking the title
        assertEquals( "Template Title", template.getTitle() ); //$NON-NLS-1$

        // Checking the objectClasses
        assertNotNull( template.getStructuralObjectClass() );
        assertNotNull( template.getAuxiliaryObjectClasses() );
        assertEquals( 2, template.getAuxiliaryObjectClasses().size() );
        assertEquals( "a", template.getAuxiliaryObjectClasses().get( 0 ) ); //$NON-NLS-1$
        assertEquals( "b", template.getAuxiliaryObjectClasses().get( 1 ) ); //$NON-NLS-1$

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );
    }


    /**
     * Tests the parser with a template file containing a section with a 
     * 'columns' attribute.
     */
    public void testReadTemplateSectionColumnsAttributeTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_section_with_columns_attribute.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertEquals( 2, section.getNumberOfColumns() );
    }


    /**
     * Tests the parser with a template file containing a section with a 
     * 'columns' attribute.
     */
    public void testReadTemplateSectionDescriptionAttributeTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_section_with_description_attribute.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertEquals( "Section description", section.getDescription() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a section with a 
     * 'columns' attribute.
     */
    public void testReadTemplateSectionTitleAttributeTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_section_with_title_attribute.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertEquals( "Section title", section.getTitle() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a section with a wrong
     * 'columns' attribute.
     */
    public void testReadTemplateSectionWrongColumnsAttributeTest()
    {
        testParsingFail( "template_section_with_wrong_columns_attribute.xml" ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a checkbox with a 
     * value for the 'attributeType' attribute.
     */
    public void testReadTemplateCheckboxAttributetypeValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_checkbox_with_attributetype_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the checkbox
        TemplateCheckbox checkbox = ( TemplateCheckbox ) section.getChildren().get( 0 );
        assertNotNull( checkbox );
        assertEquals( "1.2.3.4", checkbox.getAttributeType() ); //$NON-NLS-1$
        assertEquals( "label", checkbox.getLabel() ); //$NON-NLS-1$
        assertNull( checkbox.getCheckedValue() );
        assertNull( checkbox.getUncheckedValue() );
    }


    /**
     * Tests the parser with a template file containing a checkbox with a 
     * value for the 'checkedValue' attribute.
     */
    public void testReadTemplateCheckboxCheckedValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_checkbox_with_checked_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the checkbox
        TemplateCheckbox checkbox = ( TemplateCheckbox ) section.getChildren().get( 0 );
        assertNotNull( checkbox );
        assertEquals( "1.2.3.4", checkbox.getAttributeType() ); //$NON-NLS-1$
        assertEquals( "label", checkbox.getLabel() ); //$NON-NLS-1$
        assertEquals( "Checked value", checkbox.getCheckedValue() ); //$NON-NLS-1$
        assertNull( checkbox.getUncheckedValue() );
    }


    /**
     * Tests the parser with a template file containing a checkbox with a 
     * value for the 'uncheckedValue' attribute.
     */
    public void testReadTemplateCheckboxUncheckedValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_checkbox_with_unchecked_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the checkbox
        TemplateCheckbox checkbox = ( TemplateCheckbox ) section.getChildren().get( 0 );
        assertNotNull( checkbox );
        assertEquals( "1.2.3.4", checkbox.getAttributeType() ); //$NON-NLS-1$
        assertEquals( "label", checkbox.getLabel() ); //$NON-NLS-1$
        assertNull( checkbox.getCheckedValue() );
        assertEquals( "Unchecked value", checkbox.getUncheckedValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a checkbox with a 
     * value for the 'uncheckedValue' attribute.
     */
    public void testReadTemplateCheckboxCheckedAndUnheckedValuesTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_checkbox_with_checked_and_unchecked_values.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the checkbox
        TemplateCheckbox checkbox = ( TemplateCheckbox ) section.getChildren().get( 0 );
        assertNotNull( checkbox );
        assertEquals( "1.2.3.4", checkbox.getAttributeType() ); //$NON-NLS-1$
        assertEquals( "label", checkbox.getLabel() ); //$NON-NLS-1$
        assertEquals( "Checked value", checkbox.getCheckedValue() ); //$NON-NLS-1$
        assertEquals( "Unchecked value", checkbox.getUncheckedValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a file chooser with 
     * only value for the 'attributeType' attribute.
     */
    public void testReadTemplateFileChooserAttributeTypeValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_filechooser_with_attributetype_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the filechooser
        TemplateFileChooser filechooser = ( TemplateFileChooser ) section.getChildren().get( 0 );
        assertNotNull( filechooser );
        assertEquals( "1.2.3.4", filechooser.getAttributeType() ); //$NON-NLS-1$
        assertTrue( filechooser.isShowBrowseButton() );
        assertTrue( filechooser.isShowClearButton() );
        assertTrue( filechooser.isShowSaveAsButton() );
        assertEquals( 0, filechooser.getExtensions().size() );
        assertNull( filechooser.getIcon() );
    }


    /**
     * Tests the parser with a template file containing a file chooser with all 
     * values.
     */
    public void testReadTemplateFileChooserAllValuesTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_filechooser_with_all_values.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the filechooser
        TemplateFileChooser filechooser = ( TemplateFileChooser ) section.getChildren().get( 0 );
        assertNotNull( filechooser );
        assertEquals( "1.2.3.4", filechooser.getAttributeType() ); //$NON-NLS-1$
        assertFalse( filechooser.isShowBrowseButton() );
        assertFalse( filechooser.isShowClearButton() );
        assertFalse( filechooser.isShowSaveAsButton() );
        assertEquals( 2, filechooser.getExtensions().size() );
        assertEquals( "data", filechooser.getIcon() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing an image with only the
     * value for the 'attributeType' attribute.
     */
    public void testReadTemplateImageAttributeTypeValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_image_with_attributetype_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the image
        TemplateImage image = ( TemplateImage ) section.getChildren().get( 0 );
        assertNotNull( image );
        assertEquals( "1.2.3.4", image.getAttributeType() ); //$NON-NLS-1$
        assertTrue( image.isShowBrowseButton() );
        assertTrue( image.isShowClearButton() );
        assertTrue( image.isShowSaveAsButton() );
        assertNull( image.getImageData() );
        assertEquals( -1, image.getImageWidth() );
        assertEquals( -1, image.getImageHeight() );
    }


    /**
     * Tests the parser with a template file containing an image with all
     * values.
     */
    public void testReadTemplateImageAllValuesTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass()
                .getResource( "template_image_with_all_values.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the image
        TemplateImage image = ( TemplateImage ) section.getChildren().get( 0 );
        assertNotNull( image );
        assertEquals( "1.2.3.4", image.getAttributeType() ); //$NON-NLS-1$
        assertFalse( image.isShowBrowseButton() );
        assertFalse( image.isShowClearButton() );
        assertFalse( image.isShowSaveAsButton() );
        assertEquals( "data", image.getImageData() ); //$NON-NLS-1$
        assertEquals( 16, image.getImageWidth() );
        assertEquals( 9, image.getImageHeight() );
    }


    /**
     * Tests the parser with a template file containing a label with only the
     * value for the 'attributeType' attribute.
     */
    public void testReadTemplateLabelAttributeTypeValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_label_with_attributetype_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the label
        TemplateLabel label = ( TemplateLabel ) section.getChildren().get( 0 );
        assertNotNull( label );
        assertEquals( "1.2.3.4", label.getAttributeType() ); //$NON-NLS-1$
        assertNull( label.getValue() );
    }


    /**
     * Tests the parser with a template file containing a label with a value
     * for the 'value' attribute.
     */
    public void testReadTemplateLabelValueValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_label_with_value_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the label
        TemplateLabel label = ( TemplateLabel ) section.getChildren().get( 0 );
        assertNotNull( label );
        assertNull( label.getAttributeType() );
        assertEquals( "A label", label.getValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a link with only the
     * value for the 'attributeType' attribute.
     */
    public void testReadTemplateLinkAttributeTypeValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_link_with_attributetype_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the link
        TemplateLink link = ( TemplateLink ) section.getChildren().get( 0 );
        assertNotNull( link );
        assertEquals( "1.2.3.4", link.getAttributeType() ); //$NON-NLS-1$
        assertNull( link.getValue() );
    }


    /**
     * Tests the parser with a template file containing a link with a value
     * for the 'value' attribute.
     */
    public void testReadTemplateLinkValueValueTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass()
                .getResource( "template_link_with_value_value.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the link
        TemplateLink link = ( TemplateLink ) section.getChildren().get( 0 );
        assertNotNull( link );
        assertNull( link.getAttributeType() );
        assertEquals( "http://www.iktek.com", link.getValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a listbox with the 
     * minimal set of elements and attributes.
     */
    public void testReadTemplateListboxMinimalTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_listbox_minimal.xml" ) //$NON-NLS-1$
                .openStream() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the listbox
        TemplateListbox listbox = ( TemplateListbox ) section.getChildren().get( 0 );
        assertNotNull( listbox );
        assertEquals( "1.2.3.4", listbox.getAttributeType() ); //$NON-NLS-1$
        assertTrue( listbox.isMultipleSelection() );
        assertNotNull( listbox.getItems() );
        assertEquals( 1, listbox.getItems().size() );
        assertTrue( listbox.getItems().contains( new ValueItem( "label", "value" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Tests the parser with a template file containing a listbox with the 
     * minimal set of elements and attributes.
     */
    public void testReadTemplateListboxSingleSelectionTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_listbox_single_selection.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the listbox
        TemplateListbox listbox = ( TemplateListbox ) section.getChildren().get( 0 );
        assertNotNull( listbox );
        assertEquals( "1.2.3.4", listbox.getAttributeType() ); //$NON-NLS-1$
        assertFalse( listbox.isMultipleSelection() );
        assertNotNull( listbox.getItems() );
        assertEquals( 1, listbox.getItems().size() );
        assertTrue( listbox.getItems().contains( new ValueItem( "label", "value" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Tests the parser with a template file containing a listbox with 
     * multiple items.
     */
    public void testReadTemplateListboxMultipleItemsTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_listbox_multiple_items.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the listbox
        TemplateListbox listbox = ( TemplateListbox ) section.getChildren().get( 0 );
        assertNotNull( listbox );
        assertEquals( "1.2.3.4", listbox.getAttributeType() ); //$NON-NLS-1$
        assertTrue( listbox.isMultipleSelection() );
        assertNotNull( listbox.getItems() );
        assertEquals( 3, listbox.getItems().size() );
        assertTrue( listbox.getItems().contains( new ValueItem( "label 1", "value 1" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue( listbox.getItems().contains( new ValueItem( "label 2", "value 2" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue( listbox.getItems().contains( new ValueItem( "label 3", "value 3" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Tests the parser with a template file containing a password with the 
     * minimal set of elements and attributes.
     */
    public void testReadTemplatePasswordMinimalTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_password_minimal.xml" ) //$NON-NLS-1$
                .openStream() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the password
        TemplatePassword password = ( TemplatePassword ) section.getChildren().get( 0 );
        assertNotNull( password );
        assertEquals( "1.2.3.4", password.getAttributeType() ); //$NON-NLS-1$
        assertTrue( password.isHidden() );
        assertTrue( password.isShowEditButton() );
    }


    /**
     * Tests the parser with a template file containing a password with the 
     * minimal set of elements and attributes.
     */
    public void testReadTemplatePasswordNotHiddenTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_password_not_hidden.xml" ) //$NON-NLS-1$
                .openStream() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the password
        TemplatePassword password = ( TemplatePassword ) section.getChildren().get( 0 );
        assertNotNull( password );
        assertEquals( "1.2.3.4", password.getAttributeType() ); //$NON-NLS-1$
        assertFalse( password.isHidden() );
        assertTrue( password.isShowEditButton() );
    }


    /**
     * Tests the parser with a template file containing a password with the 
     * minimal set of elements and attributes.
     */
    public void testReadTemplatePasswordNotShowChangeButtonTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_password_not_show_edit_button.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the password
        TemplatePassword password = ( TemplatePassword ) section.getChildren().get( 0 );
        assertNotNull( password );
        assertEquals( "1.2.3.4", password.getAttributeType() ); //$NON-NLS-1$
        assertTrue( password.isHidden() );
        assertFalse( password.isShowEditButton() );
    }


    /**
     * Tests the parser with a template file containing a radio buttons with 
     * the minimal set of elements and attributes.
     */
    public void testReadTemplateRadioButtonsMinimalTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_radiobuttons_minimal.xml" ) //$NON-NLS-1$
                .openStream() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the radio buttons
        TemplateRadioButtons radioButtons = ( TemplateRadioButtons ) section.getChildren().get( 0 );
        assertNotNull( radioButtons );
        assertEquals( "1.2.3.4", radioButtons.getAttributeType() ); //$NON-NLS-1$
        assertNotNull( radioButtons.getButtons() );
        assertEquals( 1, radioButtons.getButtons().size() );
        assertTrue( radioButtons.getButtons().contains( new ValueItem( "label", "value" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Tests the parser with a template file containing a radio buttons with 
     * multiple buttons.
     */
    public void testReadTemplateRadioButtonsMultipleButtonsTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_radiobuttons_multiple_buttons.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the radio buttons
        TemplateRadioButtons radioButtons = ( TemplateRadioButtons ) section.getChildren().get( 0 );
        assertNotNull( radioButtons );
        assertEquals( "1.2.3.4", radioButtons.getAttributeType() ); //$NON-NLS-1$
        assertNotNull( radioButtons.getButtons() );
        assertEquals( 3, radioButtons.getButtons().size() );
        assertTrue( radioButtons.getButtons().contains( new ValueItem( "label 1", "value 1" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue( radioButtons.getButtons().contains( new ValueItem( "label 2", "value 2" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue( radioButtons.getButtons().contains( new ValueItem( "label 3", "value 3" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Tests the parser with a template file containing a spinner with the 
     * minimal set of elements and attributes.
     */
    public void testReadTemplateSpinnerMinimalTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_spinner_minimal.xml" ) //$NON-NLS-1$
                .openStream() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the spinner
        TemplateSpinner spinner = ( TemplateSpinner ) section.getChildren().get( 0 );
        assertNotNull( spinner );
        assertEquals( "1.2.3.4", spinner.getAttributeType() ); //$NON-NLS-1$
        assertEquals( Integer.MIN_VALUE, spinner.getMinimum() );
        assertEquals( Integer.MAX_VALUE, spinner.getMaximum() );
        assertEquals( 1, spinner.getIncrement() );
        assertEquals( 10, spinner.getPageIncrement() );
        assertEquals( 0, spinner.getDigits() );
    }


    /**
     * Tests the parser with a template file containing a spinner with all the
     * values for its attributes.
     */
    public void testReadTemplateSpinnerAllValuesTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_spinner_with_all_values.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the spinner
        TemplateSpinner spinner = ( TemplateSpinner ) section.getChildren().get( 0 );
        assertNotNull( spinner );
        assertEquals( "1.2.3.4", spinner.getAttributeType() ); //$NON-NLS-1$
        assertEquals( 10, spinner.getMinimum() );
        assertEquals( 10000, spinner.getMaximum() );
        assertEquals( 10, spinner.getIncrement() );
        assertEquals( 100, spinner.getPageIncrement() );
        assertEquals( 2, spinner.getDigits() );
    }


    /**
     * Tests the parser with a template file containing a table.
     */
    public void testReadTemplateTableTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_table.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the table
        TemplateTable table = ( TemplateTable ) section.getChildren().get( 0 );
        assertNotNull( table );
        assertEquals( "1.2.3.4", table.getAttributeType() ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template file containing a text field with the
     * minimal set of elements and attributes.
     */
    public void testReadTemplateTextFieldMinimalTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource( "template_textfield_minimal.xml" ) //$NON-NLS-1$
                .openStream() );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the textfield
        TemplateTextField textfield = ( TemplateTextField ) section.getChildren().get( 0 );
        assertNotNull( textfield );
        assertEquals( "1.2.3.4", textfield.getAttributeType() ); //$NON-NLS-1$
        assertEquals( 1, textfield.getNumberOfRows() );
        assertEquals( -1, textfield.getCharactersLimit() );
    }


    /**
     * Tests the parser with a template file containing a text field with the
     * minimal set of elements and attributes.
     */
    public void testReadTemplateTextFieldAllValuesTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_textfield_with_all_values.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateSection section = ( TemplateSection ) form.getChildren().get( 0 );
        assertNotNull( section );
        assertNotNull( section.getChildren() );
        assertEquals( 1, section.getChildren().size() );

        // Checking the textfield
        TemplateTextField textfield = ( TemplateTextField ) section.getChildren().get( 0 );
        assertNotNull( textfield );
        assertEquals( "1.2.3.4", textfield.getAttributeType() ); //$NON-NLS-1$
        assertEquals( 10, textfield.getNumberOfRows() );
        assertEquals( 256, textfield.getCharactersLimit() );
    }


    /**
     * Tests the parser with a template containing a wrong root element.
     */
    public void testReadTemplateWrongRootElementTest()
    {
        testParsingFail( "template_wrong_root_element.xml" ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template containing no 'id' attribute.
     */
    public void testReadTemplateNoIdAttributeTest()
    {
        testParsingFail( "template_no_id_attribute.xml" ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template containing no 'title' attribute.
     */
    public void testReadTemplateNoTitleAttributeTest()
    {
        testParsingFail( "template_no_title_attribute.xml" ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template containing no 'objectClasses' element.
     */
    public void testReadTemplateNoObjectClassesElementTest()
    {
        testParsingFail( "template_no_objectClasses_element.xml" ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template containing no 'structural' element.
     */
    public void testReadTemplateNoObjectClassElementTest()
    {
        testParsingFail( "template_no_structural_element.xml" ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template containing no 'form' element.
     */
    public void testReadTemplateNoFormElementTest()
    {
        testParsingFail( "template_no_form_element.xml" ); //$NON-NLS-1$
    }


    /**
     * Tests the parser with a template containing no 'section' element.
     */
    public void testReadTemplateNoSectionElementTest()
    {
        testParsingFail( "template_no_section_element.xml" ); //$NON-NLS-1$
    }


    /**
     * Asserts that parsing throws a correct TemplateIOException due to an incorrect file.
     *
     * @param testClass
     *      the Class of the TestCase
     * @param filename
     *      the path of the xml file to parse 
     */
    public void testParsingFail( String filename )
    {
        try
        {
            TemplateIO.readAsFileTemplate( this.getClass().getResource( filename ).openStream() );
        }
        catch ( TemplateIOException e )
        {
            assertTrue( e.getMessage(), true );
            return;
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        fail();
    }


    /**
     * Tests the parser with a template file containing a section with a 
     * 'columns' attribute.
     */
    public void testReadTemplateCompositeColumnsAttributeTest()
    {
        FileTemplate template = null;

        try
        {
            template = TemplateIO.readAsFileTemplate( this.getClass().getResource(
                "template_composite_with_columns_attribute.xml" ).openStream() ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
            return;
        }

        // Checking the form
        TemplateForm form = template.getForm();
        assertNotNull( form );
        assertNotNull( form.getChildren() );
        assertEquals( 1, form.getChildren().size() );

        // Checking the section
        TemplateComposite composite = ( TemplateComposite ) form.getChildren().get( 0 );
        assertNotNull( composite );
        assertEquals( 2, composite.getNumberOfColumns() );
    }
}
