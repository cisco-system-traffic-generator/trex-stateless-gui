package com.exalttech.trex.remote.models.profiles;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ProfileTest {

    ObjectMapper yamlMapper = null;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    private File getYaml(String yamlFileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(yamlFileName).getFile());
    }

    @Test
    public void invalidParse() throws IOException {
        File invalidYaml = getYaml("yamls/invalid.yaml");

        expectedException.expect(JsonMappingException.class);
        Profile[] profiles = yamlMapper.readValue(invalidYaml, Profile[].class);
    }

    @Test
    public void failOnStreamParse() throws IOException {
        File streamYaml = getYaml("yamls/simpleUdpStream.yaml");

        expectedException.expect(JsonMappingException.class);
        Profile[] profiles = yamlMapper.readValue(streamYaml, Profile[].class);
    }

    @Test
    public void validParse() throws IOException {
        File profileYaml = getYaml("yamls/simpleTcpProfile.yaml");
        Profile[] parsedProfiles = yamlMapper.readValue(profileYaml, Profile[].class);

        assertEquals(parsedProfiles.length, 2);

        List<String> expectedNames = Arrays.asList("stream_tcp", "stream_tcp_2");
        List<String> resultNames = Arrays.stream(parsedProfiles).map(Profile::getName).collect(Collectors.toList());
        assertEquals(resultNames, expectedNames);

        List<Integer> expectedIds = Arrays.asList(0, 1);
        List<Integer> resultIds = Arrays.stream(parsedProfiles).map(Profile::getStreamId).collect(Collectors.toList());
        assertEquals(expectedIds, resultIds);
    }
}