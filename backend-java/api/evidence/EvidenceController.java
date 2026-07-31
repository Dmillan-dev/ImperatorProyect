package imperator.api.evidence;

import imperator.ports.in.ImportEvidenceInputPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;

@RestController
@RequestMapping("/api/v1/evidence")
public final class EvidenceController {
    static final String NDJSON_MEDIA_TYPE = "application/x-ndjson";

    private final EvidenceNdjsonImporter importer;

    @Autowired
    public EvidenceController(ObjectProvider<ImportEvidenceInputPort> inputPortProvider) {
        this(new EvidenceNdjsonImporter(inputPortProvider::getIfAvailable, Clock.systemUTC()));
    }

    EvidenceController(EvidenceNdjsonImporter importer) {
        this.importer = importer;
    }

    @PostMapping(
            path = "/import",
            consumes = NDJSON_MEDIA_TYPE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    EvidenceImportResponse importEvidence(@RequestBody byte[] body) {
        return importer.importPayload(body);
    }
}
