package com.example.mychat.exception;

/**
 * Clase excepción personalizada BusinessLogic
 * @author cperez
 */
public class JwtServiceException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public JwtServiceException(String mensaje) {
		super(mensaje);
	}
}
