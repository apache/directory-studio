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

// $ANTLR 2.7.5 (20050128): "schema.g" -> "SchemaParser.java"$
package org.apache.directory.studio.ldapbrowser.core.model.schema.parser;




public interface SchemaTokenTypes
{
    int EOF = 1;

    int NULL_TREE_LOOKAHEAD = 3;

    int WHSP = 4;

    int LPAR = 5;

    int RPAR = 6;

    int QUOTE = 7;

    int DOLLAR = 8;

    int LBRACKET = 9;

    int RBRACKET = 10;

    int LEN = 11;

    int USAGE_USERAPPLICATIONS = 12;

    int USAGE_DIRECTORYOPERATION = 13;

    int USAGE_DISTRIBUTEDOPERATION = 14;

    int USAGE_DSAOPERATION = 15;

    int STARTNUMERICOID = 16;

    int NAME = 17;

    int DESC = 18;

    int SUP = 19;

    int MUST = 20;

    int MAY = 21;

    int EQUALITY = 22;

    int ORDERING = 23;

    int SUBSTR = 24;

    int SYNTAX = 25;

    int USAGE = 26;

    int APPLIES = 27;

    int X = 28;

    int SINGLE_VALUE = 29;

    int COLLECTIVE = 30;

    int NO_USER_MODIFICATION = 31;

    int OBSOLETE = 32;

    int ABSTRACT = 33;

    int STRUCTURAL = 34;

    int AUXILIARY = 35;

    int VALUES = 36;

    int VALUE = 37;

    int UNQUOTED_STRING = 38;

    int QUOTED_STRING = 39;
}
