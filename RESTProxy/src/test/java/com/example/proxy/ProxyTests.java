package com.example.proxy;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
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
	    	
	    	invalidProxyServiceURL= baseURL.toString();
            proxyServiceURL = baseURL.append("/proxyService").toString();
            
            
	    }
	    
	    /**
	     * Client error - malformed object sent to Proxy Service
	     * e.g. object.name  not present
	     * 
	     * Expected response HTTP 400  (HTTP Bad Request) 
	     */
	    @Test
	    public void testProxyPost400() {
	    			
	        logger.info("testProxyPost400: Client sent malformed message - madatory logical service name not present." );
	    
	        ProxyRequest proxyRequest = new ProxyRequest();
	        proxyRequest.setName(null);            //  <- creates a malformed oproxyRequest object.
	        
	        
	        ResponseEntity<?> proxyResponseEntity = null;

	         
	        try {
	            RestTemplate restTemplate = new RestTemplate();
	            proxyResponseEntity = restTemplate.exchange(proxyServiceURL,
	            		HttpMethod.POST,
	            		new HttpEntity<>(proxyRequest , createRESTHeaders() ),
	            		ProxyRequest.class);
            
	            // should not get here.
	            fail("testProxyPost400: Failed to detetect missing 'name' in proxy request");
	                
	        } catch (Exception e) {
	           assertThat(proxyResponseEntity, is( notNullValue() ) );
	           
	           
	           assertThat( proxyResponseEntity.getStatusCodeValue(), is(400) );
	           assertThat(proxyResponseEntity.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
	            
	            
    	      
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
	        
	        // Assumption: All proxy service invocations will be REST with a JSON body, also that some basic headers will be present.
	        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	        httpHeaders.add("Authorization", "Token abc123" );
	        return httpHeaders;
	    }

        /**
         * Client error - wrong Proxy Service url. 
         * 
         * e.g. Incorrect proxy service url 
         * 
         * Expected response HTTP 404  (HTTP NotFound) 
         */
        @Test
        public void testProxyPost404() {
        			
            logger.info("testProxyPost404: Client sent to unknown proxy service." );
     
            
            ResponseEntity<?> proxyResponseEntity = null;
        
             
            try {
                
                // invoke the proxy service with unsupported endpoint url.
                RestTemplate restTemplate = new RestTemplate();
                proxyResponseEntity = restTemplate.exchange(invalidProxyServiceURL,
                		HttpMethod.POST,
                		new HttpEntity<>( new ProxyRequest() , createRESTHeaders() ),
                		ProxyRequest.class);
            
                
                
                // should not get here.
                fail("testProxyPost404: Failed test. Unexpected behaviour, ");
                
             
                
            } catch (Exception e) {
                // Service located, but no valid end point mapping.
                assertThat( e.getMessage(), containsString("404") );
            }
        
            
        }

        /**
         * 3rd Party URL invoked via  Proxy Service. 
         * 
         * 
         * 
         * Expected response HTTP 200  (HTTP OK) 
         */
        @Test
        public void testProxyPost200() {
        			
            logger.info("testProxyPost200: 3rd Party URL invoked via  Proxy Service." );
        
            
            ResponseEntity<?> proxyResponseEntity = null;
            
            // Create a typical client request
            ProxyRequest proxyRequest = new ProxyRequest();
            proxyRequest.setName("GetUser");
            proxyRequest.addQueryParam("summary","true");
//          proxyRequest.addPathParam("userId","1");        // Assumed that URL path param are out of scope of this example e.g. getUser/1?summary=true
            proxyRequest.addHeader("abc","123");
        
             
            try {
                
                // invoke the proxy service with unsupported endpoint url.
                RestTemplate restTemplate = new RestTemplate();
                proxyResponseEntity = restTemplate.exchange(proxyServiceURL,
                		HttpMethod.POST,
                		new HttpEntity<>( proxyRequest , createRESTHeaders() ),
                		ProxyRequest.class);
            
                
                
                // Expected 3rd Party response code, seen from Proxy Service invocation.
                assertThat( proxyResponseEntity.getStatusCodeValue(), is(equalTo(200)) );
                
             
                
            } catch (Exception e) {
                
                // should not get here.
                fail("testProxyPost200: Failed test. Unexpected behaviour, ");
                
            }
        
            
        }
	    
	    
	    
	    
        
        
		/**
		 * Save a action as expected.
		 */
//		@Test
//		public void testActionPost200() {
//					
//		    String url = testHelper.createUnitTestActionURL(port, null);
//		    Action savedAction = null;
//		    Action newAction = Action.example();
//		    newAction.setId(null);
//		    
//		    
//		
//		    RestTemplate restTemplate = new RestTemplate();
//		
//		     
//		    try {
//		        HttpEntity<Action> responseEntity = restTemplate.exchange(url,
//		        		HttpMethod.POST,
//		        		new HttpEntity<>(newAction, testHelper.createRESTHeaders(authToken) ),
//		        		Action.class);
//		
//		        savedAction = responseEntity.getBody();
//		
//		        MediaType contentType = responseEntity.getHeaders().getContentType();
//		        // application/json;charset=UTF-8
//		        assertThat(StringUtils.equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE, contentType.toString())).isTrue();
//		        assertThat(StringUtils.isNotBlank(savedAction.getId()) ).isTrue();
//		        logger.info(String.format("testActionPost200: Created a new action [%s]",savedAction.getId() ) );
//		        
//		    } catch (Exception e) {
//		        fail("testActionPost200: Got excpetion " + e.getMessage());
//		    }
//		
//		    
//		}

	
		


	}
