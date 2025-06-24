package com.worker.Worker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiningResult {
    private String blockHash;
    private long nonce;
    private String previousHash;
    private List<Transaction> transactions;
    private LocalDateTime timestamp;
}