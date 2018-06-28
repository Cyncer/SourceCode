package com.cync.model;

public class ChatMessage {
	
	public boolean left;
	public String message;
	public String senderName;
	public String receiverName;
	public String timestamp;
	public String isread;
	public String type;
	
	
	
	
	public ChatMessage() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ChatMessage(boolean left, String message, String senderName,
			String receiverName, String timestamp, String isread,String type) {
		super();
		this.left = left;
		this.message = message;
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.timestamp = timestamp;
		this.isread=isread;
		this.type=type;
	}
	
	
	
	
	public String getIsread() {
		return isread;
	}


	public void setIsread(String isread) {
		this.isread = isread;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public boolean isLeft() {
		return left;
	}
	public void setLeft(boolean left) {
		this.left = left;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


 
	
	
}
