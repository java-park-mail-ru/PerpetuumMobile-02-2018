package server.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.messages.ChangeImageMessage;
import server.messages.MessageStates;
import server.model.User;
import server.services.FileSystemStorageService;
import server.services.UserService;
import server.storage.StorageException;
import server.storage.StorageFileNotFoundException;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static server.messages.MessageStates.BAD_DATA;

@CrossOrigin(origins = {"http://127.0.0.1:3000", "http://localhost:3000", "https://blend-front.herokuapp.com", "https://blendocu.herokuapp.com"}, allowCredentials = "true")
@Controller
public class FileUploadController {

    private final UserService userService;
    private final FileSystemStorageService storageService;

    public FileUploadController(FileSystemStorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        final Resource file = storageService.loadAsResource(filename);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/change_avatar")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                               HttpSession httpSession) {

        final String fileType = file.getContentType();
        if (!fileType.equals("image/jpeg") && !fileType.equals("image/jpg") && !fileType.equals("image/png")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(BAD_DATA);
        }

        final Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        final User currentUser = userService.getUserById(userIdInSession);
        final String currentUserImageName = currentUser.getImage();

        if (!currentUserImageName.equals("no_avatar.png")) {
            try {
                storageService.delete(currentUser.getImage());
            } catch (StorageException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }

        final String newCurrentUserImageName = UUID.randomUUID().toString() + '_' + Integer.toString(userIdInSession);

        try {
            storageService.store(file, newCurrentUserImageName);
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        currentUser.setImage(newCurrentUserImageName);
        final ChangeImageMessage changeImageMessage = new ChangeImageMessage(MessageStates.SUCCESS_UPLOAD, newCurrentUserImageName);
        return ResponseEntity.ok().body(changeImageMessage);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
