package com.exalttech.trex.util;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TrafficProfileTest {

    private TrafficProfile trafficProfile;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private File getYaml(String yamlFileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(yamlFileName).getFile());
    }

    @Before
    public void setUp() {
        trafficProfile = new TrafficProfile();
    }

    @Test
    public void getTrafficProfile_GuiProfiles() throws IOException {
        File yamlFile = getYaml("yamls/Profile_burst_two_streams_chain.yaml");

        Profile[] parsedProfiles = this.trafficProfile.getTrafficProfile(yamlFile);

        assertEquals(2, parsedProfiles.length);
        assertEquals("burst", parsedProfiles[0].getName());
        assertEquals("burst2", parsedProfiles[0].getNext());

        assertEquals("burst2", parsedProfiles[1].getName());
        assertEquals("-1", parsedProfiles[1].getNext());

        String expectedVmRaw = "{\"split_by_var\":\"\",\"instructions\":[],\"cache_size\":5000}";
        assertEquals(expectedVmRaw, parsedProfiles[0].getStream().getVmRaw());
        assertEquals(expectedVmRaw, parsedProfiles[1].getStream().getVmRaw());
    }

    @Test
    public void getTrafficProfile_NativeStreams() throws IOException {
        File yamlFile = getYaml("yamls/Native_burst_three_streams_chain.yaml");

        Profile[] parsedProfiles = this.trafficProfile.getTrafficProfile(yamlFile);

        assertEquals(3, parsedProfiles.length);
        assertEquals("S0", parsedProfiles[0].getName());
        assertEquals("S1", parsedProfiles[0].getNext());

        assertEquals("S1", parsedProfiles[1].getName());
        assertEquals("S2", parsedProfiles[1].getNext());

        assertEquals("S2", parsedProfiles[2].getName());
        assertEquals("-1", parsedProfiles[2].getNext());

        String expectedVmRaw = "{\"instructions\":[]}";

        for (Profile p : parsedProfiles) {
            assertEquals(p.getStream().getVmRaw(), expectedVmRaw);
        }
    }

    @Test
    public void getTrafficProfile_NativeStreamsNoNames() throws IOException {
        File yamlFile = getYaml("yamls/Native_burst_three_streams_chain_no_names.yaml");

        Profile[] parsedProfiles = this.trafficProfile.getTrafficProfile(yamlFile);

        assertEquals(3, parsedProfiles.length);
        assertEquals("Stream_0", parsedProfiles[0].getName());
        assertEquals("Stream_1", parsedProfiles[1].getName());
        assertEquals("Stream_2", parsedProfiles[2].getName());
    }

    @Test
    public void getTrafficProfile_validProfiles() {
        Stream<File> validYamls = Stream.of("yamls/simpleTcpProfile.yaml", "yamls/simpleUdpStream.yaml").map(this::getYaml);

        validYamls.forEach(yamlFile -> {
            try {
                Profile[] parsedProfiles = this.trafficProfile.getTrafficProfile(yamlFile);
            } catch (IOException e) {
                fail();
            }
        });
    }

    @Test
    public void getTrafficProfile_invalidProfile() throws IOException {
        File invalidYaml = getYaml("yamls/invalid.yaml");

        expectedException.expect(IOException.class);
        Profile[] parsedProfile = this.trafficProfile.getTrafficProfile(invalidYaml);
    }
}