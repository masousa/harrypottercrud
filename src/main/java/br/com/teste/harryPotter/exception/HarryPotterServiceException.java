package br.com.teste.harryPotter.exception;

public class HarryPotterServiceException extends Exception {

	
	private static final long serialVersionUID = 7380176144628013346L;
	
	public HarryPotterServiceException() {
		super();
	}
	
	public HarryPotterServiceException(String message) {
		super(message);
	}

	public HarryPotterServiceException(String message, Throwable e) {
		super(message,e);
	}

}
