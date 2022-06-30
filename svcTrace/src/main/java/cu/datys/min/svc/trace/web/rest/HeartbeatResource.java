package cu.datys.min.svc.trace.web.rest;

import cu.datys.bim.common.constants.ConfigPramConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/heartbeat")
public class HeartbeatResource {

    private final Environment env;

    public HeartbeatResource(Environment env) {
        this.env = env;
    }

    /**
     * Metodo para manejar la peticion de chequeo de salud (heartbeat)
     **/
    @GetMapping
    public ResponseEntity<Void> heartbeat() {
        ////log.info("*** {}-heartbeat ***", env.getProperty(ConfigPramConstants.SPRING_APP_NAME));
        return ResponseEntity.ok().build();
    }
}
