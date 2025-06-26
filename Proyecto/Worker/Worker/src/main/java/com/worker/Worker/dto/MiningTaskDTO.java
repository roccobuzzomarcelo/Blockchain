package com.worker.Worker.dto;

import com.worker.Worker.model.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MiningTaskDTO {
    private String blockId;              // 🔐 ID único del bloque
    private String previousHash;
    private List<Transaction> transactions;
    private int difficulty;
    private long minNonce;
    private long maxNonce;
}

