package net.miarma.api.microservices.huertos.mail;

import java.util.Arrays;
import java.util.Base64;

public class Attachment {
	private String filename;
	private String mimeType;
	private transient byte[] data;
	
	public Attachment() {}
	
	public Attachment(String filename, String mimeType, byte[] data) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.data = data;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String getBase64Data() {
		return Base64.getEncoder().encodeToString(data);
	}

	@Override
	public String toString() {
		return "AttachmentEntity [filename=" + filename + ", mimeType=" + mimeType + ", data=" + Arrays.toString(data)
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attachment other = (Attachment) obj;
		return Arrays.equals(data, other.data);
	}
}
