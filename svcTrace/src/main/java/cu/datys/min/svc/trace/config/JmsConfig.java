package cu.datys.min.svc.trace.config;

import cu.datys.bim.common.constants.BussinesConstants;
import cu.datys.bim.common.constants.ConfigPramConstants;
import cu.datys.bim.common.exceptions.SvcConfigParamNotFoundException;
import cu.datys.bim.common.exceptions.SvcHostNotFoundException;
import cu.datys.domain.bim.entity.config.SvcConfig;
import cu.datys.domain.bim.entity.config.SvcHost;
import cu.datys.domain.bim.repository.config.SvcConfigRepository;
import cu.datys.domain.bim.repository.config.SvcHostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
@Slf4j
public class JmsConfig {

    private final SvcHostRepository svcHostRepository;
    private final SvcConfigRepository svcConfigRepository;
    private final Environment env;
    private String brokerUrl;
    private String brokerUsername;
    private String brokerPassword;

    public JmsConfig(SvcHostRepository svcHostRepository,
                     SvcConfigRepository svcConfigRepository,
                     Environment env) throws SvcHostNotFoundException {
        this.svcHostRepository = svcHostRepository;
        this.svcConfigRepository = svcConfigRepository;
        this.env = env;
        this.loadBrokerConfig();
    }

    /**
     * Metodo que permite cargar la configuracion de ActiveMQ de la base de datos
     *
     * @throws SvcConfigParamNotFoundException
     * @throws SvcHostNotFoundException
     */
    private void loadBrokerConfig() throws SvcHostNotFoundException {
        SvcConfig brokerHostConf = svcConfigRepository.findById(new SvcConfig.SvcConfigId(
                env.getProperty(ConfigPramConstants.SPRING_APP_NAME), ConfigPramConstants.MESSAGE_BROKER_HOST)).orElseGet(
                () -> createConfig(ConfigPramConstants.MESSAGE_BROKER_HOST, ConfigPramConstants.MESSAGE_BROKER_HOST_DEFVALUE));
        SvcHost brokerHost = svcHostRepository.findById(brokerHostConf.getParamValue())
                .orElseThrow(() -> new SvcHostNotFoundException(
                        brokerHostConf.getParamValue(), env.getProperty(ConfigPramConstants.SPRING_APP_NAME)));
        brokerUrl = brokerHost.getHostUrl();
        brokerUsername = brokerHost.getUsername();
        brokerPassword = brokerHost.getPassword();
    }

    private SvcConfig createConfig(String paramName, String paramValue) {
        SvcConfig.SvcConfigId configId = new SvcConfig
                .SvcConfigId(env.getProperty(ConfigPramConstants.SPRING_APP_NAME), paramName);
        return svcConfigRepository.save(
                new SvcConfig().id(configId)
                        .paramValue(paramValue));
    }

    /**
     * Bean usado para inicial la conexion con el ActiveMQ
     *
     * @return
     */
    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory jmsConnectionFactory = new ActiveMQConnectionFactory(brokerUsername, brokerPassword, brokerUrl+"?wireFormat.maxInactivityDuration=30000");
        jmsConnectionFactory.setUseAsyncSend(true);
        jmsConnectionFactory.setWatchTopicAdvisories(false);
        return jmsConnectionFactory;
    }


    /**
     * Bean usado para publicar mensajes en una cola
     *
     * @return
     */
    @Bean(name = "jmsTemplateQueue")
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setMessageConverter(new JsonMessageConverter());
        template.setConnectionFactory(jmsConnectionFactory());
        template.setTimeToLive(BussinesConstants.MILISECONDS_LIVE_MESSAGE);
        template.setPubSubDomain(false);
        return template;
    }

    /**
     * Bean usado para publicar mensajes en un topico
     *
     * @return
     */
    @Bean(name = "jmsTemplateTopic")
    public JmsTemplate jmsTemplateTopic() {
        JmsTemplate template = new JmsTemplate();
        template.setMessageConverter(new JsonMessageConverter());
        template.setConnectionFactory(jmsConnectionFactory());
        template.setTimeToLive(BussinesConstants.MILISECONDS_LIVE_MESSAGE);
        template.setPubSubDomain(true);
        return template;
    }

}

