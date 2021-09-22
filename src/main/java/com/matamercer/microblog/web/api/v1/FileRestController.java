package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.services.FileService;
import com.matamercer.microblog.storage.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
public class FileRestController {

    private final FileService fileService;

    @Autowired
    public FileRestController(FileService fileService) {
        this.fileService = fileService;
    }

//    @GetMapping("/")
//    public String listUploadedFiles(Model model) throws IOException {
//
////        model.addAttribute("files", storageService.loadAll().map(
////                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
////                        "serveFile", path.getFileName().toString()).build().toUri().toString())
////                .collect(Collectors.toList()));
//
//        return "uploadForm";
//    }

    @GetMapping("/serve/{fileId}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable long fileId, @PathVariable String filename) {
        Resource fileResource = fileService.getResourceFromFile(fileService.getFile(fileId));
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + fileResource.getFilename() + "\"").body(fileResource);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}