package com.example.proxy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.proxy.dto.ProxyRequest;

@RestController
@RequestMapping("/remoteservicegateway") 
            

public class ProxyServiceController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        ResponseEntity response = null;
        String msg = "ok";
        
        try{
            
            // validate the proxy request
            
            // retrieve service mapping from persistent store
            
            // Engineer 3rd Party URL and invoke
            
            // manage the 3rd party response  ( 200, 40x & 50x) back ot client.
        
            
            
        } catch (IllegalArgumentException iae){
            httpStatus = HttpStatus.BAD_REQUEST;   // http 400
            msg = String.format("HTTPStatus:%s Unable to service proxy request.  %s",httpStatus, iae.getMessage() );
            
           
        } catch( Throwable t){
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            msg = String.format("HTTPStatus:%s A serious error occured, attempting to service request. Logical Request: '%s'. Error: %s",httpStatus.toString(), proxyRequest.getName(), t.getMessage() );
            
             
            
        }finally {
            logger.info( String.format("HTTPStatus:%s Serviced Proxy Request for logical service: '%s'", httpStatus.toString() , proxyRequest.getName() ) );
        }
        
        // return the saved - new Policy - it has been enriched with stores new primary key
        return new ResponseEntity<>(response, httpStatus);
    }
}
