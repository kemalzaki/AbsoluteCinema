package com.cinereview.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TayanganTest {

    @Test
    void testTayanganIdentitas() {
        Film tayangan = new Film(); 
        tayangan.setJudul("Inception");
        tayangan.setGenre("Sci-Fi");
        
        assertEquals("Inception", tayangan.getJudul());
        assertEquals("Sci-Fi", tayangan.getGenre());
    }
}
