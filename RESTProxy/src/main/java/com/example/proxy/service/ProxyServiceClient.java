package com.example.proxy.service;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import org.apache.commons.lang3.StringUtils;
import org.mockito.internal.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.proxy.dto.ProxyRequest;
import com.example.proxy.dto.ProxyResponse;
import com.example.proxy.model.ServiceDefinition;

@Service
public class ProxyServiceClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProxyResponse invokeRemote(ProxyRequest proxyRequest, ServiceDefinition serviceDefinition) {
        ProxyResponse proxyResponse = new ProxyResponse();

        // Form the URL from the Service Definition & Request Query Parameters
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serviceDefinition.getUrl());
        if (!proxyRequest.getQparam().isEmpty()) {
            proxyRequest.getQparam().forEach((k, v) -> {
                // prevent adding badly formed QueryParams
                if( StringUtils.isNotBlank(v)) {
                    uriBuilder.queryParam(k, v);
                }
            });
        }
        
        String     remoteURL        = uriBuilder.toUriString();
        HttpMethod remoteHttpMethod = HttpMethod.resolve(serviceDefinition.getMethod());
        
        
        // Establish request Body (JSON String) when POST/PUT   (Not required for GET/DELETE)
        String     remotePayLoad;
        if (remoteHttpMethod==HttpMethod.POST || remoteHttpMethod==HttpMethod.PUT) {
            remotePayLoad  = proxyRequest.getPayload();
            
        } else {
            remotePayLoad  = "";
            
        }
        
        
        
        

        try {
            // Set the Rest Temple sync. Timeout, based on the Service config value...
            // This is a highly troubled area in Spring, many different version of doing this over time...
            // Basically it boils down to '...Spring does not expose convenient setters for Timeout...', so you have
            // to dig around in the underlying Sender, Sender Factory, RequestConfig etc. Or delegate to
            // Apache HttpClient - which is what I have done by introducing HttpClient into Gradle dependencies.
            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectTimeout( 10000);           // allow for slow infrastructure
            httpRequestFactory.setReadTimeout( 2000);               // allow for slow remote service response
            httpRequestFactory.setConnectionRequestTimeout(60000);  // allow for this RESTProxy connection manager being 'busy'
            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
            
           
            
            // Add Default Headers (JSON) and those from serviceDef. &Request 
            HttpHeaders remoteHeaders = new HttpHeaders();
            remoteHeaders.setContentType(MediaType.APPLICATION_JSON);
            if( ! proxyRequest.getHeaders().isEmpty()) {
                // prevent form adding badly form Header defs.
                proxyRequest.getHeaders().forEach( h -> {
                    if (StringUtils.isNotBlank(h.getName())) {
                        remoteHeaders.add(h.getName(), h.getValue());
                    }
                });
            }
            if( ! serviceDefinition.getStaticHeaders().isEmpty()) {
                serviceDefinition.getStaticHeaders().forEach( h -> {
                    // prevent form adding badly form Header defs.
                    if (StringUtils.isNotBlank(h.getName())) {
                        remoteHeaders.add(h.getName(), h.getValue());
                    }
                });
            }
            // Build the Remote Request (3rd Party API) request& response Entity.
            HttpEntity<String> requestEntity = new HttpEntity<String>(remotePayLoad, remoteHeaders);
            HttpEntity<String> response = null;
            

            logger.warn(String.format(">>>> Invoking Remote 3rd party API [%s] %s:  %s",proxyRequest.getName(), remoteHttpMethod, remoteURL));
            response = restTemplate.exchange(remoteURL, 
                                             remoteHttpMethod, 
                                             requestEntity,
                                             String.class);
            logger.warn(String.format("<<<<< Response from Remote 3rd party API [%s]", proxyRequest.getName() ) );
            


            // unpack the Http Headers MultiValueMap - into a simple list of Key values. 
            response.getHeaders().forEach((k, v) -> {
                v.forEach(value -> proxyResponse.addHeader(k, value));
            });
            proxyResponse.setBody(response.getBody());

            return proxyResponse;

        } catch (Throwable t) {
            throw new RemoteInvocationException(t.getMessage());
        }
    }

}
