package com.oop.absolutecinema.service;

import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.SerialTV;
import com.oop.absolutecinema.exception.DataTidakDitemukanException;
import com.oop.absolutecinema.exception.JudulDuplikatException;
import com.oop.absolutecinema.repository.FilmRepository;
import com.oop.absolutecinema.repository.SerialTVRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TayanganServiceImpl implements TayanganService {

    @Autowired
    private FilmRepository filmRepo;
    
    @Autowired
    private SerialTVRepository serialTvRepo;

    @Override
    public Tayangan tambahTayangan(Tayangan tayanganBaru) {
        // Validasi Judul Kosong
        if (tayanganBaru.getJudul() == null || tayanganBaru.getJudul().trim().isEmpty()) {
            throw new IllegalArgumentException("Judul tayangan wajib diisi!");
        }

        // Mengecek duplikasi menggunakan method buatan Orang 4
        if (tayanganBaru instanceof Film) {
            if (!filmRepo.findByJudulContainingIgnoreCase(tayanganBaru.getJudul()).isEmpty()) {
                throw new JudulDuplikatException("Film dengan judul ini sudah terdaftar!");
            }
            return filmRepo.save((Film) tayanganBaru);
        } else if (tayanganBaru instanceof SerialTV) {
            if (!serialTvRepo.findByJudulContainingIgnoreCase(tayanganBaru.getJudul()).isEmpty()) {
                throw new JudulDuplikatException("Serial TV dengan judul ini sudah terdaftar!");
            }
            return serialTvRepo.save((SerialTV) tayanganBaru);
        }

        return tayanganBaru; 
    }

    @Override
    public List<Tayangan> lihatSemuaTayangan() {
        // Menggabungkan data dari tabel Film dan SerialTV
        List<Tayangan> semuaTayangan = new ArrayList<>();
        semuaTayangan.addAll(filmRepo.findAll());
        semuaTayangan.addAll(serialTvRepo.findAll());
        return semuaTayangan;
    }

    @Override
    public Tayangan lihatTayanganBerdasarkanId(Long id) {
        // Cari di tabel Film 
        Optional<Film> film = filmRepo.findById(id);
        if (film.isPresent()) {
            return film.get();
        }
        
        // Cari di tabel SerialTV
        Optional<SerialTV> serialTV = serialTvRepo.findById(id);
        if (serialTV.isPresent()) {
            return serialTV.get();
        }
        
        throw new DataTidakDitemukanException("Tayangan dengan ID " + id + " tidak ditemukan!");
    }

    @Override
    public Tayangan editTayangan(Long id, Tayangan dataEdit) {
        Tayangan tayanganLama = lihatTayanganBerdasarkanId(id);

        // Update atribut dasar
        tayanganLama.setJudul(dataEdit.getJudul());
        tayanganLama.setSinopsis(dataEdit.getSinopsis());
        tayanganLama.setTahunRilis(dataEdit.getTahunRilis());
        
        // Update atribut spesifik dan simpan ke database
        if (tayanganLama instanceof Film && dataEdit instanceof Film) {
            Film filmLama = (Film) tayanganLama;
            Film filmEdit = (Film) dataEdit;
            filmLama.setDurasiMenit(filmEdit.getDurasiMenit());
            filmLama.setGenre(filmEdit.getGenre());
            filmLama.setSutradara(filmEdit.getSutradara());
            return filmRepo.save(filmLama);
        } else if (tayanganLama instanceof SerialTV && dataEdit instanceof SerialTV) {
            SerialTV serialLama = (SerialTV) tayanganLama;
            SerialTV serialEdit = (SerialTV) dataEdit;
            serialLama.setJumlahMusim(serialEdit.getJumlahMusim());
            serialLama.setTotalEpisode(serialEdit.getTotalEpisode());
            serialLama.setJaringanTV(serialEdit.getJaringanTV());
            serialLama.setMasihBerjalan(serialEdit.isMasihBerjalan());
            return serialTvRepo.save(serialLama);
        }
        
        return tayanganLama;
    }

    @Override
    public void hapusTayangan(Long id) {
        Tayangan tayangan = lihatTayanganBerdasarkanId(id);
        
        // Hapus dari tabel yang sesuai
        if (tayangan instanceof Film) {
            filmRepo.delete((Film) tayangan);
        } else if (tayangan instanceof SerialTV) {
            serialTvRepo.delete((SerialTV) tayangan);
        }
    }

    @Override
    public void perbaruiDataTayangan(Tayangan tayangan) {
        if (tayangan instanceof Film) {
            filmRepo.save((Film) tayangan);
        } else if (tayangan instanceof SerialTV) {
            serialTvRepo.save((SerialTV) tayangan);
        }
    }
}