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
    !define Version "@version@"
    !define Icon "studio-installer.ico"
    !define WelcomeImage "welcome_studio.bmp"
    !define HeaderImage "header_studio.bmp"
    !define OutFile "../@installer-file-name@"
    !define SourceFolder "ApacheDirectoryStudio-win32-x86_64-${Version}"
    !define JREVersion "1.5.0"
    !define INSTDIR_REG_ROOT "HKLM"
    !define INSTDIR_REG_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${Application}"

    # Variables needed for JRE detection
    Var JREPath

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
    InstallDir "$PROGRAMFILES64\${Application}"
    
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
    !insertmacro MUI_PAGE_LICENSE "${SourceFolder}\LICENSE.txt"
    
    # Components page
    #!insertmacro MUI_PAGE_COMPONENTS
    
    # Directory page
    #!define MUI_DIRECTORYPAGE_VARIABLE $APPLICATION_HOME_DIR
    !insertmacro MUI_PAGE_DIRECTORY

    # JRE directory page
    Var JAVA_HOME_DIR
    !define MUI_DIRECTORYPAGE_VARIABLE $JAVA_HOME_DIR
    !define MUI_DIRECTORYPAGE_TEXT_DESTINATION "$(javaPageTitle)"
    !define MUI_DIRECTORYPAGE_TEXT_TOP "$(javaPageMessage)"
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
# Sections
#
    # Installer section
    Section
        SetOutPath "$INSTDIR"
        
        # Opening uninstall log
        !insertmacro UNINSTALL.LOG_OPEN_INSTALL
        
        # Adding installer source files
        File /r "${SourceFolder}\*"
        File "Apache Directory Studio.ini"
        
        # Replacing Java home directory in config file
        GetFunctionAddress $R0 ReplaceJavaHome # handle to callback fn
        Push $R0
        Push "$INSTDIR\Apache Directory Studio.ini" # file to replace in
        Call ReplaceInFile
        
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
    LangString alreadyInstalledMessage ${LANG_ENGLISH} "${Application} is already installed.$\n$\nClick 'OK' to remove the previous version$\nor 'Cancel' to cancel this installation."
    LangString alreadyInstalledMessage ${LANG_FRENCH} "${Application} est déjà installé.$\n$\nCliquez sur 'OK' pour supprimer la version précédente$\nou sur 'Annuler' pour annuler cette installation."
    LangString alreadyInstalledMessage ${LANG_GERMAN} "${Application} ist bereits installiert.$\n$\nKlicke 'OK' um die frühere Version zu entfernen$\noder 'Abbruch' um die Installation abzubrechen."

    LangString javaPageTitle ${LANG_ENGLISH} "Java Home Directory"
    LangString javaPageTitle ${LANG_FRENCH} "Répertoire Java Home"
    LangString javaPageTitle ${LANG_GERMAN} "Java Home Verzeichnis"
    
    LangString javaPageMessage ${LANG_ENGLISH} "Select the Java Home directory you would like to use to run ${Application}."
    LangString javaPageMessage ${LANG_FRENCH} "Sélectionnez le répertoire Java Home que vous souhaitez utiliser pour ${Application}"
    LangString javaPageMessage ${LANG_GERMAN} "Wählen Sie das Java Home Verzeichnis, das Sie verwenden möchten, um ${Application} laufen."
    
    #
    # <onInit>
    #
    
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
        MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION "$(alreadyInstalledMessage)" \
        IDOK uninst
        Abort
      
        # Running the uninstaller
        uninst:
            ExecWait '$R0 _?=$R1'
            
        done:
            # Checking installed JRE
            Call CheckInstalledJRE
            StrCpy $JAVA_HOME_DIR "$JREPath"
        
            # Preparing the uninstall log
            !insertmacro UNINSTALL.LOG_PREPARE_INSTALL
    FunctionEnd

    #
    # </onInit>
    #
    
    #
    # <onInstSuccess>
    #
    
    Function .onInstSuccess
         # Updating the uninstall log
         !insertmacro UNINSTALL.LOG_UPDATE_INSTALL
    FunctionEnd
    
    #
    # </onInstSuccess>
    #
    
    #
    # <UN.onInit>
    #
    
    Function UN.onInit 
         # Begin uninstall
         !insertmacro UNINSTALL.LOG_BEGIN_UNINSTALL
    FunctionEnd
    
    #
    # </UN.onInit>
    #
        
    #
    # <CheckInstalledJRE>
    #
    
    Function CheckInstalledJRE
        Push "${JREVersion}"
        Call DetectJRE
        Exch $0	; Get return value from stack
        StrCmp $0 "0" End
        StrCmp $0 "-1" End
        Goto JREAlreadyInstalled
    
        JREAlreadyInstalled:
            StrCpy $JREPath "$0"
            Pop $0 # Restore $0
            Return
      
        End:
    FunctionEnd
    
    #
    # </CheckInstalledJRE>
    #
    
    #
    # <DetectJRE>
    #

    # DetectJRE. Version requested is on the stack.
    # Returns (on stack): 0 - JRE not found. -1 - JRE found but too old. Otherwise - Path to JAVA EXE
    # Stack value will be overwritten!
    Function DetectJRE
      SetRegView 64
      Exch $0	; Get version requested
            ; Now the previous value of $0 is on the stack, and the asked for version of JDK is in $0
      Push $1	; $1 = Java version string (ie 1.5.0)
      Push $2	; $2 = Javahome
      Push $3	; $3 and $4 are used for checking the major/minor version of java
      Push $4
      ;MessageBox MB_OK "Detecting JRE"
      ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
      ;MessageBox MB_OK "Read : $1"
      StrCmp $1 "" DetectTry2
      ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "JavaHome"
      ;MessageBox MB_OK "Read 3: $2"
      StrCmp $2 "" DetectTry2
      Goto GetJRE
    
    DetectTry2:
      ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
      ;MessageBox MB_OK "Detect Read : $1"
      StrCmp $1 "" NoFound
      ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"
      ;MessageBox MB_OK "Detect Read 3: $2"
      StrCmp $2 "" NoFound
    
    GetJRE:
    ; $0 = version requested. $1 = version found. $2 = javaHome
      ;MessageBox MB_OK "Getting JRE"
      IfFileExists "$2\bin\java.exe" 0 NoFound
      StrCpy $3 $0 1			; Get major version. Example: $1 = 1.5.0, now $3 = 1
      StrCpy $4 $1 1			; $3 = major version requested, $4 = major version found
      ;MessageBox MB_OK "Want $3 , found $4"
      IntCmp $4 $3 0 FoundOld FoundNew
      StrCpy $3 $0 1 2
      StrCpy $4 $1 1 2			; Same as above. $3 is minor version requested, $4 is minor version installed
      ;MessageBox MB_OK "Want $3 , found $4"
      IntCmp $4 $3 FoundNew FoundOld FoundNew
    
    NoFound:
      ;MessageBox MB_OK "JRE not found"
      Push "0"
      Goto DetectJREEnd
    
    FoundOld:
      MessageBox MB_OK "JRE too old: $3 is older than $4"
    ;  Push ${TEMP2}
      Push "-1"
      Goto DetectJREEnd
    FoundNew:
      ;MessageBox MB_OK "JRE is new: $3 is newer than $4"
    
      Push "$2"
    ;  Push "OK"
    ;  Return
       Goto DetectJREEnd
    DetectJREEnd:
        SetRegView 32
        ; Top of stack is return value, then r4,r3,r2,r1
        Exch	; => r4,rv,r3,r2,r1,r0
        Pop $4	; => rv,r3,r2,r1r,r0
        Exch	; => r3,rv,r2,r1,r0
        Pop $3	; => rv,r2,r1,r0
        Exch 	; => r2,rv,r1,r0
        Pop $2	; => rv,r1,r0
        Exch	; => r1,rv,r0
        Pop $1	; => rv,r0
        Exch	; => r0,rv
        Pop $0	; => rv
    FunctionEnd
    
    #
    # </DetectJRE>
    #

    #
    # <ReplaceInFile>
    #
    
    Function ReplaceInFile
        ;
        Exch $R0 ;file name to search in
        Exch
        Exch $R4 ;callback function handle
        Push $R1 ;file handle
        Push $R2 ;temp file name
        Push $R3 ;temp file handle
        Push $R5 ;line read
    
        GetTempFileName $R2
          FileOpen $R1 $R0 r ;file to search in
          FileOpen $R3 $R2 w ;temp file
    
    loop_read:
         ClearErrors
         FileRead $R1 $R5 ;read line
         Push $R5 ; put line on stack
         Call $R4
         Pop $R5 ; read line from stack
         IfErrors exit
         FileWrite $R3 $R5 ;write modified line
        Goto loop_read
    exit:
          FileClose $R1
          FileClose $R3
    
           SetDetailsPrint none
          Delete $R0
          Rename $R2 $R0
          Delete $R2
           SetDetailsPrint both
    
        ; pop in reverse order
        Pop $R5
        Pop $R3
        Pop $R2
        Pop $R1
        Pop $R4
        Pop $R0
    FunctionEnd
    
    #
    # </ReplaceInFile>
    #

    #
    # <ReplaceJavaHome>
    #
    
    # Replaces the '@java.home@' placeholder
    Function ReplaceJavaHome
	    Push $R1
	    Exch
	    
        Push "@java.home@" # String to find
        Push "$JAVA_HOME_DIR" # Replacement string
        Call StrReplace
    
        ; restore stack
        Exch
        Pop $R1
    FunctionEnd
    
    #
    # </ReplaceJavaHome>
    #
    
    #
    # <StrReplace>
    #
    
    Var STR_REPLACE_VAR_0
    Var STR_REPLACE_VAR_1
    Var STR_REPLACE_VAR_2
    Var STR_REPLACE_VAR_3
    Var STR_REPLACE_VAR_4
    Var STR_REPLACE_VAR_5
    Var STR_REPLACE_VAR_6
    Var STR_REPLACE_VAR_7
    Var STR_REPLACE_VAR_8
    
    Function StrReplace
      Exch $STR_REPLACE_VAR_2
      Exch 1
      Exch $STR_REPLACE_VAR_1
      Exch 2
      Exch $STR_REPLACE_VAR_0
        StrCpy $STR_REPLACE_VAR_3 -1
        StrLen $STR_REPLACE_VAR_4 $STR_REPLACE_VAR_1
        StrLen $STR_REPLACE_VAR_6 $STR_REPLACE_VAR_0
        loop:
          IntOp $STR_REPLACE_VAR_3 $STR_REPLACE_VAR_3 + 1
          StrCpy $STR_REPLACE_VAR_5 $STR_REPLACE_VAR_0 $STR_REPLACE_VAR_4 $STR_REPLACE_VAR_3
          StrCmp $STR_REPLACE_VAR_5 $STR_REPLACE_VAR_1 found
          StrCmp $STR_REPLACE_VAR_3 $STR_REPLACE_VAR_6 done
          Goto loop
        found:
          StrCpy $STR_REPLACE_VAR_5 $STR_REPLACE_VAR_0 $STR_REPLACE_VAR_3
          IntOp $STR_REPLACE_VAR_8 $STR_REPLACE_VAR_3 + $STR_REPLACE_VAR_4
          StrCpy $STR_REPLACE_VAR_7 $STR_REPLACE_VAR_0 "" $STR_REPLACE_VAR_8
          StrCpy $STR_REPLACE_VAR_0 $STR_REPLACE_VAR_5$STR_REPLACE_VAR_2$STR_REPLACE_VAR_7
          StrLen $STR_REPLACE_VAR_6 $STR_REPLACE_VAR_0
          Goto loop
        done:
      Pop $STR_REPLACE_VAR_1 ; Prevent "invalid opcode" errors and keep the
      Pop $STR_REPLACE_VAR_1 ; stack as it was before the function was called
      Exch $STR_REPLACE_VAR_0
    FunctionEnd
    
    !macro _strReplaceConstructor OUT NEEDLE NEEDLE2 HAYSTACK
      Push "${HAYSTACK}"
      Push "${NEEDLE}"
      Push "${NEEDLE2}"
      Call StrReplace
      Pop "${OUT}"
    !macroend
    
    !define StrReplace '!insertmacro "_strReplaceConstructor"'
    
    #
    # </StrReplace>
    #
