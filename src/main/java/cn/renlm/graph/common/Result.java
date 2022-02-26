package com.lei.du.common;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 响应结果
 *
 * @author Renlm
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	private int statusCode;

	private String message;

	private Object data;

	public static Result of(HttpStatus status) {
		return new Result(status.value(), status.getReasonPhrase(), null);
	}

	public static Result success() {
		return Result.of(HttpStatus.OK);
	}

	public static Result success(Object data) {
		return success().setData(data);
	}

	public static Result error() {
		return Result.of(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static Result error(String message) {
		return Result.of(HttpStatus.INTERNAL_SERVER_ERROR).setMessage(message);
	}
}