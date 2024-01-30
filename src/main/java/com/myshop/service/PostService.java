package com.myshop.service;

import com.myshop.domain.*;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.NotificationRepository;
import com.myshop.repository.PostRepository;
import com.myshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public PostDetailDto createPost(Long userId, CreatePostDto postDto) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
        Post post = postRepository.save(postDto.toEntity(userId));
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
            post.removeLike(likes.get(userId));
        }
        else { // 좋아요
            post.addLike(Like.builder().userId(userId).build());
            notificationRepository.mSave(userId, post.getUser().getId(), NotiType.LIKE.name(), postId);
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
        post.addComment(commentDto.toEntity(user));

        notificationRepository.mSave(userId, post.getUser().getId(), NotiType.COMMENT.name(), postId);

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
    }
}
