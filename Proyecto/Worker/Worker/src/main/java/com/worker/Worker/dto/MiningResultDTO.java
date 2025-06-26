package com.worker.Worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiningResultDTO {
    private String blockId;    // 🔐 Indica qué bloque está resolviendo
    private String hash;       // 📛 Hash que resolvió
    private long nonce;        // 🔢 Nonce que usó
    private String workerId;   // 🛠️ Identificador del Worker
}
