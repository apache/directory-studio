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

package org.apache.directory.studio.common.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


/**
 * Constants used in the connection UI plugin.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class CommonUIConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private CommonUIConstants()
    {
    }

    /** The plug-in ID */
    public static final String PLUGIN_ID = CommonUIPlugin.getDefault().getPluginProperties().getString( "Plugin_id" ); //$NON-NLS-1$

    /** The pull-down image */
    public static final String IMG_PULLDOWN = "resources/icons/pulldown.gif"; //$NON-NLS-1$

    /** 
     * The various colors in use. Each color have gradients, from black to white (ie from 0 to 255). We 
     * use colors by increments of 31 :
     * <ul>
     * <li>0 : Black or no color</li>
     * <li>31 : Black Dark color (BD_[color]) for instance BD_GREEN</li>
     * <li>63 : Dark color (D_[color]) for instance D_GREEN</li>
     * <li>95 : Mid Dark Color(MD_[color]) for instance MD_GREEN</li>
     * <li>127 : Mid color (M_[color]), for instance M_GREEN</li>
     * <li>159 : Mid Light color (ML_[color]), for instance ML_GREEN</li>
     * <li>191 : Light color (L_[color]), for instance L_GREEN</li>
     * <li>224 : White Light color (WL_[color]), for instance WL_GREEN</li>
     * <li>255 : Full color ([color]), for instance GREEN</li>
     * </ul>
     **/
    // To see the world in black and white
    public static final RGB BLACK = new RGB( 0, 0, 0 );
    public static Color BLACK_COLOR;
    public static final RGB WHITE = new RGB( 255, 255, 255 );
    public static Color WHITE_COLOR;
    
    // 7 shade of greys... From dark to light, with combinaisons
    public static final RGB BD_GREY = new RGB( 31, 31, 31 );    // Black Dark grey
    public static final RGB D_GREY = new RGB( 63, 63, 63 );     // Dark grey
    public static final RGB MD_GREY = new RGB( 95, 95, 95 );    // Medium Dark grey
    public static final RGB M_GREY = new RGB( 127, 127, 127 );  // Grey
    public static final RGB ML_GREY = new RGB( 159, 159, 159 ); // Medium Light grey
    public static final RGB L_GREY = new RGB( 191, 191, 191 );  // Light grey
    public static final RGB WL_GREY = new RGB( 224, 224, 224 ); // White Light grey
    
    // Red
    public static final RGB M_RED = new RGB( 127, 0, 0 );       // Medium red
    public static final RGB ML_RED = new RGB( 159, 0, 0 );      // Medium light red
    public static final RGB RED = new RGB( 255, 0, 0 );         // Full red
    
    // Green
    public static final RGB M_GREEN = new RGB( 0, 127, 0 );     // Medium green
    public static final RGB ML_GREEN = new RGB( 0, 159, 0 );    // Medium Light green
    
    // Blue
    public static final RGB M_BLUE = new RGB( 0, 0, 127 );      // Medium blue
    public static final RGB L_BLUE = new RGB( 0, 0, 191 );      // Light blue 
    public static final RGB BLUE = new RGB( 0, 0, 255 );        // Full blue
    
    // Purple
    public static final RGB M_PURPLE = new RGB( 127, 0, 127 );  // Middle purple
    public static final RGB PURPLE = new RGB( 255, 0, 255 );    // Deep purple
    
    // Some specific colors
    public static final String IMG_INFORMATION = "resources/icons/information.gif"; //$NON-NLS-1$
}
