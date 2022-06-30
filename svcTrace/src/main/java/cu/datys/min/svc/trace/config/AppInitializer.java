package cu.datys.min.svc.trace.config;

import cu.datys.bim.common.constants.ConfigPramConstants;
import cu.datys.bim.common.enums.ServiceStatus;
import cu.datys.bim.common.exceptions.SvcConfigParamNotFoundException;
import cu.datys.bim.common.exceptions.SvcHostNotFoundException;
import cu.datys.bim.webclients.WebClientFactory;
import cu.datys.domain.bim.entity.config.SvcConfig;
import cu.datys.domain.bim.entity.config.SvcHost;
import cu.datys.domain.bim.entity.config.SvcRegistry;
import cu.datys.domain.bim.repository.auth.ActiveTokenRepository;
import cu.datys.domain.bim.repository.config.SvcConfigRepository;
import cu.datys.domain.bim.repository.config.SvcHostRepository;
import cu.datys.domain.bim.repository.config.SvcRegistryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Optional;

/**
 * Clase para inicializar los datos de la App
 * Esta ejecuta el metodo 'run' al inicio de la aplicacion de SpringBoot
 **/

@Configuration
@Slf4j
public class AppInitializer implements CommandLineRunner {

    private final SvcHostRepository svcHostRepository;
    private final SvcRegistryRepository svcRegistryRepository;
    private final SvcConfigRepository svcConfigRepository;
    private final ActiveTokenRepository activeTokenRepository;
    private final Environment env;

    public AppInitializer(SvcHostRepository svcHostRepository,
                          SvcRegistryRepository svcRegistryRepository,
                          SvcConfigRepository svcConfigRepository,
                          ActiveTokenRepository activeTokenRepository,
                          Environment env) {
        this.svcHostRepository = svcHostRepository;
        this.svcRegistryRepository = svcRegistryRepository;
        this.svcConfigRepository = svcConfigRepository;
        this.activeTokenRepository = activeTokenRepository;
        this.env = env;
    }

    @Bean
    public WebClientFactory webClientFactory() {
        return new WebClientFactory(svcRegistryRepository, activeTokenRepository);
    }

    @Override
    public void run(String... args) throws Exception {
        ////log.info("*** APP INITIALIZR ***");
        createDefaultConfigs();
        registerService();
        ////log.info("*** DONE ***");
    }

    /**
     * Inicializa las configuraciones por defecto del servicio
     **/
    public void createDefaultConfigs() {
        // deploy.host
        svcConfigRepository.findById(new SvcConfig.SvcConfigId(
                env.getProperty(ConfigPramConstants.SPRING_APP_NAME), ConfigPramConstants.DEPLOY_HOST))
                .orElseGet(() -> createConfig(ConfigPramConstants.DEPLOY_HOST, "WEBLOGIC_01"));
    }

    private SvcConfig createConfig(String paramName, String paramValue) {
        ////log.info("Creating config: {} with value: {}", paramName, paramValue);
        SvcConfig.SvcConfigId configId = new SvcConfig
                .SvcConfigId(env.getProperty(ConfigPramConstants.SPRING_APP_NAME), paramName);
        return svcConfigRepository.save(
                new SvcConfig().id(configId)
                        .paramValue(paramValue));
    }

    /**
     * Registra el servicio
     **/
    public void registerService() throws SvcConfigParamNotFoundException, SvcHostNotFoundException {
        ////log.info("Registering service: {}", env.getProperty(ConfigPramConstants.SPRING_APP_NAME));

        SvcConfig hostConf = svcConfigRepository.findById(new SvcConfig.SvcConfigId(
                env.getProperty(ConfigPramConstants.SPRING_APP_NAME), ConfigPramConstants.DEPLOY_HOST))
                .orElseThrow(() -> new SvcConfigParamNotFoundException(
                        env.getProperty(ConfigPramConstants.SPRING_APP_NAME), ConfigPramConstants.DEPLOY_HOST));

        SvcHost svcHost = svcHostRepository.findById(hostConf.getParamValue())
                .orElseThrow(() -> new SvcHostNotFoundException(
                        hostConf.getParamValue(), env.getProperty(ConfigPramConstants.SPRING_APP_NAME)));

        Optional<SvcRegistry> svcRegistryDb = svcRegistryRepository
                .findByServiceName(env.getProperty(ConfigPramConstants.SPRING_APP_NAME));
        svcRegistryDb.map(svcRegistry ->
                svcRegistryRepository.save(svcRegistry
                        .status(ServiceStatus.STARTING)
                        .host(svcHost)))
                .orElseGet(() ->
                        svcRegistryRepository.save(new SvcRegistry()
                                .serviceName(env.getProperty(ConfigPramConstants.SPRING_APP_NAME))
                                .contextPath(env.getProperty("server.servlet.context-path"))
                                .description("Servicio de Trazas.")
                                .status(ServiceStatus.STARTING)
                                .host(svcHost)));
    }
}
