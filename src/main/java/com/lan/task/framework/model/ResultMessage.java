package com.lan.task.framework.model;

/**
 * 
 * @author LAN
 * @date 2018年9月6日
 */
public class ResultMessage {
	
	private boolean success;
	
	private String code;
	
	private String message;
	
	private Object data;

	public ResultMessage() {
		super();
	}
	
	public ResultMessage(boolean success) {
		super();
		this.success = success;
	}
	
	public ResultMessage(boolean success, Object data) {
		super();
		this.success = success;
		this.data = data;
	}
	
	public ResultMessage(boolean success, Object data, String message) {
		super();
		this.success = success;
		this.data = data;
		this.message = message;
	}
	
	public ResultMessage(boolean success, Object data, String message, String code) {
		super();
		this.success = success;
		this.data = data;
		this.message = message;
		this.code = code;
	}
	
	
	public static ResultMessage success(){
		return new ResultMessage(true);
	}
	
	public static ResultMessage success(Object data){
		return new ResultMessage(true, data);
	}
	
	public static ResultMessage success(Object data, String message){
		return new ResultMessage(true, data, message);
	}
	
	public static ResultMessage error(){
		return new ResultMessage(false);
	}
	
	public static ResultMessage error(String message){
		return new ResultMessage(false, null, message);
	}
	
	public static ResultMessage error(String code, String message){
		return new ResultMessage(false, null, message, code);
	}

	public static ResultMessage error(String code, String message, Object data){
		return new ResultMessage(false, data, message, code);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
