package com.bnhp.controller;

import java.util.ArrayList;
import java.util.List;

public class ResponseApi {
	private Object data;
	private Object errors;
	public static final String[] EMPTY_STRING_ARRAY = new String[] {};
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Object getErrors() {
		return errors;
	}
	public void setErrors(Object errors) {
		this.errors = errors;
	}
	public static String[] getEmptyStringArray() {
		return EMPTY_STRING_ARRAY;
	}
	public ResponseApi(Object data, Object errors) {
		super();
		this.data = data;
		this.errors = errors;
	}
	public ResponseApi() {
		super();
		// TODO Auto-generated constructor stub
	}
	class ErrorData{
		private List<Error> global;
		{
			global = new ArrayList<Error>();
		}
		public ErrorData() {
			
		}
		public void addError(Error error) {
			global.add(error);
		}
		public List<Error> getGlobal(){return global;}
	}
	class Error{
		private String id;
		private String message;
		public Error(String id, String message) {
			super();
			this.setId(id);
			this.setMessage(message);
		}
		
		private void setMessage(String message) {
			this.message = message;
		}

		public String getId() {return id;}
		public void setId(String id) {this.id = id;};
		public String getMessage() {return message;}
		
	}

}
