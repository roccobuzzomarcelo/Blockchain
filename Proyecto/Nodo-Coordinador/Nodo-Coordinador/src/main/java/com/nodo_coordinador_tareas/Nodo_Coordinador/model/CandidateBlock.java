package com.nodo_coordinador_tareas.Nodo_Coordinador.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.EstadoBlock;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class CandidateBlock implements Serializable {
    private String candidateId;
    private String previousHash;
    private EstadoBlock estado = EstadoBlock.PENDIENTE;
    private ArrayList<Transaction> transactions;
    private int difficulty;
    private long rangeStart;
    private long rangeEnd;
}
