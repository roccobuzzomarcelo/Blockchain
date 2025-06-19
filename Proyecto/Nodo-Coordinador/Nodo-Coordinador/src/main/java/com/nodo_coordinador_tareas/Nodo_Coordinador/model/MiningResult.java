package com.nodo_coordinador_tareas.Nodo_Coordinador.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiningResult {
    private String blockHash;
    private long nonce;
    private String previousHash;
    private List<Transaction> transactions;
}