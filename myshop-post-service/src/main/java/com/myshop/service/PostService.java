package com.myshop.service;

import com.myshop.domain.*;
import com.myshop.dto.*;
import com.myshop.global.dto.PostResponseDto;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.*;
import com.myshop.user.domain.User;
import com.myshop.global.dto.CreateNotificationDto;
import com.myshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
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
        User user = findUserById(userId);
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
        Post post = findPostById(postId);
        return PostDetailDto.of(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        validatePostOwnership(userId, postId);
        deleteNotificationsForPost(postId);
        postRepository.deleteById(postId);
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        User user = findUserById(userId);
        Post post = findPostById(postId);
        Like like = post.getLikes().stream()
                .filter(l -> l.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (like != null) {
            post.removeLike(like);
            likeRepository.delete(like);
            deleteNotificationsForLike(like);
        } else {
            addLikeAndNotification(user, post);
        }
    }

    @Transactional
    public List<CommentDto> addComment(Long userId, Long postId, CreateCommentDto commentDto) {
        User user = findUserById(userId);
        Post post = findPostById(postId);
        CreateCommentAndNotification(user, post, commentDto);
        return post.getComments().stream()
                .map(CommentDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeComment(Long userId, Long postId, Long commentId) {
        validateUser(userId);
        Post post = findPostById(postId);
        Comment comment = findCommentByIdAndPost(commentId, post);
        post.removeComment(comment, userId);
        deleteNotificationForComment(commentId);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new BadRequestException("유저 정보를 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 게시물입니다."));
    }

    private void validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다."));
    }

    private void validatePostOwnership(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 게시물입니다."));
        if (!userId.equals(post.getUser().getId())) {
            throw new BadRequestException("본인의 게시물만 삭제가 가능합니다.");
        }
    }

    private void addLikeAndNotification(User user, Post post) {
        Like like = Like.builder().userId(user.getId()).build();
        post.addLike(like);
        likeRepository.save(like);

        CreateNotificationDto request = createNotificationDto(
                user.getId(), post.getUser().getId(), "LIKE", post.getId(), like.getId()
        );
        saveNotification(request);
    }

    private void CreateCommentAndNotification(User user, Post post, CreateCommentDto commentDto) {
        Comment newComment = commentDto.toEntity(user);
        post.addComment(newComment);
        commentRepository.save(newComment);

        CreateNotificationDto request = createNotificationDto(
                user.getId(), post.getUser().getId(), "COMMENT", post.getId(), newComment.getId()
        );
        saveNotification(request);
    }

    private CreateNotificationDto createNotificationDto(Long fromUserId, Long toUserId, String type, Long postId, Long typeId) {
        CreateNotificationDto request = new CreateNotificationDto();
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setType(type);
        request.setPostId(postId);
        request.setTypeId(typeId);
        return request;
    }

    private Comment findCommentByIdAndPost(Long commentId, Post post) {
        return post.getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("해당 게시물에 존재하지 않는 댓글입니다."));
    }

    // Rest Api
    private void saveNotification(CreateNotificationDto request) {
        webClient.post()
                .uri("http://localhost:8082/api/internal/feeds/notis")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void deleteNotificationsForPost(Long postId) {
        webClient.delete()
                .uri("http://localhost:8082/api/internal/feeds/notis/post/" + postId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void deleteNotificationsForLike(Like like) {
        webClient.delete()
                .uri("http://localhost:8082/api/internal/feeds/notis/type/" + like.getId())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void deleteNotificationForComment(Long commentId) {
        webClient.delete()
                .uri("http://localhost:8082/api/internal/feeds/notis/type/" + commentId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        postRepository.deleteAllByUserId(userId);
        commentRepository.deleteAllByWriterId(userId);
        likeRepository.deleteAllByUserId(userId);
    }

    public List<PostResponseDto> getPostsByUserIds(List<Long> followingIds) {
        List<Post> posts = postRepository.findByUserIdIn(followingIds);
        return posts.stream()
                .map(this::convertToPostResponseDto)
                .collect(Collectors.toList());
    }

    private PostResponseDto convertToPostResponseDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getContent(),
                post.getUser().getName(),
                post.getUser().getProfileImg(),
                post.getUser().getId(),
                post.getLikes().size(),
                post.getComments().size(),
                post.getCreatedAt());
    }
}