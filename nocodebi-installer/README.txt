NoCodeBI Installation Guide
===========================

Prerequisites:
- Java JDK 17 must be installed on your system
- Administrative privileges required for service installation

Installation:
1. Run the setup executable
2. Follow the installation wizard
3. After installation, use the Start Menu shortcuts to:
   - Install the Windows service
   - Import SSL certificates
   - Run the CLI tool

CLI Usage:
Open a command prompt and type:
> nocodebi [arguments]

Service Management:
The service will run automatically in the background. Use the provided shortcuts to:
- Install Service: Sets up the service to run at startup
- Uninstall Service: Removes the service

Certificate Import:
Import your SSL certificate to ensure secure connections.

Troubleshooting:
1. If Java is not detected, install JDK 17 from:
   https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

2. If service installation fails, run the install-service.bat as Administrator

3. If certificate import fails, check that:
   - Java JDK 17 is installed
   - The certificate file exists in the Certificates folder

Support:
For assistance, contact support@yourcompany.com