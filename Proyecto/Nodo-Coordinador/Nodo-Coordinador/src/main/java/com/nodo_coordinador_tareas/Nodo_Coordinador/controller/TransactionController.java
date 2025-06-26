package com.nodo_coordinador_tareas.Nodo_Coordinador.controller;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionPoolService transactionPoolService;

    @PostMapping("/transactions")
    public ResponseEntity<?> agregarTransaccion(@RequestBody Transaction transaction) {

        return ResponseEntity.ok("Transaccion guardada correctamente");
    }
}
