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
package org.apache.directory.studio.apacheds.experimentations;


/**
 * This interface stores all the constants used in the plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ApacheDsPluginConstants
{
    // -------
    // IMAGES
    // -------
    public static final String IMG_SERVER_INSTANCE = "resources/icons/server-instance.png";
    public static final String IMG_SERVER_NEW = "resources/icons/server_new.gif";
    public static final String IMG_SERVER_NEW_WIZARD = "resources/icons/server_new_wizard.png";
    public static final String IMG_SERVER = "resources/icons/server.gif";
    public static final String IMG_SERVER_STARTED = "resources/icons/server_started.gif";
    public static final String IMG_SERVER_STARTING1 = "resources/icons/server_starting1.gif";
    public static final String IMG_SERVER_STARTING2 = "resources/icons/server_starting2.gif";
    public static final String IMG_SERVER_STARTING3 = "resources/icons/server_starting3.gif";
    public static final String IMG_SERVER_STOPPED = "resources/icons/server_stopped.gif";
    public static final String IMG_SERVER_STOPPING1 = "resources/icons/server_stopping1.gif";
    public static final String IMG_SERVER_STOPPING2 = "resources/icons/server_stopping2.gif";
    public static final String IMG_SERVER_STOPPING3 = "resources/icons/server_stopping3.gif";
    public static final String IMG_RUN = "resources/icons/run.gif";
    public static final String IMG_STOP = "resources/icons/stop.gif";

    // -------
    // ACTIONS
    // -------
    public static final String ACTION_NEW_SERVER = "org.apache.directory.studio.apacheds.experimentations.actions.newServerAction";
    public static final String ACTION_SERVER_INSTANCE_RUN = "org.apache.directory.studio.apacheds.experimentations.actions.serverInstanceRunAction";
    public static final String ACTION_SERVER_INSTANCE_STOP = "org.apache.directory.studio.apacheds.experimentations.actions.serverInstanceStopAction";
    public static final String ACTION_PROPERTIES = "org.apache.directory.studio.apacheds.experimentations.actions.propertiesAction";
    public static final String ACTION_OPEN = "org.apache.directory.studio.apacheds.experimentations.actions.openAction";
    public static final String ACTION_DELETE = "org.apache.directory.studio.apacheds.experimentations.actions.deleteAction";

    // -----------
    // PREFERENCES
    // -----------
    /** The Preference ID for the Debug Font setting */
    public static final String PREFS_COLORS_AND_FONTS_DEBUG_FONT = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.debugFont";
    /** The Preference ID for the Debug Color setting */
    public static final String PREFS_COLORS_AND_FONTS_DEBUG_COLOR = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.debugColor";

    /** The Preference ID for the Info Font setting */
    public static final String PREFS_COLORS_AND_FONTS_INFO_FONT = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.infoFont";
    /** The Preference ID for the Info Color setting */
    public static final String PREFS_COLORS_AND_FONTS_INFO_COLOR = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.infoColor";

    /** The Preference ID for the Warn Font setting */
    public static final String PREFS_COLORS_AND_FONTS_WARN_FONT = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.warnFont";
    /** The Preference ID for the Warn Color setting */
    public static final String PREFS_COLORS_AND_FONTS_WARN_COLOR = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.warnColor";

    /** The Preference ID for the Error Font settings */
    public static final String PREFS_COLORS_AND_FONTS_ERROR_FONT = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.errorFont";
    /** The Preference ID for the Error Color setting */
    public static final String PREFS_COLORS_AND_FONTS_ERROR_COLOR = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.errorColor";

    /** The Preference ID for the Fatal Font setting */
    public static final String PREFS_COLORS_AND_FONTS_FATAL_FONT = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.fatalFont";
    /** The Preference ID for the Fatal Color setting */
    public static final String PREFS_COLORS_AND_FONTS_FATAL_COLOR = "org.apache.directory.studio.apacheds.experimentations.prefs.colorAndFonts.fatalColor";
}
