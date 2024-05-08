package one.colla.infra.redis.lastseen;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LastSeenTeamspaceServiceImpl implements LastSeenTeamspaceService {
	private final LastSeenTeamspaceRepository lastSeenTeamspaceRepository;

	@Override
	public void save(LastSeenTeamspace lastSeenTeamspace) {
		lastSeenTeamspaceRepository.save(lastSeenTeamspace);
	}

	@Override
	public Optional<LastSeenTeamspace> findByUserId(Long userId) {
		return lastSeenTeamspaceRepository.findById(userId);
	}

	@Override
	public void updateLastSeenTeamspace(Long userId, Long teamSpaceId) {
		LastSeenTeamspace lastSeenTeamspace = findOrCreateLastSeenTeamspace(userId);
		lastSeenTeamspace.updateLastSeenTeamspace(teamSpaceId);
		save(lastSeenTeamspace);
	}

	private LastSeenTeamspace findOrCreateLastSeenTeamspace(Long userId) {
		return lastSeenTeamspaceRepository.findById(userId)
			.orElseGet(() -> createLastSeenTeamspace(userId));
	}

	private LastSeenTeamspace createLastSeenTeamspace(Long userId) {
		return LastSeenTeamspace.of(userId, null);
	}
}
