package cu.datys.min.svc.trace.web.rest;

import cu.datys.bim.common.utils.UTCTimeUtil;
import cu.datys.bim.webclients.auth.dto.ValidationDTO;
import cu.datys.bim.webclients.trace.dto.TraceDTO;
import cu.datys.bim.webclients.trace.dto.TraceDataDTO;
import cu.datys.bim.webclients.trace.dto.TraceDetailDTO;
import cu.datys.domain.bim.repository.messages.AisMssgsRepository;
import cu.datys.min.svc.trace.security.TokenProvider;
import cu.datys.min.svc.trace.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class TraceResource {
    private final TokenProvider tokenProvider;
    private final TraceService traceService;
    private final AisMssgsRepository aisMssgsRepository;

    public TraceResource(TokenProvider tokenProvider, TraceService traceService, AisMssgsRepository aisMssgsRepository) {
        this.tokenProvider = tokenProvider;
        this.traceService = traceService;
        this.aisMssgsRepository = aisMssgsRepository;
    }

    @PostMapping("/insert/{type}")
    public ResponseEntity<String> setTrace(@RequestBody Map<String, Object> body, @PathVariable String type, @RequestParam String process, @RequestParam String action, HttpServletRequest httpServletRequest) {
        if (!body.containsKey("objecttype")){
            return ResponseEntity.ok("Debe insertar un objecttype dentro del body para poder insertar la Traza correctamente");
        }
        String client = httpServletRequest.getRemoteAddr();
        return traceService.createTrace(body, type, process, action, client);
    }

    @GetMapping(value = "/getMssgData", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<JSONObject> getMssgData() {
        List<JSONObject> mssgData = aisMssgsRepository.getMssgDataFromAisMssg(UTCTimeUtil.restNMintutesToTimestamp(5760, UTCTimeUtil.utcNow()).toString());
        return mssgData;
    }

}
