package com.worker.Worker.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.worker.Worker.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("MiningTask")
@NoArgsConstructor
@AllArgsConstructor
public class MiningTaskDTO {
    private String blockId;
    private String previousHash;
    private List<Transaction> transactions;
    private int difficulty;
    private long minNonce;
    private long maxNonce;
}


