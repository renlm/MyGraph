package cn.renlm.mygraph.ws;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * WebSocket 消息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
public class WsMessage<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 消息类型
	 */
	private WsType type;

	/**
	 * 接收人
	 */
	private String userId;

	/**
	 * 消息内容
	 */
	private T data;

	/**
	 * 消息类型
	 */
	public enum WsType {

		oshi("服务器信息"), online("上线"), offline("离线");

		@Getter
		private String text;

		private WsType(String text) {
			this.text = text;
		}
	}

	/**
	 * 构建消息
	 * 
	 * @param <W>
	 * @param type
	 * @param message
	 * @return
	 */
	public static final <W> WsMessage<W> build(WsType type, W message) {
		return build(type, null, message);
	}

	/**
	 * 构建消息
	 * 
	 * @param <W>
	 * @param type
	 * @param userId
	 * @param message
	 * @return
	 */
	public static final <W> WsMessage<W> build(WsType type, String userId, W message) {
		WsMessage<W> wsMessage = new WsMessage<W>();
		wsMessage.setType(type);
		wsMessage.setUserId(userId);
		wsMessage.setData(message);
		return wsMessage;
	}
}