package com.reddate.ddc.util.http;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class RestTemplateUtil {

    private RestTemplate restTemplate;

    public RestTemplateUtil() {
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();
        restTemplate = restTemplateConfig.restTemplate(restTemplateConfig.simpleClientHttpRequestFactory());
    }

    public <T> T sendPost(String url, Object params, Class<T> t) throws RestClientException {
        HttpHeaders header = new HttpHeaders();
        // Requirements need to be passed in form-data format
        header.set("charset", "UTF-8");
        header.set("Content-Type", "application/json");
//        header.setContentType(MediaType.APPLICATION_JSON);
        String value = JSONObject.toJSONString(params);

        log.debug("send http request to {} ,the params are {}", url, value);

        HttpEntity httpEntity = new HttpEntity(value, header);
        return restTemplate.postForObject(url, httpEntity, t);
    }

    public <T> T sendGet(String url, Object params, Class<T> t) throws RestClientException {
        HttpHeaders header = new HttpHeaders();
        // Requirements need to be passed in form-data format
        header.set("charset", "UTF-8");
        header.set("Content-Type", "application/json");
        String value = JSONObject.toJSONString(params);

        log.debug("send http request to {} ,the params are {}", url, value);

        HttpEntity httpEntity = new HttpEntity(value, header);
        return restTemplate.getForObject(url, t, httpEntity);
    }

    public <T> T sendDel(String url, Map<String, Object> urlParams, Object bodyParams, Class<T> t) throws RestClientException {
        HttpHeaders header = new HttpHeaders();
        // Requirements need to be passed in form-data format
        header.set("charset", "UTF-8");
        header.set("Content-Type", "application/json");
        String value = JSONObject.toJSONString(bodyParams);

        log.debug("send http request to {} ", url);

        HttpEntity httpEntity = new HttpEntity(value, header);

        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, t, urlParams);

        return response.getBody();
    }

    public <T> T sendPostFile(String url, String filePath, String fileName, Class<T> t) throws RestClientException {
        //Set ReqHeader
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        //Set ReqBody，LinkedMultiValueMap
        FileSystemResource fileSystemResource = new FileSystemResource(filePath + "/" + fileName);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", fileSystemResource);
        form.add("filename", fileName);

        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);

        return restTemplate.postForObject(url, files, t);
    }


}
