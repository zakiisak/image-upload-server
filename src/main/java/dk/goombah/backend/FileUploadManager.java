package dk.goombah.backend;

import com.amazonaws.services.appmesh.model.Backend;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class FileUploadManager {

    private static String rootDirectory = "static/files/";

    /**
     * file                 - The file for upload from a rest controller
     * localDirectoryPath   - The directory path suffix to the root directory
     * outputFileName       - The file name that will be saved to disk without the extension
     *
     * Returns the fully qualified https path to the file
     * */
    public static String saveFile(MultipartFile file, String localDirectoryPath)
    {
        String originalFileName = file.getOriginalFilename();
        int extensionStart = file.getOriginalFilename().lastIndexOf('.');
        String extensionAppendage = "";
        if(extensionStart >= 0)
        {
            extensionAppendage = file.getOriginalFilename().substring(extensionStart);
        }

        try {
            return saveFile(file.getInputStream(), localDirectoryPath, originalFileName, extensionAppendage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveFile(InputStream fileStream, String localDirectoryPath, String outputFileName, String extension)
    {
        try {
            String filePath = null;
            String fileName = "";

            do {
                fileName = outputFileName + ((int) (Math.random() * 1000000000.0)) + extension;
                filePath = rootDirectory + localDirectoryPath + "/" + fileName;
            } while(new File(filePath).exists());

            new File(filePath).mkdirs();

            Files.copy(fileStream, Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
            return BackendApplication.getBackendUrl();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getFileUrlsInDirectory(String dirName)
    {
        File[] files = new File(rootDirectory + dirName).listFiles();
        if(files == null)
            return null;
        String[] urls = new String[files.length];
        for(int i = 0; i < files.length; i++)
        {
            File file = files[i];
            urls[i] = BackendApplication.getBackendUrl() + rootDirectory.replace("static/", "") + dirName + "/" + file.getName();
        }

        return urls;
    }

    public static boolean isDirectoryNameSuitable(String dirName) {
        return new File(rootDirectory + dirName).mkdirs();
    }


}
