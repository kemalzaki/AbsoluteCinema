package com.oop.absolutecinema.service;

import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.SerialTV;
import com.oop.absolutecinema.exception.DataTidakDitemukanException;
import com.oop.absolutecinema.exception.JudulDuplikatException;

// import com.oop.absolutecinema.repository.FilmRepository;
// import com.oop.absolutecinema.repository.SerialTVRepository;
// import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TayanganServiceImpl implements TayanganService {

    // @Autowired
    // private FilmRepository filmRepo;
    
    // @Autowired
    // private SerialTVRepository serialTvRepo;

    @Override
    public Tayangan tambahTayangan(Tayangan tayanganBaru) {
        // 1. Validasi Judul Kosong
        if (tayanganBaru.getJudul() == null || tayanganBaru.getJudul().trim().isEmpty()) {
            throw new IllegalArgumentException("Judul tayangan wajib diisi!");
        }

        /*
        if (tayanganBaru instanceof Film) {
            if (!filmRepo.findByJudul(tayanganBaru.getJudul()).isEmpty()) {
                throw new JudulDuplikatException("Film dengan judul ini sudah terdaftar!");
            }
            return filmRepo.save((Film) tayanganBaru);
        } else if (tayanganBaru instanceof SerialTV) {
            return serialTvRepo.save((SerialTV) tayanganBaru);
        }
        */

        System.out.println("Berhasil menambahkan: " + tayanganBaru.getJudul());
        return tayanganBaru; 
    }

    @Override
    public List<Tayangan> lihatSemuaTayangan() {
        // Nanti isinya: menggabungkan findAll() dari filmRepo dan serialTvRepo
        System.out.println("Menampilkan semua katalog tayangan...");
        return null;
    }

    @Override
    public Tayangan lihatTayanganBerdasarkanId(Long id) {
        // Logika cari berdasarkan ID (Masih di-comment)
        /*
        // Contoh pencarian sederhana di filmRepo dulu
        return filmRepo.findById(id)
            .orElseThrow(() -> new DataTidakDitemukanException("Tayangan dengan ID " + id + " tidak ditemukan!"));
        */
        return null;
    }

    @Override
    public Tayangan editTayangan(Long id, Tayangan dataEdit) {
        Tayangan tayanganLama = lihatTayanganBerdasarkanId(id);

        // Nanti tinggal di-set atribut barunya (tayanganLama.setJudul(...))
        // lalu di-save ulang ke repository
        
        System.out.println("Data ID " + id + " berhasil diedit!");
        return tayanganLama;
    }

    @Override
    public void hapusTayangan(Long id) {
        Tayangan tayangan = lihatTayanganBerdasarkanId(id);
        
        // Logika hapus (Masih di-comment)
        /*
        if (tayangan instanceof Film) {
            filmRepo.delete((Film) tayangan);
        } else if (tayangan instanceof SerialTV) {
            serialTvRepo.delete((SerialTV) tayangan);
        }
        */
        System.out.println("Data dengan ID " + id + " berhasil dihapus!");
    }
}