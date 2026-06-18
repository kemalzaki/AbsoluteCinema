package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.service.TayanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @Autowired
    private TayanganService tayanganService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tayangans", tayanganService.lihatSemuaTayangan());
        return "index";
    }
}
