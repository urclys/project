/**
 * 
 */
package cnrst.ma.services;

import cnrst.ma.exception.FileStorageException;
import cnrst.ma.exception.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

	private final Path rapportStorageLocation;
	private final Path contratStorageLocation;

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.rapportStorageLocation = Paths.get(fileStorageProperties.getRapportUploadDir()).toAbsolutePath()
				.normalize();
		this.contratStorageLocation = Paths.get(fileStorageProperties.getContratUploadDir()).toAbsolutePath()
				.normalize();
		try {
			Files.createDirectories(this.rapportStorageLocation);
			Files.createDirectories(this.contratStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}
	@Async
	public String storeFile(MultipartFile file, String fileName, String rapportOrContrat) {
		// Normalize file name
		// String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation;
			if (rapportOrContrat.equals("rapport"))
				targetLocation = this.rapportStorageLocation.resolve(fileName);
			else
				targetLocation = this.contratStorageLocation.resolve( fileName);

			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String fileName, String rapportOrContrat) {
		try {
			Path filePath;
			if (rapportOrContrat.equals("rapport"))
				filePath = this.rapportStorageLocation.resolve(fileName).normalize();
			else
				filePath = this.contratStorageLocation.resolve(fileName).normalize();

			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	public Path getRapportStorageLocation() {
		return rapportStorageLocation;
	}

	public Path getContratStorageLocation() {
		return contratStorageLocation;
	}
	
}