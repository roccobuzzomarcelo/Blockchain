package com.nodo_coordinador_tareas.Nodo_Coordinador.controller;


import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.repository.IBlockRepository;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.IBlockService;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.TransactionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blocks")
public class BlockchainController {

    @Autowired
    IBlockRepository blockRepository;

    @Autowired
    IBlockService blockService;

    @Autowired
    TransactionPoolService transactionPoolService;


    @PostMapping
    public ResponseEntity<Block> createBlock(@RequestBody Block block) {
        Block savedBlock = blockService.save(block);
        return ResponseEntity.ok(savedBlock);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Block> getBlock(@PathVariable String id) {
        return blockService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/transactions")
    public String agregarTransaccion(@RequestBody Transaction transaction) {
        transactionPoolService.guardar(transaction);
        return "Transacción agregada al pool";
    }




}