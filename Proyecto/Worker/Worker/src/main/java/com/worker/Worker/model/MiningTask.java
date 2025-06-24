package com.worker.Worker.model;


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
    private int difficulty;
    private long minNonce;
    private long maxNonce;
}
