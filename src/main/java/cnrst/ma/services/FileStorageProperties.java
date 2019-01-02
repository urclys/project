package cnrst.ma.services;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
	private String contratUploadDir;
	private String rapportUploadDir;
	public String getContratUploadDir() {
		return contratUploadDir;
	}
	public void setContratUploadDir(String contratUploadDir) {
		this.contratUploadDir = contratUploadDir;
	}
	public String getRapportUploadDir() {
		return rapportUploadDir;
	}
	public void setRapportUploadDir(String rapportUploadDir) {
		this.rapportUploadDir = rapportUploadDir;
	}
    
}
