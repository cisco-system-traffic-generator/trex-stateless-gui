package com.exalttech.trex.remote.models.profiles;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class StreamTest {

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
        Stream[] streams = yamlMapper.readValue(invalidYaml, Stream[].class);
    }

    @Test
    public void validParse() throws IOException {
        File streamYaml = getYaml("yamls/simpleUdpStream.yaml");
        Stream[] streams = yamlMapper.readValue(streamYaml, Stream[].class);

        assertEquals(streams.length, 1);
        Stream parsedStream = streams[0];
        assertTrue(parsedStream.isEnabled());
        assertEquals(parsedStream.getActionCount(), 8);
        assertEquals(parsedStream.getFlags(), 3);
        assertTrue(parsedStream.getFlowStats().isEnabled());
        assertEquals(parsedStream.getIsg(), 1.5, 0.001);
        assertEquals(parsedStream.getMode().getType(), "continuous");
        assertEquals(parsedStream.getMode().getRate().getType(), "pps");
        assertEquals(parsedStream.getMode().getRate().getValue(), 28, 0.001);
        assertNotNull(parsedStream.getPacket().getBinary());
        assertEquals(parsedStream.getPacket().getMeta(), "");
        assertTrue(parsedStream.isSelfStart());
        assertTrue(parsedStream.getAdditionalProperties().containsKey("vm"));
        Map<String, Object> vm = (Map<String, Object>) parsedStream.getAdditionalProperties().get("vm");
        assertTrue(vm.containsKey("instructions"));
    }
}