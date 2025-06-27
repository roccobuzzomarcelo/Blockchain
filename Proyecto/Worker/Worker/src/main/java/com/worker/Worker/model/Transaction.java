package com.worker.Worker.model;

import com.worker.Worker.enums.EstadoTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@RedisHash("transaction")
public class Transaction implements Serializable {

    public Transaction() {
        this.estado = EstadoTransaction.PENDIENTE;
    }

    @Id
    private String id;
    private String usuarioEmisor;
    private String usuarioReceptor;
    private int monto;
    private EstadoTransaction estado;

}

