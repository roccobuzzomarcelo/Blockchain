package com.nodo_coordinador_tareas.Nodo_Coordinador.model;


import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.EstadoBlock;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateBlock implements Serializable {
    private String candidateId;           // UUID para identificar esta tarea
    private String previousHash;
    private EstadoBlock estado = EstadoBlock.PENDIENTE; // HEAD del último bloque
    private ArrayList<Transaction> transactions;
    private int difficulty;               // Prefijo requerido     // Concatenación o JSON base para hashear
    private long rangeStart;              // Desde qué nonce probar
    private long rangeEnd;                // Hasta qué nonce probar
}
