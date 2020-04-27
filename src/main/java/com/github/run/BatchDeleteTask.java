package com.github.run;

import com.github.model.Config;
import com.github.model.Relation;
import com.github.repository.EsRepository;
import com.github.util.Logs;
import com.github.util.U;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

@Profile("!test")
@Configuration
public class BatchDeleteTask {

    private final Config config;
    private final EsRepository esRepository;
    public BatchDeleteTask(Config config, EsRepository esRepository) {
        this.config = config;
        this.esRepository = esRepository;
    }

    @Scheduled(cron = "13 0/1 * * * *")
    public void offlineDevice() {
        if (Logs.ROOT_LOG.isInfoEnabled()) {
            Logs.ROOT_LOG.info("begin to delete data");
        }

        try {
            for (Relation relation : config.getRelation()) {
                String deleteField = relation.getDeleteField();
                String deleteValue = relation.getDeleteValue();
                if (relation.isDelete() && U.isNotBlank(deleteField) && U.isNotBlank(deleteValue)) {
                    esRepository.batchDelete(relation.useIndex(), deleteField, deleteValue);
                }
            }
        } finally {
            if (Logs.ROOT_LOG.isInfoEnabled()) {
                Logs.ROOT_LOG.info("end of batch delete data");
            }
        }
    }
}
