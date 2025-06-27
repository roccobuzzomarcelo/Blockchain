package com.nodo_coordinador_tareas.Nodo_Coordinador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NodoCoordinadorApplication {
	public static void main(String[] args) {
		SpringApplication.run(NodoCoordinadorApplication.class, args);
	}
}
