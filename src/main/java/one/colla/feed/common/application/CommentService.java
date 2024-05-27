package one.colla.feed.common.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.colla.common.security.authentication.CustomUserDetails;
import one.colla.feed.common.application.dto.request.CreateCommentRequest;
import one.colla.feed.common.domain.Comment;
import one.colla.feed.common.domain.CommentRepository;
import one.colla.feed.common.domain.Feed;
import one.colla.feed.common.domain.FeedRepository;
import one.colla.global.exception.CommonException;
import one.colla.global.exception.ExceptionCode;
import one.colla.teamspace.application.TeamspaceService;
import one.colla.teamspace.domain.Teamspace;
import one.colla.teamspace.domain.UserTeamspace;
import one.colla.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
	private final TeamspaceService teamspaceService;
	private final FeedRepository feedRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public void create(
		final CustomUserDetails userDetails,
		final Long teamspaceId,
		final Long feedId,
		final CreateCommentRequest request
	) {
		UserTeamspace userTeamspace = teamspaceService.getUserTeamspace(userDetails, teamspaceId);
		Teamspace teamspace = userTeamspace.getTeamspace();
		User user = userTeamspace.getUser();

		Feed feed = feedRepository.findByIdAndTeamspace(feedId, teamspace)
			.orElseThrow(() -> new CommonException(ExceptionCode.NOT_FOUND_FEED));

		Comment newComment = Comment.of(user, feed, request.content());
		feed.addComment(newComment);
		commentRepository.save(newComment);

		log.info(
			"댓글 작성 - 팀스페이스 Id: {}, 사용자 Id: {}, 피드 Id: {}, 생성된 댓글 Id: {}",
			teamspaceId, user.getId(), feedId, newComment.getId()
		);
	}
}
