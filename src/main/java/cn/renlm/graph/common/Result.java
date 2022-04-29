package cn.renlm.graph.common;

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
public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int statusCode;

	private String message;

	private T data;

	public static <R> Result<R> of(HttpStatus status) {
		return new Result<R>(status.value(), status.getReasonPhrase(), null);
	}

	public static <R> Result<R> of(HttpStatus status, String message) {
		Result<R> result = Result.of(status);
		return result.setMessage(message);
	}

	public static <R> Result<R> success() {
		return Result.of(HttpStatus.OK);
	}

	public static <R> Result<R> success(R data) {
		Result<R> result = success();
		return result.setData(data);
	}

	public static <R> Result<R> error() {
		return Result.of(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static <R> Result<R> error(String message) {
		return Result.of(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}

	public boolean isSuccess() {
		return this.statusCode == HttpStatus.OK.value();
	}
}