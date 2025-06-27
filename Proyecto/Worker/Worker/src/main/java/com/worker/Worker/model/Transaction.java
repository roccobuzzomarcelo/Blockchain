package com.worker.Worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@RedisHash("transaction")
public class Transaction implements Serializable {

    public Transaction() {

    }

    @Id
    private String id;
    private String usuarioEmisor;
    private String usuarioReceptor;
    private int monto;

}

