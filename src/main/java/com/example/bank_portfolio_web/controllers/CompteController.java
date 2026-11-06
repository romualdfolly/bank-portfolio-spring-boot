package com.example.bank_portfolio_web.controllers;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.bank_portfolio_web.models.Compte;
import com.example.bank_portfolio_web.services.BanqueService;

@Controller
@RequestMapping("/comptes")
public class CompteController {

    private final BanqueService banqueService;

    public CompteController(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    // =========================
    // CRÉATION D'UN COMPTE
    // =========================

    @PostMapping("/create")
    public String create(
        @RequestParam String numero,
        @RequestParam Long clientId,
        RedirectAttributes redirectAttributes
    ) throws SQLException {

        try {
            banqueService.openCompte(numero, clientId);

            redirectAttributes.addFlashAttribute(
                "success", "Compte bancaire créé avec succès !"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                "error", e.getMessage()
            );
        }

        // Retour vers les détails du client
        return "redirect:/clients/" + clientId;
    }

    // =========================
    // DÉTAILS D'UN COMPTE
    // =========================

    @GetMapping("/{numero}")
    public String show(
        @PathVariable String numero,
        Model model
    ) throws SQLException {

        Compte compte = banqueService.findCompte(numero);

        if (compte == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Compte bancaire introuvable"
            );
        }

        model.addAttribute("compte", compte);
        model.addAttribute("transactions", banqueService.historique(numero));

        return "pages/comptes/show";
    }
}