package com.cisco.trex.stl.gui.services.capture;

import com.cisco.trex.stateless.model.capture.CapturedPackets;
import org.apache.log4j.Logger;
import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class UnixPktDumpService implements PktDumpService {

    private static Logger LOG = Logger.getLogger(UnixPktDumpService.class);

    private final String pipeName = "/var/tmp/WiresharkPipe_" + System.currentTimeMillis();

    private Process wiresharkProcess;

    private boolean initalized = false;

    private boolean globalHeaderWrote = false;

    PcapHandle handle;

    PcapDumper dumper;
    private Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public Process init(String wireSharkExecPath) throws PktDumpServiceInitException {
        createPipe();
        try {
            wiresharkProcess = new ProcessBuilder(new String[]{wireSharkExecPath, "-k", "-i", pipeName}).start();
            handle = Pcaps.openDead(DataLinkType.EN10MB, 65536);
            dumper = handle.dumpOpen(pipeName);
            initalized = true;
            return wiresharkProcess;
        } catch (IOException e) {
            LOG.error("Unable to start WireShark", e);
            destroyPipe();
            throw new PktDumpServiceInitException(e.getMessage());
        } catch (PcapNativeException | NotOpenException e) {
            LOG.error("Unable to create dumper or open handler.", e );
            throw new PktDumpServiceInitException(e.getMessage());
        }
    }

    @Override
    public void dump(CapturedPackets capturedPkts) throws PktDumpServiceException {
        capturedPkts.getPkts().stream()
                .map(capturedPkt -> toEtherPkt(decoder.decode(capturedPkt.getBinary())))
                .forEach(ethPkt -> {
                    try {
                        dumper.dump(ethPkt);
                        dumper.flush();
                    } catch (NotOpenException | PcapNativeException e) {
                        LOG.error("Unable to dump pkt.", e);
                    }
                });
    }
    private EthernetPacket toEtherPkt(byte[] pkt) {
        EthernetPacket ethPkt = null;
        try {
            ethPkt = EthernetPacket.newPacket(pkt, 0, pkt.length);
        } catch (IllegalRawDataException e) {
            LOG.error("Save PCAP. Unable to parse pkt from server.", e);
            return null;
        }
        return ethPkt;
    }
    @Override
    public void close() {
        if(!initalized) {
            return;
        }
        dumper.close();
        handle.close();
        wiresharkProcess.destroy();
        destroyPipe();
        initalized = false;
    }

    private void destroyPipe() {
        String[] command = new String[] {"unlink", pipeName};
        try {
            Process unlink = new ProcessBuilder(command).inheritIO().start();
            unlink.waitFor();
        } catch (InterruptedException | IOException e) {
            LOG.error("Unable to delete a pipe due to: " + e.getMessage(), e);
            return;
        }
    }

    private void createPipe() throws PktDumpServiceInitException {
        File fifo = new File(pipeName);
        if (!fifo.exists()) {
            String[] command = new String[] {"mkfifo", pipeName};
            try {
                Process mkfifo = new ProcessBuilder(command).inheritIO().start();
                mkfifo.waitFor();
            } catch (InterruptedException | IOException e) {
                LOG.error("Unable to create a pipe due to: " + e.getMessage(), e);
                return;
            }
        }
    }
}
