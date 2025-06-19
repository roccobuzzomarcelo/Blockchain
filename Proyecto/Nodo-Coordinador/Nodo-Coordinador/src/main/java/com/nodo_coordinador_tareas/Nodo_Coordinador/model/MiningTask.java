package com.nodo_coordinador_tareas.Nodo_Coordinador.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiningTask {
    private String previousHash;
    private List<Transaction> transactions;
    private int difficulty;  // Nivel de dificultad del PoW
}
