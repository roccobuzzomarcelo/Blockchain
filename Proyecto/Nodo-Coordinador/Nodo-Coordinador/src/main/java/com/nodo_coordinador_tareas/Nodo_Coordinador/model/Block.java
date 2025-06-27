package com.nodo_coordinador_tareas.Nodo_Coordinador.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
    private ArrayList<Transaction> transactions;
    private int difficulty;

}
