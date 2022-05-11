/*
 * Copyright (C) 2022 hcadavid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package rug.icdtools.extensions.dashboard.interfacing.docsapiclient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


/**
 *
 * @author hcadavid
 */
public class DashboardAPIClient {
    
    private String authToken;
    private CloseableHttpClient httpClient;
    private String baseURL;

    public DashboardAPIClient(String baseURL,String credentials) throws APIAccessException {
        try {
            if (credentials==null || !credentials.contains(":")){
                throw new APIAccessException("Unable to use documentation pipeline API at:"+baseURL+". Wrong credentials format, expected user:password ");
            }
            this.authToken = "Bearer " + getToken(baseURL,credentials);
            this.baseURL = baseURL;
            this.httpClient = HttpClients.createDefault();
            
        } catch (IOException ex) {
            throw new APIAccessException("Unable to use documentation pipeline API at:"+baseURL+". - "+ex.getLocalizedMessage(),ex);
            
        }
    }

    /**
     * 
     * @param baseURL
     * @param credentials (format user:password)
     * @return
     * @throws JsonProcessingException
     * @throws IOException 
     */
    private String getToken(String baseURL, String credentials) throws JsonProcessingException, IOException, APIAccessException {
        HttpPost post = new HttpPost(baseURL+"/auth/login");

        ObjectMapper mapper = new ObjectMapper();

        String user=credentials.substring(0,credentials.indexOf(":"));
        String pwd =credentials.substring(credentials.indexOf(":")+1); 
        
        HttpEntity stringEntity = new StringEntity(mapper.writeValueAsString(new JwtAuthenticationRequest(user, pwd)), ContentType.APPLICATION_JSON);

        post.setEntity(stringEntity);

        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  CloseableHttpResponse response = httpClient.execute(post)) {

            String token = EntityUtils.toString(response.getEntity());

            JsonObject jsonResp = new Gson().fromJson(token, JsonObject.class);

            if (jsonResp == null){
                throw new APIAccessException("Failed API authentication for user ["+user+"]");
            }
            
            
            return jsonResp.get("access_token").getAsString();
        }

    }
    
    /**
     * 
     * @param resource
     * @param jsonObject
     * @throws APIAccessException 
     */
    public void postResource(String resource,String jsonObject) throws APIAccessException{
        try {

            HttpPost postRequest = new HttpPost(baseURL + resource);            
            postRequest.addHeader("Authorization", authToken);
            postRequest.addHeader("Content-Type", "application/json;charset=UTF-8");
            postRequest.setEntity(new StringEntity(jsonObject,StandardCharsets.UTF_8));
            
            HttpResponse response = httpClient.execute(postRequest);
            
            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300 ){
                throw new APIAccessException("Failure while posting resource "+resource+". Object:"+jsonObject+". HTTP Code:"+response.getStatusLine().getStatusCode());
            }
                       
        } catch (JsonProcessingException e ) {
            throw new APIAccessException("Failure while posting resource "+resource+". Object:"+jsonObject,e);
        } catch (IOException e) {
            throw new APIAccessException("Failure while posting resource "+resource+". Object:"+jsonObject,e);
        }

    }
    
    
    public void putResource(String resource,String jsonObject) throws APIAccessException{
        try {

            HttpPut putRequest = new HttpPut(baseURL + resource);
            putRequest.addHeader("Authorization", authToken);
            putRequest.addHeader("Content-Type", "application/json;charset=UTF-8");
            putRequest.setEntity(new StringEntity(jsonObject,StandardCharsets.UTF_8));

            HttpResponse response = httpClient.execute(putRequest);

            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                throw new APIAccessException("Failure while posting resource " + resource + ". Object:" + jsonObject + ". HTTP Code:" + response.getStatusLine().getStatusCode());
            }

        } catch (JsonProcessingException e) {
            throw new APIAccessException("Failure while posting resource " + resource + ". Object:" + jsonObject, e);
        } catch (IOException e) {
            throw new APIAccessException("Failure while posting resource " + resource + ". Object:" + jsonObject, e);
        }

    }

    public <T> T getResource(String resource, Class<T> resourceType) throws APIAccessException {
        ObjectMapper om = new ObjectMapper();
        //om.readValue(src, va)

        HttpGet getRequest = new HttpGet(baseURL + resource);
        getRequest.addHeader("Authorization", authToken);
        getRequest.addHeader("Content-Type", "application/json");

        HttpResponse response;
        try {
            response = httpClient.execute(getRequest);
            int returnCode = response.getStatusLine().getStatusCode();

            if (returnCode < 200 || returnCode >= 300) {
                if (returnCode == 404) {
                    throw new APIAccessException("Resource " + resource + " doesn't exist.");
                } else {
                    throw new APIAccessException("Server returned error code "+ returnCode +" when getting resource "+ resource);
                }
            } else {
                T entity = null;
                try {
                    entity = om.readValue(response.getEntity().getContent(), resourceType);
                    return entity;
                } catch (JsonParseException | JsonMappingException ex) {
                    throw new APIAccessException("Failure while parsing resource " + resource + ". Object:" + entity, ex);
                }
            }

        } catch (IOException ex) {
            throw new APIAccessException("I/O error when sending a GET request to the server at "+baseURL, ex);
        }

    }

}
