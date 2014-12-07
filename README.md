
## Build from command line

1. Build the 'Eclipse Target Platform' first

    cd eclipse-target-platform
    mvn clean install
    cd ..

2. Build the main artifacts

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

