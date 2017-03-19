package com.apppubs.d20.exception;

import java.io.IOException;

/**
 * 外部存储不可用异常 External Storage Unavailable Exception
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class ESUnavailableException extends IOException {
	public ESUnavailableException() {
		super();
	}

	public ESUnavailableException(String detailMessage) {
		super(detailMessage);
	}
	
}
