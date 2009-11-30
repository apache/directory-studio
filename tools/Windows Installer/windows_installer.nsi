#
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
#

#
# Constants and variables
#
    !define Application "Apache Directory Studio"
    !define Version "1.5.1.v20091130"
    !define Icon "utils\studio-installer.ico"
    !define WelcomeImage "utils\welcome_studio.bmp"
    !define HeaderImage "utils\header_studio.bmp"
    !define OutFile "ApacheDirectoryStudio-win32-${Version}.exe"
    !define SourceFolder "release"
    !define INSTDIR_REG_ROOT "HKLM"
    !define INSTDIR_REG_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${Application}"

#
# Modules inclusions
#
    # Modern UI module
    !include "MUI.nsh"
    
    # Uninstall log module
    !include AdvUninstLog.nsh
    
#
# Configuration
#
    # Name of the application
    Name "${Application}"
    
    # Output installer file
    OutFile "${OutFile}"
    
    # Default install directory
    InstallDir "$PROGRAMFILES\${Application}"
    
    # Branding text
    BrandingText "${Application} - ${Version}"

    # Activating XPStyle
    XPStyle on

    # Installer icon
    !define MUI_ICON "${Icon}"
    
    # Uninstaller icon
    !define MUI_UNICON "${Icon}"
    
    # Welcome image
    !define MUI_WELCOMEFINISHPAGE_BITMAP "${WelcomeImage}"
    
    # Activating header image
    !define MUI_HEADERIMAGE
    !define MUI_HEADERIMAGE_BITMAP "${HeaderImage}"

    # Activating small description for the components page
    !define MUI_COMPONENTSPAGE_SMALLDESC
    
    # Activating a confirmation when aborting the installation
    !define MUI_ABORTWARNING
    
    # Unattended uninstallation
    !insertmacro UNATTENDED_UNINSTALL

#
# Pages
#
    #
    # Installer pages
    #
    
    # Welcome page
    !insertmacro MUI_PAGE_WELCOME
    
    # License page
    !insertmacro MUI_PAGE_LICENSE "release\LICENSE.txt"
    
    # Components page
    #!insertmacro MUI_PAGE_COMPONENTS
    
    # Directory page
    #!define MUI_DIRECTORYPAGE_VARIABLE $APPLICATION_HOME_DIR
    !insertmacro MUI_PAGE_DIRECTORY
    
    # Installation page
    !insertmacro MUI_PAGE_INSTFILES
    
    # Finish page
    !insertmacro MUI_PAGE_FINISH
    
    #
    # Uninstaller pages
    #
    
    # Confirmation page
    !insertmacro MUI_UNPAGE_CONFIRM
    
    # Uninstallation page
    !insertmacro MUI_UNPAGE_INSTFILES

#
# Languages (the first one is the default one)
#
    !insertmacro MUI_LANGUAGE "English"
    !insertmacro MUI_LANGUAGE "French"
    !insertmacro MUI_LANGUAGE "German"
    
#
# Functions
#
    # Internationalized strings
    LangString message ${LANG_ENGLISH} "${Application} is already installed.$\n$\nClick 'OK' to remove the previous version$\nor 'Cancel' to cancel this installation."
    LangString message ${LANG_FRENCH} "${Application} est déjà installé.$\n$\nCliquez sur 'OK' pour supprimer la version précédente$\nou sur 'Annuler' pour annuler cette installation."
    LangString message ${LANG_GERMAN} "${Application} ist bereits installiert.$\n$\nKlicke 'OK' um die frühere Version zu entfernen$\noder 'Abbruch' um die Installation abzubrechen."

    # onInit function
    Function .onInit
        # Preventing the window to close automatically at the end of the (un)installation
        SetAutoClose false
    
        # Verifying if the application is already installed
        ReadRegStr $R0 "${INSTDIR_REG_ROOT}" "${INSTDIR_REG_KEY}" "UninstallString"
        StrCmp $R0 "" done
        
        # Getting install location
        ReadRegStr $R1 "${INSTDIR_REG_ROOT}" "SOFTWARE\${Application}" "InstallDir"
        StrCmp $R1 "" done
    
        # The application is already installed
        # Asking before running the uninstaller
        MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION "$(message)" \
        IDOK uninst
        Abort
      
        # Running the uninstaller
        uninst:
            ExecWait '$R0 _?=$R1'
            
        done:
            # Preparing the uninstall log
            !insertmacro UNINSTALL.LOG_PREPARE_INSTALL
    FunctionEnd

    # onInstSuccess function
    Function .onInstSuccess
         # Updating the uninstall log
         !insertmacro UNINSTALL.LOG_UPDATE_INSTALL
    FunctionEnd
    
    # UN.onInit function
    Function UN.onInit 
         # Begin uninstall
         !insertmacro UNINSTALL.LOG_BEGIN_UNINSTALL
    FunctionEnd

