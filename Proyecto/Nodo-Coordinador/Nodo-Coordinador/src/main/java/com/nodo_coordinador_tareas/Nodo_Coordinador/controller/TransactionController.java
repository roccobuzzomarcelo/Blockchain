package com.nodo_coordinador_tareas.Nodo_Coordinador.controller;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.TransactionValidatorService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TransactionValidatorService validatorService;

    @PostMapping
    public ResponseEntity<?> agregarTransaccion(@RequestBody Transaction transaction) {

        if (!validatorService.esValida(transaction)) {
            System.out.println("Transacción inválida descartada: " + transaction.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transacción no válida.");
        }

        rabbitTemplate.convertAndSend("transaction_exchange", "transaction.routingKey", transaction);
        return ok(transaction);
    }
}
