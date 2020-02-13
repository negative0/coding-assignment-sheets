package com.example.demo.controllers;

import com.example.demo.models.Entity;
import com.example.demo.models.SheetResponseEntity;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class SheetsController {
    private static final String APPLICATION_NAME = "evento";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        File file = ResourceUtils.getFile("classpath:"+CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(file);
//                GoogleAuthorizeUtils.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @GetMapping(value = "/readSheet")
    public ResponseEntity<SheetResponseEntity> getList(@RequestParam("sheet_id") String sheetID) throws IOException, GeneralSecurityException {
        try {
            List<Entity> entityList = new ArrayList<>();

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String range = "Sheet1!A:D";
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            ValueRange response = service.spreadsheets().values()
                    .get(sheetID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            values.remove(0);
            double totalLikes = 0;
            double totalFollowers = 0;
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {

                for (List row : values) {
                    // Print columns A and E, which correspond to indices 0 and 4.
                    try {
                        int likes = Integer.parseInt((String) row.get(1));
                        int followers = Integer.parseInt((String) row.get(2));
                        totalLikes += likes;
                        totalFollowers += followers;
                        entityList.add(new Entity(
                                (String) row.get(0),
                                likes,
                                followers
                                ));
                    } catch (Exception e) {
                        System.out.println("Cannot parse Row" + e);
                    }
                }
            }
            return ResponseEntity.ok(new SheetResponseEntity(entityList, totalLikes, totalFollowers));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
