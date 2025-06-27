package com.nodo_coordinador_tareas.Nodo_Coordinador.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiningTaskDTO {
    private String blockId;
    private String previousHash;
    private ArrayList<Transaction> transactions;
    private int difficulty;
    private long minNonce;
    private long maxNonce;
}