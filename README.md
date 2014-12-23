
## Build from command line

1. Build the 'Eclipse Target Platform' first

    cd eclipse-target-platform
    mvn -f pom-first.xml clean install
    cd ..

2. Generate MANIFEST.MF filres

    mvn -f pom-first.xml clean bundle:manifest

3. Build the main artifacts

    mvn clean install


## Setup Eclipse workspace

Recommended IDE is 'Eclipse (Luna) for RCP Developers': <http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/lunasr1>

1. Import 'Eclipse Target Platorm' project first

    * File -> Import... -> Maven -> Existing Maven Projects
    * Choose 'studio-tycho/eclipse-target-platform' as root directory
    * Only this single project is selected
    * Finish

2. Initialize target platform

    * Open the `eclipse-target-platform.target` file with the 'Target Editor'
    * In the top right corner click 'Set as Target Platform'

3. Import the main plugins

    * File -> Import... -> Maven -> Existing Maven Projects
    * Chosse 'studio-tycho' as root directory
    * All the plugins are selected
    * Finish

During import some Maven plugin connectors need to be installed, accept the installation and restart.

## Run

	* Open 'Run Configurations...' dialog
	* Select 'Eclipse Application' and create a new run configuration
	* If not on OSX, remove VM arguments -Xdock:icon and -XstartOnFirstThread on 'Arguments' tab

