package com.nodo_coordinador_tareas.Nodo_Coordinador.model;

import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.EstadoTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
    private Instant timestamp;


    private EstadoTransaction estado;
    
}
