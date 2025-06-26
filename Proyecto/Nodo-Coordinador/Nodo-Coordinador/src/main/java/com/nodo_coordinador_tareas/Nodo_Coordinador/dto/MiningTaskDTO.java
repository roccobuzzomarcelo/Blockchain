package com.nodo_coordinador_tareas.Nodo_Coordinador.dto;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiningTaskDTO {
    private String blockId;              // 🔐 ID único del BLOQUE CANDIDATO
    private String previousHash;
    private List<Transaction> transactions;
    private int difficulty;
    private long minNonce;
    private long maxNonce;
}