#
# Sections
#
    # Installer section
    Section
        SetOutPath "$INSTDIR"
        
        # Opening uninstall log
        !insertmacro UNINSTALL.LOG_OPEN_INSTALL
        
        # Adding installer source files
        File /r "${SourceFolder}\*"
        
        # Storing install location
        WriteRegStr "${INSTDIR_REG_ROOT}" "SOFTWARE\${Application}" "InstallDir" $INSTDIR

        # Creating directories in the start menu
        CreateDirectory "$SMPROGRAMS\Apache Directory Studio"
        
        # Creating a shortcut to the application
        CreateShortCut "$SMPROGRAMS\Apache Directory Studio\Apache Directory Studio.lnk" "$INSTDIR\Apache Directory Studio.exe" "" "$INSTDIR\Apache Directory Studio.exe" 0
        
        # Creating an internet shortcut to the documentation
        WriteINIStr "$SMPROGRAMS\Apache Directory Studio\Documentation.url" "InternetShortcut" "URL" "http://directory.apache.org/studio/users-guide.html"

        # Configuring registries for the uninstaller
        WriteRegStr "${INSTDIR_REG_ROOT}" "${INSTDIR_REG_KEY}" "DisplayName" "${Application} - (remove only)"
        WriteRegStr "${INSTDIR_REG_ROOT}" "${INSTDIR_REG_KEY}" "DisplayIcon" "$INSTDIR\uninstall.exe"
        WriteRegStr "${INSTDIR_REG_ROOT}" "${INSTDIR_REG_KEY}" "UninstallString" '"$INSTDIR\uninstall.exe"'
        WriteRegDWORD "${INSTDIR_REG_ROOT}" "${INSTDIR_REG_KEY}" "NoModify" "1"
        WriteRegDWORD "${INSTDIR_REG_ROOT}" "${INSTDIR_REG_KEY}" "NoRepair" "1"

        # Creating the uninstaller
        WriteUninstaller "$INSTDIR\Uninstall.exe"
        
        # Creating a shortcut to the uninstaller
        CreateShortCut "$SMPROGRAMS\Apache Directory Studio\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
        
        # Closing uninstall log
        !insertmacro UNINSTALL.LOG_CLOSE_INSTALL
    SectionEnd
    
    # Uninstaller section
    Section Uninstall
        # Removing installed files (one line per directory is mandatory)
        !insertmacro UNINSTALL.LOG_UNINSTALL "$INSTDIR"
        !insertmacro UNINSTALL.LOG_UNINSTALL "$INSTDIR\configuration"
        !insertmacro UNINSTALL.LOG_UNINSTALL "$INSTDIR\plugins"
        !insertmacro UNINSTALL.LOG_UNINSTALL "$INSTDIR\features"
        Delete "$INSTDIR\Uninstall.exe"
         
        # Finishing uninstall
        !insertmacro UNINSTALL.LOG_END_UNINSTALL
        
        # Remove shortcuts and folders in the start menu
        RMDir /r "$SMPROGRAMS\Apache Directory Studio"
        
        # Removing registry keys
        DeleteRegKey "${INSTDIR_REG_ROOT}" "${INSTDIR_REG_KEY}"
    SectionEnd
