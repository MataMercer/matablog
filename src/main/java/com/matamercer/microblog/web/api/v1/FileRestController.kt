package com.matamercer.microblog.web.api.v1

import org.springframework.beans.factory.annotation.Autowired
import com.matamercer.microblog.services.FileService
import org.springframework.http.ResponseEntity
import com.matamercer.microblog.storage.StorageFileNotFoundException
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/files")
class FileRestController @Autowired constructor(private val fileService: FileService) {
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
    fun serveFile(@PathVariable fileId: Long, @PathVariable filename: String?): ResponseEntity<Resource> {
        val fileResource = fileService.getResourceFromFile(fileService.getFile(fileId))
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).header(
            HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + fileResource.filename + "\""
        ).body(fileResource)
    }

    @ExceptionHandler(StorageFileNotFoundException::class)
    fun handleStorageFileNotFound(exc: StorageFileNotFoundException?): ResponseEntity<*> {
        return ResponseEntity.notFound().build<Any>()
    }
}