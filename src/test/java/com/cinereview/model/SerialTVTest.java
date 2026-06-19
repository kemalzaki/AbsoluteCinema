package com.cinereview.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SerialTVTest {

    @Test
    void testSetJumlahEpisodeValid() {
        SerialTV serial = new SerialTV();
        serial.setJumlahEpisode(24);
        assertEquals(24, serial.getJumlahEpisode());
    }

    @Test
    void testSerialTVAttributes() {
        SerialTV serial = new SerialTV();
        serial.setJudul("Stranger Things");
        assertEquals("Stranger Things", serial.getJudul());
    }
}
