package com.example.proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.proxy.dto.ProxyRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProxyTests {

    @LocalServerPort
    private int port = 0;

    private String proxyServiceURL;
    private String invalidProxyServiceURL;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void init() throws Exception {

        StringBuilder baseURL = new StringBuilder();
        baseURL.append("http://localhost:").append(port).append("/remoteservicegateway");

        invalidProxyServiceURL = baseURL.toString();
        proxyServiceURL = baseURL.append("/proxyService").toString();

    }

    /**
     * Client error - malformed object sent to Proxy Service e.g. object.name not
     * present
     * 
     * Expected response HTTP 400 (HTTP Bad Request)
     */
    @Test
    public void testProxyPost400() {

        logger.info("testProxyPost400: Client sent malformed message - madatory logical service name not present.");

        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setName(null); // <- creates a malformed oproxyRequest object.

        ResponseEntity<?> proxyResponseEntity = null;

        try {
            RestTemplate restTemplate = new RestTemplate();
            proxyResponseEntity = restTemplate.exchange(proxyServiceURL, HttpMethod.POST,
                    new HttpEntity<>(proxyRequest, createRESTHeaders()), ProxyRequest.class);

            // should not get here.
            fail("testProxyPost400: Failed to detetect missing 'name' in proxy request");

        } catch (HttpClientErrorException hsee) {
            assertThat(hsee.getStatusCode().value(), is(equalTo(400)));

        } catch (Exception e) {   
            fail("testDuplicateLogicalService: Failed test. Unexpected Exception, ");
        }

        ;
    }

    /**
     * Create the REST api headers. Used by the RESTTemplate
     * 
     * @return
     */
    public HttpHeaders createRESTHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        // Assumption: All proxy service invocations will be REST with a JSON body, also
        // that some basic headers will be present.
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", "Token abc123");
        return httpHeaders;
    }

    /**
     * Client error - wrong Proxy Service url.
     * 
     * e.g. Incorrect proxy service url
     * 
     * Expected response HTTP 404 (HTTP NotFound)
     */
    @Test
    public void testProxyPost404() {

        logger.info("testProxyPost404: Client sent to unknown proxy service.");

        ResponseEntity<?> proxyResponseEntity = null;

        try {

            // invoke the proxy service with unsupported endpoint url.
            RestTemplate restTemplate = new RestTemplate();
            proxyResponseEntity = restTemplate.exchange(invalidProxyServiceURL, HttpMethod.POST,
                    new HttpEntity<>(new ProxyRequest(), createRESTHeaders()), ProxyRequest.class);

            // should not get here.
            fail("testProxyPost404: Failed test. Unexpected behaviour, ");

        } catch (HttpClientErrorException hsee) {
            assertThat(hsee.getStatusCode().value(), is(equalTo(404)));

        } catch (Exception e) {   
            fail("testDuplicateLogicalService: Failed test. Unexpected Exception, ");
        }

    }

    /**
     * 3rd Party URL invoked via Proxy Service.
     * 
     * 
     * 
     * Expected response HTTP 200 (HTTP OK)
     */
    @Test
    public void testProxyPost200() {

        logger.info("testProxyPost200: 3rd Party URL invoked via  Proxy Service.");

        ResponseEntity<?> proxyResponseEntity = null;

        // Create a typical client request
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setName("GetUser");
        proxyRequest.addQueryParam("summary", "true");
//      proxyRequest.addPathParam("userId","1");        // Assumed that URL path param are out of scope of this example e.g. getUser/1?summary=true
        proxyRequest.addHeader("abc", "123");

        try {

            // invoke the proxy service with unsupported endpoint url.
            RestTemplate restTemplate = new RestTemplate();
            proxyResponseEntity = restTemplate.exchange(proxyServiceURL, HttpMethod.POST,
                    new HttpEntity<>(proxyRequest, createRESTHeaders()), ProxyRequest.class);

            // Expected 3rd Party response code, seen from Proxy Service invocation.
            assertThat(proxyResponseEntity.getStatusCodeValue(), is(equalTo(200)));

        } catch (Exception e) {

            // should not get here.
            fail("testProxyPost200: Failed test. Unexpected behaviour, ");

        }

    }

    /**
     * Test for an unknown 3rd party service.
     * 
     * 
     * 
     * Expected response HTTP 400 (HTTP Bad Request)
     */
    @Test
    public void testUnknownLogicalService() {

        logger.info("testUnknownLogicalService: Try to invoke an unknown 3rd Party URL via  Proxy Service.");

        ResponseEntity<?> proxyResponseEntity = null;

        // Create a typical client request
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setName("GetUnDefinedLogicalService");     // <-- this has not been declare in the proxy service store...
        

        try {

            // invoke the proxy service with unsupported endpoint url.
            RestTemplate restTemplate = new RestTemplate();
            proxyResponseEntity = restTemplate.exchange(proxyServiceURL, HttpMethod.POST,
                    new HttpEntity<>(proxyRequest, createRESTHeaders()), ProxyRequest.class);

            // should not get here.
            fail("testUnknownLogicalService: Failed test. Unexpected behaviour, ");
        } catch (HttpClientErrorException hcee) {
            assertThat(hcee.getStatusCode().value(), is(equalTo(400)));

        } catch (Exception e) {   
            fail("testDuplicateLogicalService: Failed test. Unexpected Exception, ");
        }

    }

    /**
     * Test for an duplicate logical service definition handling.
     * 
     * 
     * 
     * Expected response HTTP 500 (HTTP Bad Request)
     */
    @Test
    public void testDuplicateLogicalService() {
    
        logger.info("testDuplicateLogicalService: Duplicates exist in the PRoxy cservice config.");
    
        ResponseEntity<?> proxyResponseEntity = null;
    
        // Create a typical client request
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setName("EditUser");     // <-- thisis duplicated within the proxy service store...
        
    
        try {
    
            // invoke the proxy service with unsupported endpoint url.
            RestTemplate restTemplate = new RestTemplate();
            proxyResponseEntity = restTemplate.exchange(proxyServiceURL, HttpMethod.POST,
                    new HttpEntity<>(proxyRequest, createRESTHeaders()), ProxyRequest.class);
    
            // should not get here.
            fail("testDuplicateLogicalService: Failed test. Unexpected behaviour, ");
        } catch (HttpServerErrorException hsee) {
            assertThat(hsee.getStatusCode().value(), is(equalTo(500)));

        } catch (Exception e) {   
            fail("testDuplicateLogicalService: Failed test. Unexpected Exception, ");
        }
    
    }

    

}
