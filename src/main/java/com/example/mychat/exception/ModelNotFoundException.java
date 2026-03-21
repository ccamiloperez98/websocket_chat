package com.example.mychat.exception;

/**
 * Clase excepción personalizada ModelNotFound
 * @author @cperez
 */
public class ModelNotFoundException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public ModelNotFoundException(String msj) {
		super(msj);
	}
}
