package com.nodo_coordinador_tareas.Nodo_Coordinador.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("block")
public class Block {

    @Id
    private String blockHash;
    private String previousHash;
    private long nonce;
    private Instant timestamp;
    private List<Transaction> transactions;
    private int difficulty;


}
