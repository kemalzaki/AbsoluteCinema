package com.cinereview.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.cinereview.model.Film;

public class FilmTest {

    @Test
    void testSetDurasiMenitValid() {
        Film film = new Film();
        film.setDurasiMenit(120);
        assertEquals(120, film.getDurasiMenit());
    }
}
