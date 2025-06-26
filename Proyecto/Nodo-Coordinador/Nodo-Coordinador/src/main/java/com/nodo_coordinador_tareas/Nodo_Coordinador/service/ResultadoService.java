package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningResultDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.CandidateBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ResultadoService {

    @Value("${blockchain.difficultyPrefix}")
    private String dificultad;

    private final RedisTemplate<String, CandidateBlock> redisTemplate;
    private final AtomicLong altura = new AtomicLong(0);

    @Autowired
    private BlockService blockService;

    public ResultadoService(RedisTemplate<String, CandidateBlock> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean procesarResultado(MiningResultDTO resultado) {
        CandidateBlock candidato = redisTemplate.opsForValue().get("block-pending:" + resultado.getBlockId());

        if (candidato == null) return false;

        String base = candidato.getPreviousHash()
                + serializarTransacciones(candidato.getTransactions());

        String hashCalculado = HashUtil.calcularHashMD5(base, resultado.getNonce());

        if (hashCalculado.equals(resultado.getHash()) && hashCalculado.startsWith(dificultad)) {
            Block bloque = Block.builder()
                    .blockHash(hashCalculado)
                    .nonce(resultado.getNonce())
                    .timestamp(java.time.Instant.now())
                    .transactions(candidato.getTransactions())
                    .difficulty(dificultad.length())
                    .previousHash(candidato.getPreviousHash())
                    .build();

            blockService.save(bloque);
            redisTemplate.delete("block-pending:" + resultado.getBlockId());
            return true;
        }

        return false;
    }


    private String serializarTransacciones(List<Transaction> txs) {
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : txs) {
            sb.append(tx.getId())
                    .append(tx.getUsuarioEmisor())
                    .append(tx.getUsuarioReceptor())
                    .append(tx.getMonto())
                    .append(tx.getTimestamp());
        }
        return sb.toString();
    }
}
