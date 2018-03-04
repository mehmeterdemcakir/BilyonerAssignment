package com.bilyoner.controller;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bilyoner.model.Number;
import com.bilyoner.repository.NumberRepository;



@RestController
@RequestMapping("/sayi")
public class NumberController{

  @Autowired
  private NumberRepository numberRepository;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  
  /**
   * DB'ye istenen Number objesini POST request olarak gonderip, kaydeder.
   * 
   * Postman Chrome Eklentisi vb. Program ile Asagidaki gibi JSON raw data
   * localhost:8989/sayi/ adresine gönderilir.
   * {
   * "sayi": "35",
   * "eklenmeTarihi": "2018.03.04 10:58:57"
   * }
   * Istenirse yazdigim test class'ı da kullanilabilir.
   */
  @RequestMapping(method = RequestMethod.POST)
  public Map<String, Object> createNumber(@RequestBody Map<String, Object> numberMap){
    Number number = new Number(numberMap.get("sayi").toString(), 
    						  (numberMap.get("eklenmeTarihi")).toString());
    
    Map<String, Object> response = new LinkedHashMap<String, Object>();
    if(getNumberDetails(Integer.parseInt(number.getSayi())) == null) {
    	 response.put("message", "Sayi basariyla eklendi.");
    	 response.put("number", numberRepository.save(number));
    }else {
    	 response.put("message", "Eklemek istediginiz sayi DB'de kayitli!");
    }
   
    return response;
  }
  
  /**
   * 
   * DB'den parametrede verilen sayinin icinde oldugu Number objesini döndürür.
   * localhost:8989/sayi/{Getirilmesi Istenen Number Objesinin Icindeki Sayi}
   */
  @RequestMapping(method = RequestMethod.GET, value="/{sayi}")
  public Number getNumberDetails(@PathVariable("sayi") Integer sayi){
    return numberRepository.findOne(sayi.toString());
  }
  
  /**
   * 
   * DB'den parametrede verilen sayinin icinde oldugu Number objesini siler.
   * Silme islemi sonucunu mesaj ile kullaniciya iletir.
   * localhost:8989/sayi/{Silinmesi Istenen Sayi}
   */
  @RequestMapping(method = RequestMethod.DELETE, value="/{sayi}")
  public Map<String, String> deleteNumber(@PathVariable("sayi") String sayi){
    Map<String, String> response = new HashMap<String, String>();  
	if(numberRepository.exists(sayi)) {
		 numberRepository.delete(sayi);
		 response.put("message", "Sayi DB'den basariyla silindi.");
	}else {
		 response.put("message", "Sayi DB'de kayitli olmadigi icin silinemedi.");
	}
   
    return response;
  }
  
  /**
   * 
   * DB'deki butun Number kayitlarini sirali olarak döndürür.
   * Kullanimi: localhost:8989/sayi/getAllNumbers/sorted/?sortingType=desc ise Buyukten - Kucuge
   * 			localhost:8989/sayi/getAllNumbers/sorted/?sortingType=asc  ise Kucukten - Buyuge
   * 			localhost:8989/sayi/getAllNumbers/sorted/ ise default olarak asc oldugu icin Kucukten - Buyuge 
   *  sirali olarak collection'daki tum sayilari döndürür.
   */
  @RequestMapping(method = RequestMethod.GET,value="/getAllNumbers/sorted/")
  public List<Number> getAllNumbers(@RequestParam(value = "sortingType") Optional<String> sortingType){
	List<Number> numbers = null;  
	if(!sortingType.equals(Optional.empty())) {
		if(sortingType.get().equalsIgnoreCase("asc")){
		    numbers = numberRepository.findAll(new Sort(Sort.Direction.ASC, "sayi"));
		}else if(sortingType.get().equalsIgnoreCase("desc")) {
		    numbers = numberRepository.findAll(new Sort(Sort.Direction.DESC, "sayi"));
		}
	}else{
      numbers = numberRepository.findAll(new Sort(Sort.Direction.ASC, "sayi"));
	}
		
	
	
    return numbers;
  }
  
  /**
   * 
   * DB'deki max. sayiyi döndürür.
   * Kullanim: localhost:8989/sayi/getMaxNumber
   */
  @RequestMapping(method = RequestMethod.GET, value="/getMaxNumber")
  public String getMaxNumber() {
	  final Query query = new Query()
			  .limit(1)
			  .with(new Sort(Sort.Direction.DESC, "sayi"));

	  return mongoTemplate.findOne(query, Number.class).getSayi();

  }
  
  /**
   * 
   * DB'deki min. sayiyi döndürür.
   * Kullanim: localhost:8989/sayi/getMinNumber
   */
  @RequestMapping(method = RequestMethod.GET, value="/getMinNumber")
  public String getMinNumber() {
	  final Query query = new Query()
			  .limit(1)
			  .with(new Sort(Sort.Direction.ASC, "sayi"));

	  return mongoTemplate.findOne(query, Number.class).getSayi();

  }
}
