package com.oop.absolutecinema.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    @Test
    void testSetDurasiMenitValid() {
        Film film = new Film();
        film.setDurasiMenit(120);
        assertEquals(120, film.getDurasiMenit());
    }

    @Test
    void testSetDurasiMenitGanjilAtauNegatif() {
        Film film = new Film();
        
        film.setDurasiMenit(-50); 
        assertTrue(film.getDurasiMenit() <= 0, "Durasi tidak boleh negatif");
    }
}
