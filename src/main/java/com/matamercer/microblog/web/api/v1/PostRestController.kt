package com.matamercer.microblog.web.api.v1

import com.matamercer.microblog.models.entities.File
import com.matamercer.microblog.security.CurrentUser
import com.matamercer.microblog.security.UserPrincipal
import com.matamercer.microblog.services.LikeService
import com.matamercer.microblog.services.PostService
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*
import java.util.stream.Collectors
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/post")
class PostRestController @Autowired constructor(
    private val postService: PostService,
    private val likeService: LikeService
) {
    @GetMapping("/{id}")
    fun getPost(@PathVariable id: String): ResponseEntity<PostResponseDto> {
        return ResponseEntity.ok(postService.getPostResponseDto(id.toLong()))
    }

    @GetMapping("/")
    fun getPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(name = "blogName", required = false) optionalBlogName: String?,
        @RequestParam(name = "category", required = false) optionalCategory: String?,
        @RequestParam(name = "tags", required = false) optionalTagNames: List<String>?
    ): ResponseEntity<Page<PostResponseDto>> {
        val posts = postService.searchPosts(
            optionalBlogName,
            optionalCategory,
            optionalTagNames,
            PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt")
        )
        return ResponseEntity.ok(posts)
    }

    //    @GetMapping("/{id}/replies")
    //    public ResponseEntity<Page<PostResponseDto>> getPostReplies(@PathVariable String id) {
    //        return ResponseEntity.ok(postService.getPostResponseDto(Long.parseLong(id)).getReplies());
    //    }
    @GetMapping("/{id}/attachments")
    fun getFileAttachments(@PathVariable id: String?): ResponseEntity<List<URI>> {
        assert(id != null)
        val postAttachmentList = postService.getPost(
            id!!.toLong()
        ).attachments
        val postAttachmentURIList = postAttachmentList.map { postAttachment: File ->
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(
                    FileRestController::class.java
                ).serveFile(postAttachment.id!!, postAttachment.name)
            ).toUri()
        }
        return ResponseEntity.ok(postAttachmentURIList)
    }

    @PostMapping("/create")
    fun createNewPostForm(
        postRequestDto: @Valid PostRequestDto,
        @RequestParam(name = "files", required = false) files: Array<MultipartFile>?,
        @CurrentUser userPrincipal: UserPrincipal
    ): ResponseEntity<PostResponseDto> {
        val post = postService.createNewPost(postRequestDto, files ?: emptyArray(), userPrincipal.activeBlog)
        val location = ServletUriComponentsBuilder.fromCurrentRequest().path(
            "/{id}"
        ).buildAndExpand(post.id).toUri()
        return ResponseEntity.created(location).body(post)
    }

//    @PostMapping("/reply")
//    fun createReplyPostForm(
//        postRequestDto: @Valid PostRequestDto,
//        @RequestParam(name = "files", required = false) files: Array<MultipartFile>?, @CurrentUser userPrincipal: UserPrincipal
//    ): ResponseEntity<PostResponseDto> {
//        val post = postService.createReplyPost(postRequestDto, files ?: emptyArray(), userPrincipal.activeBlog)
//        val location = ServletUriComponentsBuilder.fromCurrentRequest().path(
//            "/{id}"
//        ).buildAndExpand(post.id).toUri()
//        return ResponseEntity.created(location).body(post)
//    }

    @PutMapping("/update")
    fun updatePostForm(
        updatePostRequest: @Valid PostRequestDto,
        @RequestParam(name = "files", required = false) files: Array<MultipartFile>?,
        @CurrentUser userPrincipal: UserPrincipal
    ): ResponseEntity<PostResponseDto> {
        val post = postService.updatePost(updatePostRequest, files ?: emptyArray(), userPrincipal.activeBlog)
        return ResponseEntity.ok().body(post)
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: String, @CurrentUser userPrincipal: UserPrincipal): ResponseEntity<*> {
        postService.deletePost(id.toLong())
        return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/{id}/like")
    fun likePost(@PathVariable id: String, @CurrentUser userPrincipal: UserPrincipal): ResponseEntity<*> {
        likeService.likePost(userPrincipal.activeBlog, id.toLong())
        return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/{id}/like")
    fun unlikePost(@PathVariable id: String, @CurrentUser userPrincipal: UserPrincipal): ResponseEntity<*> {
        likeService.unlikePost(userPrincipal.activeBlog, id.toLong())
        return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
    }
}