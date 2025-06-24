package com.nodo_coordinador_tareas.Nodo_Coordinador.config;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.repository.IBlockRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;

@Component
public class GenesisBlockInitializer {

    private final IBlockRepository blockRepository;

    @Autowired
    public GenesisBlockInitializer(IBlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    @PostConstruct
    public void initGenesisBlock() {
        if (blockRepository.count() == 0) {
            Block genesisBlock = Block.builder()
                    .blockHash("00000000000000000000000000000000")
                    .previousHash("GENESIS")
                    .nonce(0L)
                    .timestamp(Instant.now())
                    .transactions(Collections.emptyList())
                    .build();

            blockRepository.save(genesisBlock);
            System.out.println("Bloque Génesis creado y guardado en Redis.");
        } else {
            System.out.println("ℹEl bloque génesis ya existe. No se creó uno nuevo.");
        }
    }
}

