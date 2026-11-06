package com.example.bank_portfolio_web.controllers;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bank_portfolio_web.forms.ClientForm;
import com.example.bank_portfolio_web.models.Client;
import com.example.bank_portfolio_web.services.BanqueService;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final BanqueService banqueService;

    public ClientController(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    @GetMapping
    public String index(Model model) throws SQLException {
        model.addAttribute("clients", banqueService.listClients());
        return "pages/clients/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) throws SQLException {
        Client client = banqueService.findClient(id);
        //
        if (client == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Client introuvable");
        }

        model.addAttribute("client", client);
        model.addAttribute("comptes", banqueService.listClientComptes(id));
        return "pages/clients/show";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ClientForm form, RedirectAttributes redirectAttributes) throws SQLException {
        //
        try {
            Client client = new Client(
                    form.nom(),
                    form.prenom(),
                    form.email());

            banqueService.createClient(client);

            redirectAttributes.addFlashAttribute(
                    "success", "Client créé avec succès !");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/clients";
    }
}
