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
package org.apache.directory.studio.templateeditor.model.widgets;


/**
 * This class implements a template text field.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TemplateTextField extends AbstractTemplateWidget
{
    /** The default number of rows value */
    public static int DEFAULT_NUMBER_OF_ROWS = 1;

    /** The default characters limit value */
    public static int DEFAULT_CHARACTERS_LIMIT = -1;

    /** The default dollar sign is new line value */
    public static boolean DEFAULT_DOLLAR_SIGN_IS_NEW_LINE = false;

    /** The number of rows */
    private int numberOfRows = DEFAULT_NUMBER_OF_ROWS;

    /** The characters limit */
    private int charactersLimit = DEFAULT_CHARACTERS_LIMIT;

    /** The flag which indicates if dollar sign ('$') is to be interpreted as a new line */
    private boolean dollarSignIsNewLine = DEFAULT_DOLLAR_SIGN_IS_NEW_LINE;


    /**
     * Creates a new instance of TemplateTextField.
     *
     * @param parent
     *      the parent element
     */
    public TemplateTextField( TemplateWidget parent )
    {
        super( parent );
    }


    /**
     * Gets the characters limit.
     *
     * @return
     *      the characters limit
     */
    public int getCharactersLimit()
    {
        return charactersLimit;
    }


    /**
     * Gets the number of rows.
     *
     * @return
     *      the number of rows
     */
    public int getNumberOfRows()
    {
        return numberOfRows;
    }


    /**
     * Indicates if dollar sign ('$') is to be interpreted as a new line.
     *
     * @return
     *      <code>true</code> if dollar sign ('$') is to be interpreted as a new line, 
     *      <code>false</code> if not
     */
    public boolean isDollarSignIsNewLine()
    {
        return dollarSignIsNewLine;
    }


    /**
     * Sets the characters limit.
     *
     * @param charactersLimit
     *      the characters limit
     */
    public void setCharactersLimit( int charactersLimit )
    {
        this.charactersLimit = charactersLimit;
    }


    /**
     * Sets the flag which indicates if dollar sign ('$') is to be interpreted as a new line.
     *
     * @param dollarSignIsNewLine
     *      <code>true</code> if dollar sign ('$') is to be interpreted as a new line, 
     *      <code>false</code> if not
     */
    public void setDollarSignIsNewLine( boolean dollarSignIsNewLine )
    {
        this.dollarSignIsNewLine = dollarSignIsNewLine;
    }


    /**
     * Sets the number of rows.
     *
     * @param numberOfRows
     *      the number of rows
     */
    public void setNumberOfRows( int numberOfRows )
    {
        this.numberOfRows = numberOfRows;
    }
}
