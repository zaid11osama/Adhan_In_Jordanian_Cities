package com.arabbank.hdf.uam.brain.orchestrator.gathering;

import com.arabbank.hdf.uam.brain.orchestrator.JobStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
class CollectorClient {
    private final RestTemplate restTemplate;
    @Value("${uam.api.collector.base-url}")
    private String baseUrl;
    @Value("${uam.api.collector.run-jobs-path}")
    private String runJobsEndPointPath;
    @Value("${uam.api.collector.jobs-status-path}")
    private String getStatusEndPointPath;
    @Value("${uam.api.collector.jobs-status-reschedule}")
    private String getRescheduleEndPointPath;

    public HttpStatus callRunCycleApi(String...codes) {
        String url = baseUrl + runJobsEndPointPath;

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("codes", (Object[]) codes)
                .build()
                .toUri();

        log.info("Sending request: {}", uri);

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return response.getStatusCode();
    }

    public List<JobStatusDto> callGetCycleStatusApi() {
        URI targetUrl = UriComponentsBuilder.fromUriString(baseUrl + getStatusEndPointPath)
                .build()
                .toUri();

        ResponseEntity<JobStatusDto[]> response = restTemplate.getForEntity(targetUrl, JobStatusDto[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public void callRescheduleActions() {
        String url = baseUrl + getRescheduleEndPointPath;

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .build()
                .toUri();

        log.info("Reschedule actions request: {}", uri);

        restTemplate.getForEntity(uri, String.class);
    }
}
