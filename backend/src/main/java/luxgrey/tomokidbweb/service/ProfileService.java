package luxgrey.tomokidbweb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import luxgrey.tomokidbweb.dto.ProfileDTOPostOrPut;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
import luxgrey.tomokidbweb.exception.InvalidProfileTagsException;
import luxgrey.tomokidbweb.mapper.ProfileMapper;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.repository.ProfileRepository;
import luxgrey.tomokidbweb.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final TagRepository tagRepository;
  private final ProfileMapper profileMapper;

  @Autowired
  public ProfileService(
      ProfileRepository profileRepository,
      TagRepository tagRepository,
      ProfileMapper profileMapper
  ) {
    this.profileRepository = profileRepository;
    this.tagRepository = tagRepository;
    this.profileMapper = profileMapper;
  }

  public Profile createProfile(ProfileDTOPostOrPut profileDTOPostOrPut)
      throws InvalidProfileTagsException {
    Profile profileToPersist = profileMapper.toProfile(profileDTOPostOrPut);
    profileToPersist.setTags(
        validateAndBuildProfileTags(
            profileDTOPostOrPut.getTagIdsExisting(),
            profileDTOPostOrPut.getTagNamesNew()
        )
    );

    return profileRepository.save(profileToPersist);
  }

  public Optional<Profile> getProfile(Long id) {
    return profileRepository.findById(id);
  }

  public Page<ProfileDTOShort> getProfilesPageByAliasAndTagIds(
      int pageStart, int pageSize, String aliasName, Collection<Long> tagIds) {
    return this.profileMapper.toProfileDTOShortPage(
        profileRepository.findByAliasAndTagIds(
            PageRequest.of(pageStart, pageSize),
            aliasName,
            tagIds
        )
    );
  }

  private List<Tag> validateAndBuildProfileTags(Set<Long> tagIdsExisting, Set<String> tagNamesNew)
      throws InvalidProfileTagsException {
    List<Tag> tagsToPersist = new ArrayList<>();

    assertAllTagIdsInDatabase(tagIdsExisting);
    for (Long tagId : tagIdsExisting) {
      tagsToPersist.add(tagRepository.getReferenceById(tagId));
    }

    assertTagNamesNotInDatabase(tagNamesNew);
    for (String tagName : tagNamesNew) {
      Tag newTag = new Tag();
      newTag.setName(tagName);
      tagsToPersist.add(newTag);
    }

    return tagsToPersist;
  }

  /**
   * Asserts that all values in tagIds are existing IDs for Tag in the corresponding database table
   * and throws an Exception if that is not the case
   */
  private void assertAllTagIdsInDatabase(Set<Long> tagIds) throws InvalidProfileTagsException {
    if (tagIds.size() != tagRepository.countByIdIn(tagIds)) {
      throw new InvalidProfileTagsException(
          "Tried to persist Tag(s) with manually assigned ID that is not present in database");
    }
  }

  /**
   * Asserts that all values in tagNames do not yet exist as Tag names in the corresponding database
   * table and throws an Exception if that is not the case
   */
  private void assertTagNamesNotInDatabase(Set<String> tagNames)
      throws InvalidProfileTagsException {
    if (tagRepository.countByNameIn(tagNames) > 0) {
      throw new InvalidProfileTagsException(
          "Tried to persist Tag(s) with name that already exists in database");
    }
  }
}
