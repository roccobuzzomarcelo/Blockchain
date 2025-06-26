package com.nodo_coordinador_tareas.Nodo_Coordinador.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("block")
public class Block implements Serializable {
    @Id
    private String blockHash;
    private String previousHash;
    private List<Transaction> transactions;
    private long nonce;
    private Instant timestamp;
    private int difficulty;
}