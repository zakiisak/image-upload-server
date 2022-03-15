package dk.goombah.backend.controllers;

import dk.goombah.backend.BackendApplication;
import dk.goombah.backend.FileUploadManager;
import dk.goombah.backend.models.KeyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UploadController {


    //Key / Directory name
    private Map<String, String> keyDirectories = new HashMap<String, String>();
    // The reversed version of above - Directory name / Key
    private Map<String, String> directoryKeys = new HashMap<String, String>();

    private static final String delimeter = " § ";

    private void startSavingTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(BackendApplication.running)
                {

                    String output = "";

                    for(Map.Entry<String, String> entry : keyDirectories.entrySet()) {
                        String key = entry.getKey();
                        String name = entry.getValue();

                        output += key + delimeter + name.replaceAll("\n", "").replaceAll("§", "") + "\n";
                    }

                    try {

                        File file = new File("keys.txt");
                        //if(file.exists())
                        //    file.delete();

                        Files.writeString(new File("keys.txt").toPath(), output, StandardOpenOption.CREATE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Update file once an hour
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public UploadController() {

        if(new File("keys.txt").exists())
        {
            try {
                List<String> lines = Files.readAllLines(new File("keys.txt").toPath());

                for(String line : lines)
                {
                    String[] parts = line.split(delimeter);
                    if(parts.length > 1)
                    {
                        String key = parts[0];
                        String name = parts[1];

                        keyDirectories.put(key, name);
                        directoryKeys.put(name, key);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        startSavingTimer();

    }

    @PostMapping("/upload/{key}")
    //Image saving
    public void uploadImages(@PathVariable String key, @RequestParam("file") MultipartFile[] images) {

        if(keyDirectories.containsKey(key))
        {
            String directoryName = keyDirectories.get(key);
            for(MultipartFile file : images)
            {
                FileUploadManager.saveFile(file, directoryName);
            }

        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "key " + key + " is not a valid key");
    }

    @GetMapping("/getMapping/{key}")
    public KeyResponse getMapping(@PathVariable String key) {
        KeyResponse response = new KeyResponse();

        if(keyDirectories.containsKey(key)) {

            response.setKey(key);
            response.setDirectoryName(keyDirectories.get(key));
            response.setShareUrl(BackendApplication.getBackendUrl() + "upload.html?key=" + key);
            response.setImages(FileUploadManager.getFileUrlsInDirectory(response.getDirectoryName()));

            return response;
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Key " + key + " doesn't exist!");
    }

    @GetMapping("/createMapping/{name}")
    public KeyResponse createMapping(@PathVariable String name)
    {
        if(directoryKeys.containsKey(name))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That directory name is already in use");
        }
        else {

            if(FileUploadManager.isDirectoryNameSuitable(name)) {

                String key = null;
                do {
                    key = UUID.randomUUID().toString();
                } while (keyDirectories.containsKey(key));

                keyDirectories.put(key, name);
                directoryKeys.put(name, key);

                KeyResponse response = new KeyResponse();
                response.setKey(key);
                response.setDirectoryName(name);
                response.setShareUrl(BackendApplication.getBackendUrl() + "upload.html?key=" + key);
                return response;
            }
            else throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "That name is not suitable for a directory");
        }
    }



}
