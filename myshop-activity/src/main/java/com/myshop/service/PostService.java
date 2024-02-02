package com.myshop.service;

import com.myshop.domain.*;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final WebClient webClient;

    @Transactional
    public PostDetailDto createPost(Long userId, CreatePostDto postDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        Post post = postRepository.save(postDto.toEntity(user));
        return getPostById(post.getId());
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDetailDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 게시물입니다.")
        );
        return PostDetailDto.of(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 게시물입니다.")
        );
        if (!userId.equals(post.getUser().getId())) {
            throw new BadRequestException("본인의 게시물만 삭제가 가능합니다.");
        }
        webClient.delete()
                .uri("http://localhost:8081/api/internal/feeds/notis/post/" + postId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        postRepository.deleteById(postId);
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 게시물입니다.")
        );

        boolean alreadyLike = post.getLikes()
                .stream()
                .map(Like::getUserId)
                .anyMatch(uid -> uid == userId);

        if (alreadyLike) { // 이미 좋아요 했다면 취소
            Map<Long, Like> likes = post.getLikes()
                    .stream()
                    .collect(Collectors.toMap(Like::getUserId, Function.identity()));
            Like like = likes.get(userId);
            post.removeLike(like);
            webClient.delete()
                    .uri("http://localhost:8081/api/internal/feeds/notis/type/" + like.getId())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
        else { // 좋아요
            Like like = Like.builder().userId(userId).build();
            post.addLike(like);
            likeRepository.save(like);

            // 알림 저장 restApi
            NotificationCreateRequest request = new NotificationCreateRequest();
            request.setFromUserId(userId);
            request.setToUserId(post.getUser().getId());
            request.setType("LIKE");
            request.setPostId(postId);
            request.setTypeId(like.getId());

            webClient.post()
                    .uri("http://localhost:8081/api/internal/feeds/notis")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }
    }

    @Transactional
    public List<CommentDto> addComment(Long userId, Long postId, CreateCommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 게시물입니다.")
        );
        Comment newComment = commentDto.toEntity(user);
        post.addComment(newComment);
        commentRepository.save(newComment);

        // 알림 저장 restApi
        NotificationCreateRequest request = new NotificationCreateRequest();
        request.setFromUserId(userId);
        request.setToUserId(post.getUser().getId());
        request.setType("COMMENT");
        request.setPostId(postId);
        request.setTypeId(newComment.getId());

        webClient.post()
                .uri("http://localhost:8081/api/internal/feeds/notis")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();

        return post.getComments().stream()
                .map(CommentDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeComment(Long userId, Long postId, Long commentId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 게시물입니다.")
        );
        Map<Long, Comment> comments = post.getComments()
                .stream()
                .collect(Collectors.toMap(Comment::getId, Function.identity()));

        if (!comments.containsKey(commentId)) {
            throw new BadRequestException("댓글 삭제는 댓글을 단 게시물에만 가능합니다.");
        }
        post.removeComment(comments.get(commentId), userId);
        webClient.delete()
                .uri("http://localhost:8081/api/internal/feeds/notis/type/" + commentId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // Rest Api
    @Transactional
    public void deleteAllByUserId(Long userId) {
        postRepository.deleteAllByUserId(userId);
        commentRepository.deleteAllByWriterId(userId);
        likeRepository.deleteAllByUserId(userId);
    }

    // 사용자 ID 목록을 기반으로 PostResponseDto 목록을 조회하고 반환
//    public List<PostResponseDto> getPostsByUserIds(List<Long> userIds) {
//        List<Post> posts = postRepository.findByUserIdIn(userIds);
//        return posts.stream()
//                .map(PostDto::of)
//                .map(this::convertToPostResponseDto)
//                .collect(Collectors.toList());
//    }

    public List<PostResponseDto> getPostsByUserIds(List<Long> userIds) {
        // 사용자 ID 목록 출력
        System.out.println("Fetching posts for userIds: " + userIds);

        List<Post> posts = postRepository.findByUserIdIn(userIds);

        // 조회된 Post 목록 출력
        System.out.println("Retrieved posts size: " + posts.size());

        List<PostResponseDto> postResponseDtos = posts.stream()
                .map(PostDto::of)
                .map(postDto -> {
                    // PostDto를 PostResponseDto로 변환 전 출력
                    System.out.println("Converting PostDto to PostResponseDto:" + postDto.toString());
                    return convertToPostResponseDto(postDto);
                })
                .collect(Collectors.toList());

        // 최종 변환된 PostResponseDto 목록 출력
        System.out.println("Converted PostResponseDtos: " + postResponseDtos.toString());

        return postResponseDtos;
    }

    // PostDto를 PostResponseDto로 변환하는 메서드
    private PostResponseDto convertToPostResponseDto(PostDto postDto) {
        return PostResponseDto.builder()
                .id(postDto.getId())
                .content(postDto.getContent())
                .name(postDto.getName())
                .profileImg(postDto.getProfileImg())
                .userId(postDto.getUserId())
                .likeCount(postDto.getLikeCount())
                .commentCount(postDto.getCommentCount())
                .createdDate(postDto.getCreatedDate())
                .build();
    }


}
