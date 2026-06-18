package com.oop.absolutecinema.service;

import com.oop.absolutecinema.DTO.ReviewDTO;
import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.Review;
import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.entity.User;
import com.oop.absolutecinema.exception.DataTidakDitemukanException;
import com.oop.absolutecinema.repository.ReviewRepository;
import com.oop.absolutecinema.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TayanganService tayanganService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User mockUser;
    private Tayangan mockTayangan;
    private ReviewDTO.Request requestDto;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        
        mockTayangan = new Film("Test Film", "Sinopsis", 2024);
        
        requestDto = new ReviewDTO.Request();
        requestDto.setUserId(1L);
        requestDto.setTayanganId(10L);
        requestDto.setSkor(4);
        requestDto.setTeks("Film yang bagus!");
    }

    @Test
    void testTambahReview_Success() {
        when(reviewRepository.existsByUserIdAndTayanganId(1L, 10L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(tayanganService.lihatTayanganBerdasarkanId(10L)).thenReturn(mockTayangan);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.tambahReview(requestDto);

        assertNotNull(result);
        assertEquals(4, result.getSkor());
        assertEquals("Film yang bagus!", result.getTeks());
        assertEquals(4.0, mockTayangan.hitungRatingRataRata());
        assertEquals(1, mockTayangan.getJumlahReviewer());

        verify(tayanganService).perbaruiDataTayangan(mockTayangan);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testTambahReview_DuplicateReview_ThrowsException() {
        when(reviewRepository.existsByUserIdAndTayanganId(1L, 10L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.tambahReview(requestDto);
        });

        assertTrue(exception.getMessage().contains("Tidak diizinkan review ganda"));
    }

    @Test
    void testTambahReview_UserNotFound_ThrowsException() {
        when(reviewRepository.existsByUserIdAndTayanganId(1L, 10L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        DataTidakDitemukanException exception = assertThrows(DataTidakDitemukanException.class, () -> {
            reviewService.tambahReview(requestDto);
        });

        assertTrue(exception.getMessage().contains("tidak ditemukan"));
    }
}
