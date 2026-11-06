package com.example.bank_portfolio_web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.bank_portfolio_web.database.DatabaseConnection;

@SpringBootApplication
public class BankPortfolioWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankPortfolioWebApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			try {
				// Création du fichier schema.sql si inexistant
				DatabaseConnection.createSchema();

				// Connexion et initialisation de la base
				try (Connection connection = DatabaseConnection.connect()) {
					System.out.println("Connexion SQLite réussie !");
					DatabaseConnection.initializeDatabase(connection);
				}

			} catch (SQLException | IOException e) {
				System.err.println("Erreur : " + e.getMessage());

				throw new IllegalStateException(
					"Initialisation de la base de données impossible", e
				);
			}
		};
	}
}
