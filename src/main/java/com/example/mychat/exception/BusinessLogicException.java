package com.example.mychat.exception;

/**
 * Clase excepción personalizada BusinessLogic
 * @author cperez
 */
public class BusinessLogicException  extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public BusinessLogicException(String mensaje) {		
		super(mensaje);
	}
}
