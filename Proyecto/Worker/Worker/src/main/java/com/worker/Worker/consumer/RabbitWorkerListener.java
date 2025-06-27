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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RabbitWorkerListener {

    @Value("${coordinador.url}")
    private String coordinadorUrl;

    @Value("${worker.id}")
    private String workerId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MineroCPU minero = new MineroCPU();



    private final Set<String> bloquesYaMinados = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = "mining_tasks_queue")
    public void recibirTarea(MiningTaskDTO tarea) throws JsonProcessingException {
        if (bloquesYaMinados.contains(tarea.getBlockId())) {
            System.out.printf("[Worker %s] 🔁 Tarea ignorada porque el bloque %s ya está minado.%n", workerId, tarea.getBlockId());
            return;
        }

        System.out.printf("[Worker %s] Tarea recibida para el bloque: %s%n", workerId, tarea.getBlockId());

        String url = coordinadorUrl + "/block-status/" + tarea.getBlockId();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String estado = response.getBody();

        if ("NO_EXISTE".equals(estado)) {
            System.out.println("El bloque no existe");
            return;
        }

        if ("MINADO".equals(estado)) {
            System.out.println("El bloque ya fue minado");
            bloquesYaMinados.add(tarea.getBlockId());
            return;
        }

        String baseString = tarea.getPreviousHash() + serializarTransacciones(tarea.getTransactions());
        String prefijo = "0".repeat(tarea.getDifficulty());

        ResultadoMinado resultadoMinado = minero.minar(prefijo, baseString, tarea.getMinNonce(), tarea.getMaxNonce());

        if (resultadoMinado != null) {
            MiningResultDTO resultado = new MiningResultDTO();
            resultado.setHash(resultadoMinado.getHash());
            resultado.setNonce(resultadoMinado.getNonce());
            resultado.setBlockId(tarea.getBlockId());
            resultado.setWorkerId(workerId);

            System.out.println("cadena: " + baseString);
            System.out.println("hash armado: " + resultado.getHash() + " entre : " + tarea.getMinNonce() + " y " + tarea.getMaxNonce());
            System.out.println("hash conseguido: " + resultado);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(resultado);
            System.out.println("JSON generado: " + json);

            try {
                ResponseEntity<String> responsePost = restTemplate.postForEntity(coordinadorUrl + "/solved_task", resultado, String.class);

                if (responsePost.getStatusCode().is2xxSuccessful()) {
                    System.out.printf("[Worker %s] ¡Solución aceptada! Hash: %s%n", workerId, resultadoMinado.getHash());
                    bloquesYaMinados.add(tarea.getBlockId());
                } else {
                    System.out.printf("[Worker %s] Solución rechazada con código: %s. Mensaje: %s%n",
                            workerId, responsePost.getStatusCode(), responsePost.getBody());
                }

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.CONFLICT) {
                    System.out.printf("[Worker %s] ⚠ Bloque ya minado. No se enviará más.%n", workerId);
                    bloquesYaMinados.add(tarea.getBlockId());
                } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    System.out.printf("[Worker %s] Solución inválida. Mensaje: %s%n", workerId, e.getResponseBodyAsString());
                } else {
                    System.out.printf("[Worker %s] Error inesperado al enviar solución. Código: %s, Mensaje: %s%n",
                            workerId, e.getStatusCode(), e.getResponseBodyAsString());
                }
            }

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
