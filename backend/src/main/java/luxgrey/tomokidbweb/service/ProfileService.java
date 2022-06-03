package luxgrey.tomokidbweb.service;

import java.util.Collection;
import java.util.Optional;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

  private final ProfileRepository profileRepository;

  @Autowired
  public ProfileService(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public Optional<Profile> getProfile(Long id) {
    return profileRepository.findById(id);
  }

  public Page<ProfileDTOShort> getProfilesPageByAliasAndTagIds(
      int pageStart, int pageSize, String aliasName, Collection<Long> tagIds) {
    return ProfileDTOShort.toProfileDTOPage(
        profileRepository.findByAliasAndTagIds(
            PageRequest.of(pageStart, pageSize),
            aliasName,
            tagIds
        )
    );
  }
}
