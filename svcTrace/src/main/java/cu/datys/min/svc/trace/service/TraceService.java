package cu.datys.min.svc.trace.service;

import cu.datys.bim.common.constants.SvcNamesConstants;
import cu.datys.bim.common.credentials.AdminCredentials;
import cu.datys.bim.common.utils.UTCTimeUtil;
import cu.datys.bim.webclients.WebClientFactory;
import cu.datys.bim.webclients.auth.WCLogin;
import cu.datys.bim.webclients.trace.SvcTraceWebClient;
import cu.datys.bim.webclients.trace.dto.TraceDTO;
import cu.datys.bim.webclients.trace.dto.TraceDataDTO;
import cu.datys.bim.webclients.trace.dto.TraceDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TraceService {

    private final SvcTraceWebClient svcTraceWebClient;

    public TraceService(WebClientFactory webClientFactory) {
        this.svcTraceWebClient = webClientFactory
                .build(SvcNamesConstants.SVC_TRACE, SvcTraceWebClient.class)
                .orElse(null);
    }

    /**
     * El cuerpo de la traza esta estructurado por la data(TraceDataDTO) y el detail(TraceDetailDTO).
     * La traza utiliza la dir del Weblogic en la tabla de SVC_HOST
     */
    public ResponseEntity<String> createTrace(Map<String, Object> body, String type, String process, String action, String client) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            List<SvcHost> ipAddress = svcHostRepository.findByHostName(ConfigPramConstants.DEPLOY_HOST_DEFVALUE);
//            String host = ipAddress.get(0).getHostUrl().split("//")[1].split(":")[0];
            TraceDetailDTO traceDetailDTO = new TraceDetailDTO(createTraceObject(body), body.get("objecttype").toString());
            TraceDataDTO traceDataDTO = new TraceDataDTO(type, process, action, client, auth.getName());
            TraceDTO traceDTO = new TraceDTO(traceDataDTO, traceDetailDTO);
            Optional<String> resp = svcTraceWebClient.insertTrace
                    (WCLogin.of(AdminCredentials.buildExternalAuthService()), traceDTO);
            return ResponseEntity.ok(resp.orElse("Problema con la insercion de la traza"));
            //log.info(resp.orElse(null));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.ok(String.format("La traza no pudo ser insertada por esta causa: %s", ex.getMessage()));
        }
    }


    /**
     * Se obtienen la fecha y hora actual del servidor para incorporarlo al detail de la traza
     */
    public Map<String, Object> createTraceObject(Map<String, Object> body) {
        body.put("fecha", UTCTimeUtil.getYearMonthDay());
        body.put("Hora", UTCTimeUtil.getActualCompliteHour());
        return body;
    }


}
