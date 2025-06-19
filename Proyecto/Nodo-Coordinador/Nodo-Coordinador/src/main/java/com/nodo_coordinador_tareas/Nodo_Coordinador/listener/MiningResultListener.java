package com.nodo_coordinador_tareas.Nodo_Coordinador.listener;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningResult;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MiningResultListener {

    @RabbitListener(queues = "mining_results_queue")
    public void receiveMiningResult(MiningResult result) {
        System.out.println("Resultado recibido: " + result);
        // Aquí validar resultado y guardar bloque
        boolean valido = validarBloque(result);
        if (valido) {
            System.out.println("Bloque válido, guardando en Redis...");
            // Guardar en Redis o base de datos
        } else {
            System.out.println("Bloque inválido, descartado.");
        }
    }

    private boolean validarBloque(MiningResult result) {
        // Aquí se implementa validación del hash con dificultad
        // Ejemplo simplificado: verificar si hash empieza con cantidad de ceros = dificultad
        // Usar función hash y comparar
        return true; // por ahora devuelve true para testear
    }
}

