package server.controller;

//import java.io.IOException;
//import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.messages.ChangeImageMessage;
import server.messages.MessageStates;
import server.services.FileSystemStorageService;
import server.services.UserService;
import server.storage.StorageFileNotFoundException;

import javax.servlet.http.HttpSession;

@CrossOrigin(origins = {"http://127.0.0.1:3000", "http://localhost:3000", "https://blend-front.herokuapp.com", "https://blendocu.herokuapp.com"}, allowCredentials = "true")
@Controller
public class FileUploadController {

    private final UserService userService;
    private final FileSystemStorageService storageService;

    public FileUploadController(FileSystemStorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    /*
    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }
    */

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/change_avatar")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                               HttpSession httpSession) {
        Integer userIdInSession = (Integer) httpSession.getAttribute("blendocu");

        System.out.println("Change_avatar");
        String fileName = userIdInSession + file.getOriginalFilename();
        System.out.println(fileName);
        storageService.store(file, fileName);
        userService.getUserById(userIdInSession).setImage(fileName);

        ChangeImageMessage changeImageMessage = new ChangeImageMessage(MessageStates.SUCCESS_UPLOAD, fileName);
        return ResponseEntity.ok().body(changeImageMessage);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}