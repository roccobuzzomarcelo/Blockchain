package com.worker.Worker.service;

import com.worker.Worker.model.MiningResult;
import com.worker.Worker.model.MiningTask;
import com.worker.Worker.model.Transaction;
import minero.MineroCPU;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {



    public String crearCadena(String previousHash, List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();

        // Agregar el hash anterior
        sb.append(previousHash);

        // Asegurar que las transacciones estén en un orden consistente
        transactions.stream()
                .sorted(Comparator.comparing(Transaction::getId)) // o por otro campo si no hay ID
                .forEach(tx -> sb.append(tx.getUsuarioEmisor())
                        .append(tx.getUsuarioReceptor())
                        .append(tx.getMonto()));

        return sb.toString();
    }

    public MiningResult minar(MiningTask miningTask, String cadena){
        // 1. Crear el prefijo (cadena de ceros) según la dificultad
        String prefijo = "0".repeat(miningTask.getDifficulty());

        // 2. Crear instancia de la clase Minero
        MineroCPU minero = new MineroCPU();


        // 3. Invocar el método minar del Minero
        MineroCPU.ResultadoMinado resultado = minero.minar(prefijo, cadena,miningTask.getMinNonce(), miningTask.getMaxNonce());

        // 4. Verificar si se encontró un resultado válido
        if (resultado != null) {
            // 5. Construir el MiningResult completo
            MiningResult miningResult = new MiningResult();

            miningResult.setBlockHash(resultado.getHash());
            miningResult.setNonce(resultado.getNonce());
            miningResult.setPreviousHash(miningTask.getPreviousHash());
            miningResult.setTransactions(miningTask.getTransactions());
            miningResult.setTimestamp(java.time.LocalDateTime.now());

            return miningResult;

        } else {
            // No se encontró solución en el rango, podés retornar null o un MiningResult vacío
            return null;
        }
    }
}
