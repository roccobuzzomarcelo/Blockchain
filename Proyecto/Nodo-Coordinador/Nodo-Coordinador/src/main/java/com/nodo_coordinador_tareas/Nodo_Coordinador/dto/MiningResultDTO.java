package com.nodo_coordinador_tareas.Nodo_Coordinador.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiningResultDTO {
    private String blockId;
    private String hash;
    private long nonce;
    private String workerId;
}
