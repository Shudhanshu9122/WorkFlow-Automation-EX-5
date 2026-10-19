package edu.srmist.leave.api;

import edu.srmist.leave.api.dto.LeaveRequest;
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
 * REST API for Student Leave Application Workflow.
 * Endpoint: POST /leave
 */
@RestController
@RequestMapping("/leave")
public class LeaveController {

    private static final Logger log = LoggerFactory.getLogger(LeaveController.class);
    private static final String BPMN_PROCESS_ID = "student-leave";

    private final CamundaClient camundaClient;

    @Autowired
    public LeaveController(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> applyLeave(@Valid @RequestBody LeaveRequest request) {
        log.info("[REST POST /leave] Received leave request for student: {} ({}) from {} to {}",
                request.getStudentName(), request.getRegNo(), request.getFromDate(), request.getToDate());

        ProcessInstanceEvent instance = camundaClient.newCreateInstanceCommand()
                .bpmnProcessId(BPMN_PROCESS_ID)
                .latestVersion()
                .variables(request)
                .send()
                .join();

        long instanceKey = instance.getProcessInstanceKey();
        log.info("[REST POST /leave] Started process instance key: {}", instanceKey);

        return ResponseEntity.ok(Map.of(
                "processInstanceKey", instanceKey,
                "bpmnProcessId", BPMN_PROCESS_ID,
                "status", "STARTED"
        ));
    }
}
