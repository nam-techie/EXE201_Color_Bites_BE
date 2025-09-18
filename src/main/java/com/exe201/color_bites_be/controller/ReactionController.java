package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.response.ReactionResponse;
import com.exe201.color_bites_be.dto.response.ReactionSummaryResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.service.IReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@Tag(name = "Reaction Management", description = "APIs quản lý reaction system")
@PreAuthorize("hasAuthority('USER')")
public class ReactionController {

    private final IReactionService reactionService;


    @PostMapping("/toggle/{postId}")
    @Operation(summary = "Toggle like/unlike bài viết", 
               description = "Tự động detect trạng thái hiện tại và toggle reaction")
    public ResponseDto<Map<String, Object>> toggleReaction(@PathVariable String postId) {
        try {
            boolean isLiked = reactionService.toggleReaction(postId);
            long reactionCount = reactionService.getReactionCount(postId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("isLiked", isLiked);
            result.put("reactionCount", reactionCount);
            result.put("action", isLiked ? "liked" : "unliked");
            
            String message = isLiked ? 
                "Đã thích bài viết thành công" : 
                "Đã bỏ thích bài viết thành công";
                
            return new ResponseDto<>(HttpStatus.OK.value(), message, result);
            
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật reaction", null);
        }
    }

    /**
     * Lấy danh sách người đã like bài viết
     */
    @GetMapping("/post/{postId}/users")
    @Operation(summary = "Lấy danh sách người đã like bài viết", 
               description = "Phân trang danh sách người đã react bài viết")
    public ResponseDto<Page<ReactionResponse>> getReactionsByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReactionResponse> reactions = reactionService.getReactionsByPost(postId, pageable);
            
            return new ResponseDto<>(HttpStatus.OK.value(), 
                    "Danh sách người đã like được tải thành công", reactions);
                    
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách reaction", null);
        }
    }


    @GetMapping("/post/{postId}/summary")
    @Operation(summary = "Lấy tổng hợp thông tin reaction", 
               description = "Bao gồm: tổng số, trạng thái user, người react gần đây")
    public ResponseDto<ReactionSummaryResponse> getReactionSummary(@PathVariable String postId) {
        try {
            ReactionSummaryResponse summary = reactionService.getReactionSummary(postId);
            
            return new ResponseDto<>(HttpStatus.OK.value(), 
                    "Thông tin reaction được tải thành công", summary);
                    
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin reaction", null);
        }
    }

//    /**
//     * Lấy danh sách bài viết user đã like
//     */
//    @GetMapping("/user/{accountId}/posts")
//    @Operation(summary = "Lấy bài viết user đã like",
//               description = "Danh sách ID bài viết user đã react")
//    public ResponseDto<Page<String>> getPostsLikedByUser(
//            @PathVariable String accountId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        try {
//            Pageable pageable = PageRequest.of(page, size);
//            Page<String> postIds = reactionService.getPostsLikedByUser(accountId, pageable);
//
//            return new ResponseDto<>(HttpStatus.OK.value(),
//                    "Danh sách bài viết đã like được tải thành công", postIds);
//
//        } catch (Exception e) {
//            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    "Đã xảy ra lỗi khi lấy danh sách bài viết", null);
//        }
//    }

    /**
     * Lấy danh sách bài viết user hiện tại đã like
     */
    @GetMapping("/my-liked-posts")
    @Operation(summary = "Lấy bài viết tôi đã like", 
               description = "Danh sách bài viết user hiện tại đã react")
    public ResponseDto<Page<String>> getMyLikedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<String> postIds = reactionService.getPostsLikedByUser(null, pageable);
            
            return new ResponseDto<>(HttpStatus.OK.value(), 
                    "Danh sách bài viết bạn đã like được tải thành công", postIds);
                    
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách bài viết", null);
        }
    }

    /**
     * Kiểm tra trạng thái reaction của user cho bài viết
     */
    @GetMapping("/post/{postId}/status")
    @Operation(summary = "Kiểm tra trạng thái reaction", 
               description = "Kiểm tra user hiện tại đã like bài viết chưa")
    public ResponseDto<Map<String, Object>> getReactionStatus(@PathVariable String postId) {
        try {
            boolean hasReacted = reactionService.hasUserReacted(postId, null);
            long reactionCount = reactionService.getReactionCount(postId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("hasReacted", hasReacted);
            result.put("reactionCount", reactionCount);
            result.put("reactionType", hasReacted ? "LOVE" : null);
            
            return new ResponseDto<>(HttpStatus.OK.value(), 
                    "Trạng thái reaction được tải thành công", result);
                    
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi kiểm tra trạng thái reaction", null);
        }
    }
}
