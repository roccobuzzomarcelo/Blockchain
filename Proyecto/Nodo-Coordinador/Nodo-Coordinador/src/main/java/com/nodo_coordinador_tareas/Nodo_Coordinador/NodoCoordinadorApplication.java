package com.nodo_coordinador_tareas.Nodo_Coordinador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@SpringBootApplication
public class NodoCoordinadorApplication {
	public static void main(String[] args) {
		SpringApplication.run(NodoCoordinadorApplication.class, args);
	}
}


