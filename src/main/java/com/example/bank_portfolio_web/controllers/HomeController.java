package com.example.bank_portfolio_web.controllers;

import org.springframework.ui.Model;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.bank_portfolio_web.services.BanqueService;

@Controller
public class HomeController {
    
    private final BanqueService banqueService;

    public HomeController(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    @GetMapping("/")
    public String welcome(Model model) throws SQLException {
        int nbClients = this.banqueService.listClients().size();
        int nbComptes = this.banqueService.listComptes().size();
        int nbTransactions = this.banqueService.listTransactions().size();

        model.addAttribute("nbClients", nbClients);
        model.addAttribute("nbComptes", nbComptes);
        model.addAttribute("nbTransactions", nbTransactions);

        return "pages/welcome";
    }
}
