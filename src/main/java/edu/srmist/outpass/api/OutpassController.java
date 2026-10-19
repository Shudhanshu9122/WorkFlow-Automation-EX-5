package edu.srmist.outpass.api;

import edu.srmist.outpass.api.dto.OutpassRequest;
import edu.srmist.outpass.api.dto.OutpassResponse;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for Student Hostel Out-Pass Requests.
 * Endpoint: POST /outpass
 */
@RestController
@RequestMapping("/outpass")
public class OutpassController {

    private static final Logger log = LoggerFactory.getLogger(OutpassController.class);
    private static final String BPMN_PROCESS_ID = "hostel-outpass";

    private final CamundaClient camundaClient;

    @Autowired
    public OutpassController(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    /**
     * Submit a new hostel out-pass application.
     * Triggers the 'hostel-outpass' process on Camunda 8 SaaS.
     */
    @PostMapping
    public ResponseEntity<OutpassResponse> requestOutpass(@Valid @RequestBody OutpassRequest request) {
        log.info("[REST POST /outpass] Received out-pass request for student: {} ({})",
                request.getStudentName(), request.getRegNo());

        ProcessInstanceEvent instance = camundaClient.newCreateInstanceCommand()
                .bpmnProcessId(BPMN_PROCESS_ID)
                .latestVersion()
                .variables(request)
                .send()
                .join();

        long instanceKey = instance.getProcessInstanceKey();
        log.info("[REST POST /outpass] Started process instance key: {} for student: {}",
                instanceKey, request.getStudentName());

        OutpassResponse response = new OutpassResponse(
                instanceKey,
                BPMN_PROCESS_ID,
                request.getStudentName(),
                request.getRegNo(),
                "INITIATED",
                "Out-pass request workflow instance successfully created on Camunda 8 SaaS"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Health check / Info endpoint for out-pass service.
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "service", "Hostel Out-Pass Automation Service",
                "version", "1.0.0",
                "processDefinitionId", BPMN_PROCESS_ID,
                "status", "UP"
        ));
    }
}
