package com.bnhp.controller;

import com.bnhp.service.SchemasRegistryCreatorService;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/create")
public class CreatorController {

	@Autowired
	SchemasRegistryCreatorService simplyJsonCreator;

	/**
	 * Example of Json
	 * {
	 *     "name":"persnName",
	 *     "age":22,
	 *     "worker":true,
	 *     "tech":{
	 *         "ver":1,
	 *         "techName":"kafka stream"
	 *         "knowl":true
	 *     }
	 * }
	 * @param json
	 * @return
	 */
	@PostMapping("/json/inner")
	public ResponseApi getSchemaFromJson(@RequestBody String json) {
	ResponseApi res = new ResponseApi();
		JsonSchema sc = null;
	try {
		 sc = simplyJsonCreator.createSchemaFromInnerClassJson(json);
		res.setData(sc.toString());
		res.setErrors(ResponseApi.EMPTY_STRING_ARRAY);
	}catch (Exception e) {
		e.printStackTrace();
		res.setData(ResponseApi.EMPTY_STRING_ARRAY);
		res.setErrors("Error code: 001 can not create Json Schema Registry " + e.getMessage());
	}
	return res;
	}
	
}
