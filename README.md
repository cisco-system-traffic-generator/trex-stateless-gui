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

##  Builds 

Latest build version 3.2 with release notes is available [here](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/releases/tag/v3.2)

You can also download installers directly from here:
 - Windows: [trex-stateless-gui3.2.exe](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/releases/download/v3.2/trex-stateless-gui3.2.exe)
 - Mac OS: [trex-stateless-gui-3.2.dmg](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/releases/download/v3.2/trex-stateless-gui-3.2.dmg) and [trex-stateless-gui-3.2.pkg](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/releases/download/v3.2/trex-stateless-gui-3.2.pkg)
 - Compiled binary: [trex-stateless-gui-3.2.tgz](https://github.com/cisco-system-traffic-generator/trex-stateless-gui/releases/download/v3.2/trex-stateless-gui-3.2.tgz)

## YouTrack

Report bug/request feature [YouTrack](http://trex-tgn.cisco.com/youtrack/issues)

##  Contact Us

Follow us on [TRex traffic generator google group](https://groups.google.com/forum/#!forum/trex-tgn)

##  Questions

You can use our [TRex forum](https://groups.google.com/forum/#!forum/trex-tgn) if you have any question.








