package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.service.TayanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tayangan")
public class TayanganController {

    @Autowired
    private TayanganService tayanganService;

    @GetMapping
    public List<Tayangan> tampilkanKatalog() {
        return tayanganService.lihatSemuaTayangan();
    }

    @GetMapping("/{id}")
    public Tayangan tampilkanDetail(@PathVariable Long id) {
        return tayanganService.lihatTayanganBerdasarkanId(id);
    }

    @PostMapping
    public Tayangan tambahTayangan(@RequestBody Tayangan tayanganBaru) {
        return tayanganService.tambahTayangan(tayanganBaru);
    }

    @PostMapping("/{id}")
    public Tayangan editTayangan(@PathVariable Long id, @RequestBody Tayangan dataEdit) {
        return tayanganService.editTayangan(id, dataEdit);
    }

    @DeleteMapping("/{id}")
    public String hapusTayangan(@PathVariable Long id) {
        tayanganService.hapusTayangan(id);
        return "Tayangan berhasil dihapus";
    }
}