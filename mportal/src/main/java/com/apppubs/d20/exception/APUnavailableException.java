package com.apppubs.d20.exception;

import java.io.IOException;

/**
 * 外部存储不可用异常 External Storage Unavailable Exception
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class APUnavailableException extends IOException {
	public APUnavailableException() {
		super();
	}

	public APUnavailableException(String detailMessage) {
		super(detailMessage);
	}
	
}
