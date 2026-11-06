package com.example.bank_portfolio_web.services;



import com.example.bank_portfolio_web.database.DatabaseConnection;

import com.example.bank_portfolio_web.models.Client;
import com.example.bank_portfolio_web.models.Compte;
import com.example.bank_portfolio_web.models.Transaction;
import com.example.bank_portfolio_web.repositories.ClientRepository;
import com.example.bank_portfolio_web.repositories.CompteRepository;
import com.example.bank_portfolio_web.repositories.TransactionRepository;
import com.example.bank_portfolio_web.types.OperationType;


import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import org.springframework.stereotype.Service;

@Service 
public class BanqueService {

    private final ClientRepository clientRepository = new ClientRepository();
    private final CompteRepository compteRepository = new CompteRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();

    // =========================
    // CLIENTS
    // =========================
    public Client createClient(Client client) throws SQLException {
        if (clientRepository.emailExists(client.email())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        long id = clientRepository.save(client);

        return new Client(id, client.nom(), client.prenom(), client.email());
    }

    public Client findClient(long id) throws SQLException {
        return clientRepository.find(id);
    }

    public List<Client> listClients() throws SQLException {
        return clientRepository.findAll();
    }

    // =========================
    // COMPTES
    // =========================
    public void openCompte(String numero, long clientId)
            throws SQLException {

        if (clientRepository.find(clientId) == null) {
            throw new IllegalArgumentException("Client inexistant");
        }

        if (compteRepository.find(numero) != null) {
            throw new IllegalArgumentException(
                "Ce numéro de compte existe déjà"
            );
        }

        Compte compte = new Compte(numero, clientId);
        compteRepository.save(compte);
    }

    public Compte findCompte(String numero) throws SQLException {
        return compteRepository.find(numero);
    }

    public List<Compte> listClientComptes(long clientId)
            throws SQLException {

        return compteRepository.findByClientId(clientId);
    }

    public List<Compte> listComptes() throws SQLException {
        return compteRepository.findAll();
    }

    public BigDecimal consultBalance(String numero)
            throws SQLException {

        Compte compte = compteRepository.find(numero);

        if (compte == null) {
            throw new IllegalArgumentException("Compte inexistant");
        }

        return compte.getSolde();
    }

    // =========================
    // DEPOT
    // =========================

    public void deposit(String numero, BigDecimal montant) throws SQLException {

        try (Connection connection = DatabaseConnection.connect()) {
            connection.setAutoCommit(false);

            try {
                // find compte, update montant, save in database
                Compte compte = compteRepository.find(connection, numero);

                if (compte == null) {
                    throw new IllegalArgumentException("Compte inexistant");
                }

                compte.deposit(montant);
                compteRepository.updateSolde(connection, numero, compte.getSolde());

                // save transaction
                transactionRepository.save(connection, new Transaction(
                    OperationType.DEPOT,
                    montant,
                    null,
                    numero
                ));

                connection.commit();

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // =========================
    // RETRAIT
    // =========================
    public void withdraw(String numero, BigDecimal montant) throws SQLException {

        try (Connection connection = DatabaseConnection.connect()) {
            connection.setAutoCommit(false);

            try {
                // find compte, update montant, save in database
                Compte compte = compteRepository.find(connection, numero);

                if (compte == null) {
                    throw new IllegalArgumentException("Compte inexistant");
                }

                compte.withdraw(montant);
                compteRepository.updateSolde(connection, numero, compte.getSolde());

                // save transaction
                transactionRepository.save(connection, new Transaction(
                    OperationType.RETRAIT,
                    montant,
                    numero,
                    null
                ));

                connection.commit();

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // =========================
    // VIREMENT
    // =========================

    public void transfert(String numeroSource, String numeroDestination, BigDecimal montant) throws SQLException {

        if (numeroSource == null ||
            numeroDestination == null ||
            numeroSource.equals(numeroDestination)) {

            throw new IllegalArgumentException(
                    "Comptes source et destination invalides"
            );
        }

        try (Connection connection = DatabaseConnection.connect()) {
            connection.setAutoCommit(false);

            try {
                Compte sender = compteRepository.find(connection, numeroSource);
                Compte receiver = compteRepository.find(connection, numeroDestination);

                if (sender == null || receiver == null) {
                    throw new IllegalArgumentException("Ni l'envoyeur ni le receveur ne peut être null");
                }

                sender.withdraw(montant);
                receiver.deposit(montant);

                compteRepository.updateSolde(connection, numeroSource, sender.getSolde());
                compteRepository.updateSolde(connection, numeroDestination, receiver.getSolde());

                // save transaction
                transactionRepository.save(connection, new Transaction(
                    OperationType.VIREMENT,
                    montant,
                    numeroSource,
                    numeroDestination
                ));

                connection.commit();

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // =========================
    // HISTORIQUE
    // =========================

    public List<Transaction> historique(String numero)
            throws SQLException {

        return transactionRepository.findByCompte(numero);
    }

    public List<Transaction> listTransactions() throws SQLException {
        return transactionRepository.findAll();
    }
}