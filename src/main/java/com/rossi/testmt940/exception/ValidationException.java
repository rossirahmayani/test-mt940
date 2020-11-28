package com.rossi.testmt940.exception;

import com.rossi.testmt940.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final ResponseCode responseCode;
}
