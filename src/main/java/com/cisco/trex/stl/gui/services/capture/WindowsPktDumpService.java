package com.cisco.trex.stl.gui.services.capture;

import com.cisco.trex.stateless.model.capture.CapturedPackets;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

import static com.sun.jna.platform.win32.WinBase.PIPE_READMODE_MESSAGE;
import static com.sun.jna.platform.win32.WinBase.PIPE_TYPE_MESSAGE;
import static com.sun.jna.platform.win32.WinBase.PIPE_WAIT;

public class WindowsPktDumpService implements PktDumpService {

    private static Logger LOG = Logger.getLogger(WindowsPktDumpService.class);

    private final String pipeName = "\\\\.\\pipe\\WiresharkPipe_" + System.currentTimeMillis();

    private HANDLE pipeHandler;

    private Process wiresharkProcess;

    private boolean initalized = false;

    private boolean globalHeaderWrote = false;

    @Override
    public Process init(String wireSharkExecPath) throws PktDumpServiceInitException {
        createPipe();
        try {
            wiresharkProcess = new ProcessBuilder(new String[]{wireSharkExecPath, "-k", "-i", pipeName}).start();
            Kernel32.INSTANCE.ConnectNamedPipe(pipeHandler, null);
            initalized = true;
            return wiresharkProcess;
        } catch (IOException e) {
            LOG.error("Unable to start WireShark", e);
            destroyPipe();
            throw new PktDumpServiceInitException(e.getMessage());
        }
    }

    @Override
    public void dump(CapturedPackets capturedPkts) throws PktDumpServiceException {
        if (!initalized) {
            throw new PktDumpServiceException("Service is not initialized yet.");
        }
        if (!globalHeaderWrote) {
            dumpGlobalHeader();
        }

        capturedPkts.getPkts().forEach(pkt -> {
            byte[] decodedPkt = Base64.getDecoder().decode(pkt.getBinary());

            byte[] pktBin = joinArray(
                    // Packet Header
                    ByteBuffer.allocate(4).putInt((int) pkt.getTimeStamp()).array(),
                    ByteBuffer.allocate(4).putInt(0).array(),
                    ByteBuffer.allocate(4).putInt(decodedPkt.length).array(),
                    ByteBuffer.allocate(4).putInt(decodedPkt.length).array(),
                    // Packet data
                    decodedPkt
            );

            if (!Kernel32.INSTANCE.WriteFile(pipeHandler, pktBin, pktBin.length, new IntByReference(0), null)) {
                LOG.error("Unable to dump pkt.");
            }
            if (!Kernel32.INSTANCE.FlushFileBuffers(pipeHandler)) {
                LOG.error("Unable to flush pkts.");
            }
        });
    }

    /**
     * @see <a href="https://wiki.wireshark.org/Development/LibpcapFileFormat#File_Format">PCAP format description</a>
     * @throws PktCaptureServiceException
     */

    private void dumpGlobalHeader() throws PktDumpServiceException {
        byte[] pktHeader = joinArray(
                ByteBuffer.allocate(4).putInt(0xA1B2C3D4).array(), // Magic string
                ByteBuffer.allocate(4).putInt(0x02000400).array(), // Major and Minor version of the current PCAP format
                ByteBuffer.allocate(4).putInt(0x00000000).array(), // GMT to local correction
                ByteBuffer.allocate(4).putInt(0x00000000).array(), // accuracy of timestamps
                ByteBuffer.allocate(4).putInt(65536).array(),      // Snaplen
                ByteBuffer.allocate(4).putInt(0x00000001).array()  // Data link type(Ethernet)
        );

        boolean headerResult = Kernel32.INSTANCE.WriteFile(
                                   pipeHandler,
                                   pktHeader,
                                   pktHeader.length,
                                   new IntByReference(0),
                                   null
                               );
        if (!headerResult) {
            throw new PktDumpServiceException("Unable to write global header to the pipe. Cause error code:" + Kernel32.INSTANCE.GetLastError());
        }
        boolean flushResult = Kernel32.INSTANCE.FlushFileBuffers(pipeHandler);
        if (!flushResult) {
            throw new PktDumpServiceException("Unable to flush message into the pipe. Cause error code:" + Kernel32.INSTANCE.GetLastError());
        }
        globalHeaderWrote = true;
    }

    private byte[] joinArray(byte[]... arrays) {
        int length = 0;
        for(byte[] array: arrays) {
            length += array.length;
        }

        final byte[] result = new byte[length];

        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }

    @Override
    public void close() {
        if (wiresharkProcess != null) {
            wiresharkProcess.destroy();
        }
        destroyPipe();
    }

    private void createPipe() {
        pipeHandler = Kernel32.INSTANCE.CreateNamedPipe(pipeName,
                WinBase.PIPE_ACCESS_OUTBOUND,        // dwOpenMode
                PIPE_TYPE_MESSAGE | PIPE_WAIT | PIPE_READMODE_MESSAGE,    // dwPipeMode
                1,    // nMaxInstances,
                65536,    // nOutBufferSize,
                65536,    // nInBufferSize,
                1000,    // nDefaultTimeOut,
                null);
    }

    private void destroyPipe() {
        if (pipeHandler != null) {
            boolean result = Kernel32.INSTANCE.CloseHandle(pipeHandler);
            if(!result) {
                LOG.error("Unable to close pipe: " + pipeName);
            }
        }
    }
}
