package org.openmrs.module.ezvacimmunization;

public class FileUploadBean {

protected byte data[] = new byte[1024];

	
    public byte[] getData() {
    	return data;
    }

	
    public void setData(byte[] data) {
    	this.data = data;
    }
}
