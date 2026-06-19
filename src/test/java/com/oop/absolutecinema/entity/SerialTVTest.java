package com.oop.absolutecinema.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SerialTVTest {

    @Test
    void testSetJumlahEpisodeValid() {
        SerialTV serial = new SerialTV();
        serial.setTotalEpisode(24);
        assertEquals(24, serial.getTotalEpisode());
    }

    @Test
    void testSerialTVAttributes() {
        SerialTV serial = new SerialTV();
        serial.setJudul("Stranger Things");
        assertEquals("Stranger Things", serial.getJudul());
    }
}
