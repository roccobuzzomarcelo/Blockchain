package com.worker.Worker.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worker.Worker.dto.MiningResultDTO;
import com.worker.Worker.dto.MiningTaskDTO;
import com.worker.Worker.model.Transaction;
import minero.MineroCPU;
import minero.MineroCPU.ResultadoMinado;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RabbitWorkerListener {

    @Value("${coordinador.url}")
    private String coordinadorUrl;

    @Value("${worker.id}")
    private String workerId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MineroCPU minero = new MineroCPU();



    @RabbitListener(queues = "mining_tasks_queue")
    public void recibirTarea(MiningTaskDTO tarea) throws JsonProcessingException {
        System.out.printf("[Worker %s] Tarea recibida  para el bloque: )", workerId, tarea.getBlockId());

        String statusUrl = coordinadorUrl + "/block-status/" + tarea.getBlockId();

        ResponseEntity<String> response = restTemplate.getForEntity(statusUrl, String.class);
        String estado = response.getBody();

        if ("MINADO".equals(estado) || "NO_EXISTE".equals(estado)) {
            System.out.printf("[Worker %s] Bloque %s ya fue minado o no existe%n", workerId, tarea.getBlockId());
            return;
        }

        // Construir la cadena base a minar: previousHash + txs serializadas
        String baseString = tarea.getPreviousHash() + serializarTransacciones(tarea.getTransactions());
        String prefijo = "0".repeat(tarea.getDifficulty());

        ResultadoMinado resultadoMinado = minero.minar(prefijo, baseString, tarea.getMinNonce(), tarea.getMaxNonce());

        if (resultadoMinado != null) {
            MiningResultDTO resultado = new MiningResultDTO();
            resultado.setHash(resultadoMinado.getHash());
            resultado.setNonce(resultadoMinado.getNonce());
            resultado.setBlockId(tarea.getBlockId());
            resultado.setWorkerId(workerId);

            System.out.println("hash armado: " +  resultado.getHash() +  "entre : " + tarea.getMinNonce() + "y"  + tarea.getMaxNonce());

            System.out.println("hash conseguido: " + resultado);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(resultado);
            System.out.println("JSON generado: " + json);
            restTemplate.postForEntity(coordinadorUrl + "/solved_task", resultado, String.class);
            System.out.printf("[Worker %s] ¡Solución enviada! Hash: %s%n", workerId, resultadoMinado.getHash());
        } else {
            System.out.printf("[Worker %s] No se encontró solución válida.%n", workerId);
        }
    }

    private String serializarTransacciones(List<Transaction> txs) {
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
