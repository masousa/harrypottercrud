package br.com.teste.harryPotter.controller.domain;

import java.io.Serializable;

public class Message implements Serializable{

	
	private static final long serialVersionUID = 904580124116256409L;
	
	private String message;
	private MessageType typeMessage;
	
	
	
	public Message() {
		super();
	}
	
	public Message(String message) {
		super();
		this.message = message;
		
	}
	
	public Message(String message, MessageType typeMessage) {
		super();
		this.message = message;
		this.typeMessage = typeMessage;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public MessageType getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(MessageType typeMessage) {
		this.typeMessage = typeMessage;
	}
	
	
	

}
