package com.worker.Worker.dto;


import com.worker.Worker.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiningTaskDTO {
    private String blockId;
    private String previousHash;
    private ArrayList<Transaction> transactions;
    private int difficulty;
    private long minNonce;
    private long maxNonce;

}


