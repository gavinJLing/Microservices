package com.example.proxy.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.proxy.dto.ProxyRequest;
import com.example.proxy.dto.ProxyResponse;
import com.example.proxy.model.ServiceDefinition;
import com.example.proxy.repository.ServiceRepository;
import com.example.proxy.service.ProxyServiceClient;
import com.example.proxy.service.RemoteInvocationException;

@RestController
@RequestMapping("/remoteservicegateway") 
            

public class ProxyServiceController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServiceRepository serviceRepo;
    
    @Autowired 
    ProxyServiceClient proxyServiceClient;
    
    /**
     * POST /remoteservicegateway/proxyService
     *  Realy proxy Request onto external 3rd party service and respond
     * 
     *
     * returns:
     * HTTP 200  if 3rd party executed ok
     * HTTP 400  if malformed Request
     * HTTP 404  if remote service not found (consider time out)
     * HTTP 503  if something when wrong... (i.e.no storage device)
     * 
     * @return
     */
    @RequestMapping(value = "/proxyService", method = RequestMethod.POST, headers="Accept=application/json")
    public ResponseEntity<?> service(@RequestBody ProxyRequest proxyRequest) {
        HttpStatus httpStatus = HttpStatus.OK;
        ProxyResponse proxyResponse = null;
        String msg = "Processed ok";
        
        try{
            
            // validate the proxy request
            if (proxyRequest.isNotValid() ) {
                throw new IllegalArgumentException("Malformed Proxy Service Request. Not Valid.");
            } 
            
            // retrieve service mapping from persistent store
            List<ServiceDefinition> serviceDefs = serviceRepo.findByName(proxyRequest.getName());
            // check for a match. If not found then client is asking for an unknown service. (http 400 - bad service)
            if (serviceDefs.isEmpty()) {
                throw new IllegalArgumentException("Invalid Proxy Service Request. Logical service name not recognised.");
            }
            // check for service duplicates and report back as an internal server issue. (http 500 Internal server error)
            if (serviceDefs.size() > 1) {
                throw new IllegalStateException(String.format("Duplicate services located. %s '%s' services located.",serviceDefs.size(), proxyRequest.getName() ));
            }
            
            
           
            // Engineer 3rd Party URL and invoke
            // Any exceptions encountered during the upstream rest call - will be wrapped as a
            // RemoteInvocationException and get a return code on Http 502 (bad gateway)
            proxyResponse = proxyServiceClient.invokeRemote(proxyRequest, serviceDefs.get(0));
          
            
               
            
            
        } catch (IllegalArgumentException iae){
            httpStatus = HttpStatus.BAD_REQUEST;   // http 400
            msg = String.format("Unable to service proxy request. %s", iae.getMessage() );
            
        } catch (IllegalStateException ise) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;   // http 500
            msg = String.format("Unable to service proxy request. %s", ise.getMessage() );
            
        } catch ( RemoteInvocationException rie) {
            httpStatus = HttpStatus.BAD_GATEWAY;   // http 502
            msg = String.format("Unable to service proxy request. %s", rie.getMessage() );
            
        } catch( Throwable t){
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            msg = String.format("A serious error occured, attempting to service request. %s", t.getMessage() );
            
             
            
        }finally {
            logger.info( String.format("HTTPStatus:%s, Request:%s, Msg:%s", httpStatus , proxyRequest.getName() , msg ) );
        }
        
         
   
       return new ResponseEntity<>(proxyResponse, httpStatus);
       
       
    }
}
