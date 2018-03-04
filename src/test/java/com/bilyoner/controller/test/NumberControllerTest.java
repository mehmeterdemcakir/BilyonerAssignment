package com.bilyoner.controller.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bilyoner.BilyonerApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bilyoner.model.Number;
import com.bilyoner.repository.NumberRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BilyonerApplication.class)
public class NumberControllerTest {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  
  @Autowired
  private NumberRepository numberRepository;
  
  private TestRestTemplate restTemplate = new TestRestTemplate();
  
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
  
  @Test
  public void testCreateNumberApi() throws JsonProcessingException{

	  try {
		  Map<String, Object> requestBody = new HashMap<String, Object>();
		  requestBody.put("sayi", "18");
		  requestBody.put("eklenmeTarihi",dateFormat.format(Calendar.getInstance().getTime()));
		 

		  HttpHeaders requestHeaders = new HttpHeaders();
		  requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		  HttpEntity<String> httpEntity = 
				  new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

		  @SuppressWarnings("unchecked")
		Map<String, Object> apiResponse = 
				  restTemplate.postForObject("http://localhost:8989/sayi", httpEntity, Map.class, Collections.EMPTY_MAP);

		  assertNotNull(apiResponse);

		  String message = apiResponse.get("message").toString();
		  assertEquals("Sayi basariyla eklendi.", message);
		  String sayi = ((Map<String, Object>)apiResponse.get("number")).get("sayi").toString();

		  assertNotNull(sayi);

		  Number numberFromDb = numberRepository.findOne(sayi);
		  assertEquals("18", numberFromDb.getSayi());


		  //Test etmek icin eklenen sayiyi sil.
		  numberRepository.delete(sayi);

	  } catch (Exception e) {
		  e.printStackTrace();
	  }

  }
  
  @Test
  public void testGetNumberDetailsApi(){
	
	  try {
		  
		  Number number;
		  number = new Number("25", dateFormat.format(Calendar.getInstance().getTime()));
		  

		  numberRepository.save(number);

		  String sayi = number.getSayi();

		  Number apiResponse = restTemplate.getForObject("http://localhost:8989/sayi/"+ sayi, Number.class);

		  assertNotNull(apiResponse);
		  assertEquals(number.getSayi(), apiResponse.getSayi());

		  numberRepository.delete(sayi);

	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
  
  @Test
  public void testDeleteNumberApi(){

	  
	  try {
		  Number number;
		  number = new Number("45",  dateFormat.format(Calendar.getInstance().getTime()));
		 

		  numberRepository.save(number);

		  String sayi = number.getSayi();

		  restTemplate.delete("http://localhost:8989/sayi/"+ sayi, Collections.EMPTY_MAP);

		  Number numberFromDb = numberRepository.findOne(sayi);
		  assertNull(numberFromDb);
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
  
}
