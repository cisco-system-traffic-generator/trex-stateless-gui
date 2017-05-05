# TRex Stateless GUI

TRex Stateless GUI application provides a graphical user interface for [TRex](https://trex-tgn.cisco.com/ "TRex").

## Description and main features:

TRex Stateless GUI application is a JavaFX based application.

![trex-main](https://cloud.githubusercontent.com/assets/11919839/25692283/8071acc2-30cb-11e7-9c98-ebd9bcbbce0e.png)

The application main features can be split into tree parts (TRex Management, Traffic Profile management and Packet Crafting tool):

### TRex Management

**Main Features**

This is an online part, you need to be connected to TRex in order to perform the following actions:

- Connect and manage TRex v2.23 and above.
- Ability to preview all ports along with their status and statistics.
- Ability to assign traffic to a specific port and start the traffic.
- Ability to update the bandwidth / update the multiplier options on the fly.
- Dashboard to view the port details, stream details, latency details, global statistics and charts.
- Logger view to preview server messages.
- Advanced logger view to see all the JSON requests sent to the server and server's responses.

### Traffic Profile Management

Traffic Profile Management is an offline tool, you can use it without connecting to TRex server.

![trex-stream-builder2](https://cloud.githubusercontent.com/assets/11919839/25692282/806f5954-30cb-11e7-853d-bda585ce2d9d.png)

**Main Features**

- Ability to import existing YAML profiles / export them to JSON or YAML formats
- Ability to create a profile from scratch
- Ability to create one or more streams for a given profile.
- Ability to edit existing stream properties or create new ones.
- Ability to build a stream from existing PCAP file or from scratch (using advanced Stream builder).
- Ability to export a stream to PCAP format.

![trex-stream-properties](https://cloud.githubusercontent.com/assets/11919839/25692285/8076aa92-30cb-11e7-9931-61fea6d6fb48.png)

## Advanced mode with Packet Crafting tool
Packet Crafting Tool provides a capability to create any packet from scratch or load packet from PCAP file and modify it.
![Packet Crafting Tool](https://raw.githubusercontent.com/kisel/trex-packet-editor-gui/master/docs/trex-packet-editor-main-dlg.png)

Field Engine provides an easy way to add TRex VM instructions to a stream.

<img src="https://cloud.githubusercontent.com/assets/2825175/20897636/b69ef016-bb55-11e6-8d7e-0e68c3c22311.png" width="400">

Find more information about Packet Crafting tool on [Wiki page](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/wiki#packet-editor)
 
### Dashboard

Updated dashboard provides a capability to view real-time statistics per stream. Enhacned global statistic. You can find more information about Dashboard on [Wiki page](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/wiki#dashboard)

![trex-main-dashboard](https://cloud.githubusercontent.com/assets/11919839/25692284/8073163e-30cb-11e7-8f2d-f442c44993c1.png)

### Port Management
 
Now it is easy to manage port attributes, configure port layer mode and see hardware counters. You can find more infromation on [Wiki page](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/wiki#port-management)

![Port Attributes](https://cloud.githubusercontent.com/assets/2825175/25737935/93aaad6e-31a4-11e7-8d27-b51b3dd3d8c2.png)

## Build a Native App

This section describes how to build a native application bundle (EXE for Windows and DMG for Mac).

1. `mvn clean jfx:native` (generates the native packaging using Maven).
2. Look for the the installer, DMG, or EXE in `(project)\target\jfx\native`.

This will build an installer for whatever platform you are building on. Building on a Mac will produce a Mac app. Building on a Windows PC will produce a Windows app.

## Building a Windows Installer and Native Application EXE

Building a Mac DMG or Windows EXE works straight of the box. However, Windows requires either WiX for MSI creation or Inno Setup for an EXE-based installer (both install an EXE for the JavaFX application along with a packaged JRE).

Both don't need to be installed, just whichever one gets you to your desired installer format.

### Build an EXE Installer with Inno Setup

Follow the steps below to create an EXE-based installer with Inno Setup and the JavaFX Maven plugin.

1. Download from [Inno Setup's site](http://www.jrsoftware.org/isinfo.php)
2. Install it (I defaulted most of the installation options)
3. Add `C:\Program Files (x86)\Inno Setup 5` to your system `Path` variable.

Once done, run `mvn jfx:native` to create an EXE based installer at `(project)\target\jfx\native`.

By running the EXE installer, it installs the JavaFX application to the user's local app data folder, as well as an uninstaller entry in the `Programs and Features` control panel. The application is also launched immediately following installation.

### Build an MSI with WiX

To install WiX and have the JavaFX Maven plugin use it during a build, do the following:

1. Download from [WiX's site](http://wixtoolset.org/)
2. Install it.
3. Add `C:\Program Files (x86)\WiX Toolset v3.10\bin` to your system `Path` variable.

Once that is complete, `mvn jfx:native` will create an MSI file in `(project)\target\jfx\native\bundles`.

Running the MSI installs the application to `C:\Program Files (x86)\(project name)` without prompt. Additionally, it creates an uninstaller listing in the `Programs and Features` control panel.

## YouTrack

Report bug/request feature [YouTrack](http://trex-tgn.cisco.com/youtrack/issues)

##  Contact Us

Follow us on [TRex traffic generator google group](https://groups.google.com/forum/#!forum/trex-tgn)

##  Questions

You can use our [TRex forum](https://groups.google.com/forum/#!forum/trex-tgn) if you have any question.

##  Installer 

###  Windows Installer 

You can find our Windows Beta installer in the Release Tab.

###  Mac OS Installer 

You can find our Mac Beta installer in the Release Tab.

###  Linux RPM Installer 

You can find our Linux RPM Beta installer in the Release Tab.

## Automation

TRex has infrastructure for two kinds of automation, UI automation and JUnit/TestNG automation. The sections below provide more information about each type:

### UI Automation:
TRex uses  [testFX](https://github.com/TestFX/TestFX "testFX") version 4  library which is  based on JUnit testing framework and supports Java 8.

The tests are located in :

`src/test/java/com/exalttech/trex/ui`

Tests can be run using maven command:

`mvn clean test -P Run-UI-TRex-Suite`


### JUnit/TestNG Automation

TRex integrates uses [TestNG](http://testng.org/doc/index.html "TestNG") framework.

The tests are located in :

`src/test/java/com/exalttech/trex/simulator`

Tests can be run using maven command:

`mvn clean test -P Run-TRex-Suite`

As part of the unit tests use TRex Simulator (stl-sim), you need to make sure that it is accessible from the machine used for testing.










