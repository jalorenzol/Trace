package cu.datys.min.svc.trace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"cu.datys.domain.bim.entity"})
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = {"cu.datys.domain.bim.repository"})
@EnableCaching
@SpringBootApplication
public class SvcTraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvcTraceApplication.class, args);
    }

}
