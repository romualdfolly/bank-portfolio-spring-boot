
package com.example.bank_portfolio_web.controllers;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bank_portfolio_web.services.BanqueService;

@Controller
@RequestMapping("/comptes")
public class TransactionController {

    private final BanqueService banqueService;

    public TransactionController(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    // =========================
    // DÉPÔT
    // =========================
    @PostMapping("/deposit")
    public String deposit(
        @RequestParam String numero,
        @RequestParam BigDecimal montant,
        RedirectAttributes redirectAttributes
    ) throws SQLException {

        try {
            banqueService.deposit(numero, montant);

            redirectAttributes.addFlashAttribute(
                "success", "Dépôt effectué avec succès !"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute(
                "error", e.getMessage()
            );
        }

        return "redirect:/comptes/" + numero;
    }

    // =========================
    // RETRAIT
    // =========================
    @PostMapping("/withdraw")
    public String withdraw(
        @RequestParam String numero,
        @RequestParam BigDecimal montant,
        RedirectAttributes redirectAttributes
    ) throws SQLException {

        try {
            banqueService.withdraw(numero, montant);

            redirectAttributes.addFlashAttribute(
                "success", "Retrait effectué avec succès !"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute(
                "error", e.getMessage()
            );
        }

        return "redirect:/comptes/" + numero;
    }

    // =========================
    // VIREMENT
    // =========================
    @PostMapping("/transfert")
    public String transfert(
        @RequestParam String numeroSource,
        @RequestParam String numeroDestination,
        @RequestParam BigDecimal montant,
        RedirectAttributes redirectAttributes
    ) throws SQLException {

        try {
            banqueService.transfert(
                numeroSource,
                numeroDestination,
                montant
            );

            redirectAttributes.addFlashAttribute(
                "success", "Virement effectué avec succès !"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute(
                "error", e.getMessage()
            );
        }

        return "redirect:/comptes/" + numeroSource;
    }
}
