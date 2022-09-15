package kafka.schema.creator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kafka.schema.creator.service.SchemaRegistryCreatorService;

@RestController
@RequestMapping("/create")
public class CreatorController {

	@Autowired
	SchemaRegistryCreatorService simplyJsonCreator;
	
	@PostMapping("/json")
	public ResponseApi getSchemaFromJson(@RequestBody String json) {
	ResponseApi res = new ResponseApi();
	try {
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	return res;
	}
	
}
