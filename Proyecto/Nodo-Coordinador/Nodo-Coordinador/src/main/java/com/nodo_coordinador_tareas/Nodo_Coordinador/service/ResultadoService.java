package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningResultDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.EstadoBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.ResultadoValidacion;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.CandidateBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ResultadoService {


    @Autowired
    @Qualifier("candidateBlockRedisTemplate")
    private RedisTemplate<String, CandidateBlock> redisTemplate;


    private final AtomicLong altura = new AtomicLong(0);

    @Autowired
    private BlockService blockService;

    public ResultadoValidacion procesarResultado(MiningResultDTO resultado) {
        String redisKey = "block-pending:" + resultado.getBlockId();
        CandidateBlock candidato = (CandidateBlock) redisTemplate.opsForValue().get(redisKey);

        if (candidato == null) {
            System.out.println("El bloque no existe");
            return ResultadoValidacion.INVALIDA;
        }

        String lockKey = "lock:" + resultado.getBlockId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, candidato, Duration.ofSeconds(10));

        if (locked == null || !locked) {
            System.out.println("No se pudo adquirir el lock");
            return ResultadoValidacion.INVALIDA;
        }

        try {
            if (candidato.getEstado() == EstadoBlock.MINADO) {
                System.out.println("Ya minado (dentro del lock)");
                return ResultadoValidacion.YA_MINADO;
            }

            String base = candidato.getPreviousHash() + serializarTransacciones(candidato.getTransactions());
            String hashCalculado = HashUtil.calcularHashMD5(base, resultado.getNonce());
            String prefijoRequerido = "0".repeat(candidato.getDifficulty());

            if (!hashCalculado.startsWith(prefijoRequerido)) {
                System.out.println("Hash inválido");
                return ResultadoValidacion.INVALIDA;
            }



            Block bloque = Block.builder()
                    .blockHash(hashCalculado)
                    .nonce(resultado.getNonce())
                    .timestamp(Instant.now())
                    .transactions(candidato.getTransactions())
                    .difficulty(candidato.getDifficulty())
                    .previousHash(candidato.getPreviousHash())
                    .build();

            blockService.save(bloque);

            candidato.setEstado(EstadoBlock.MINADO);
            redisTemplate.opsForValue().set(redisKey, candidato, Duration.ofMinutes(10));

            System.out.println("Bloque minado correctamente");
            return ResultadoValidacion.VALIDA;

        } finally {
            redisTemplate.delete(lockKey);
        }
    }



    private String serializarTransacciones(ArrayList<Transaction> txs) {
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : txs) {
            sb.append(tx.getId())
                    .append(tx.getUsuarioEmisor())
                    .append(tx.getUsuarioReceptor())
                    .append(tx.getMonto());
        }
        return sb.toString();
    }
}
