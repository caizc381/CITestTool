package com.mytijian.admin.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseWrapper extends HttpServletResponseWrapper {
	private final static Logger log = LoggerFactory
			.getLogger(ResponseWrapper.class);
	private PrintWriter cachedWriter;
	private ByteArrayOutputStream bufferedWriter;
	private String charsetName = "UTF-8";
	
	private boolean responseUsed = false;

	public ResponseWrapper(HttpServletResponse response) {
		super(response);
		newOutputStream();
	}

	public ResponseWrapper(HttpServletResponse response, String charsetName) {
		this(response);
		this.charsetName = charsetName;
	}

	private void newOutputStream() {
		bufferedWriter = new ByteArrayOutputStream();
		cachedWriter = new PrintWriter(bufferedWriter);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return cachedWriter;
	}

	public String getResult() {
		try {
			cachedWriter.flush();
			String result = bufferedWriter.toString(charsetName);
			cachedWriter.close();
			bufferedWriter.close();
			return result;
		} catch (IOException e) {
			log.error(charsetName + " is not supported!", e);
			return bufferedWriter.toString();
		}
	}

	public void replaceContent(String content) throws IOException {
		newOutputStream();
		bufferedWriter.write(content.getBytes());
	}
	
	public ServletOutputStream getOriginalOutputStream() throws IOException {
		this.responseUsed = true;
		return super.getOutputStream();
	}
	
	public HttpServletResponse getOriginalResponse() {
		this.responseUsed = true;
		return (HttpServletResponse) super.getResponse();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		ServletOutputStream outputStream = new ServletOutputStream() {

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
			}

			@Override
			public void write(int b) throws IOException {
				bufferedWriter.write(b);
			}
		};
		return outputStream;
	}

	public boolean isResponseUsed() {
		return responseUsed;
	}
}
