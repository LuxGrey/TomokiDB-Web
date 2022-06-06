package luxgrey.tomokidbweb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import luxgrey.tomokidbweb.dto.ProfileDTONoId;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
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

  @Autowired
  public ProfileService(
      ProfileRepository profileRepository,
      TagRepository tagRepository
  ) {
    this.profileRepository = profileRepository;
    this.tagRepository = tagRepository;
  }

  public Profile createProfile(ProfileDTONoId profileDTONoId) {
    Profile profileToPersist = ProfileDTONoId.toProfile(profileDTONoId);
    validateAndCorrectProfileTagsForWrite(profileToPersist);

    return profileRepository.save(profileToPersist);
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

  /**
   * Validates and corrects a Profile instance that was created as port of a POST or PUT.
   * <p>
   * Ensures that every Tag contained in profile.tags either has an ID (for assigning an existing
   * Tag) or a name (for creating and assigning a new Tag), but not both (which would cause the name
   * of an existing Tag to be overwritten)
   *
   * @throws IllegalArgumentException if any of the Profile's tags are found to be invalid
   */
  private void validateAndCorrectProfileTagsForWrite(Profile profile) throws IllegalArgumentException {
    Set<Long> tagIds = new HashSet<>();
    List<Tag> tagsWithId = new ArrayList<>();

    Set<String> tagNames = new HashSet<>();
    List<Tag> tagsWithOnlyName = new ArrayList<>();

    // first validation stage: undesired attributes and duplicates
    for (Tag tag : profile.getTags()) {
      if (tag.getId() != null) {
        assertValidTagWithIdOnly(tag, tagIds);
        tagIds.add(tag.getId());
        tagsWithId.add(tag);
        continue;
      }

      if (!tag.getName().isBlank()) {
        assertValidTagWithName(tag, tagNames);
        tagNames.add(tag.getName());
        tagsWithOnlyName.add(tag);
      }
    }

    // second validation stage: comparison with database contents
    assertTagNamesNotInDatabase(tagNames);
  }

  /**
   * Asserts that the provided Tag only has id set and name not set and that the id does not already
   * exist in tagIds.
   *
   * @param tagIds        a Set of Long that contains all previously encountered Tag IDs; also used
   *                      for validation
   * @throws IllegalArgumentException if Tag is not valid
   */
  private void assertValidTagWithIdOnly(Tag tagToValidate, Set<Long> tagIds)
      throws IllegalArgumentException {
    if (!tagToValidate.getName().isBlank()) {
      throw new IllegalArgumentException(
          "Tried to persist Tag that contains both id (" + tagToValidate.getId() + ") and name ("
              + tagToValidate.getName() + ")"
      );
    }

    if (tagIds.contains(tagToValidate.getId())) {
      throw new IllegalArgumentException(
          "Tried to persist more than 1 Tag with same id: " + tagToValidate.getId());
    }
  }

  /**
   * Asserts that the provided Tag only has name set and that the name does not already exist in
   * tagIds.
   *
   * @param tagNames      a Set of String that contains all previously encountered Tag names,
   *                      used for validation
   * @throws IllegalArgumentException if Tag is not valid
   */
  private void assertValidTagWithName(Tag tagToValidate, Set<String> tagNames)
      throws IllegalArgumentException {
    if (tagNames.contains(tagToValidate.getName())) {
      throw new IllegalArgumentException(
          "Tried to persist more than 1 Tag with same id: " + tagToValidate.getId());
    }
  }

  private void assertTagNamesNotInDatabase(Collection<String> tagNames)
      throws IllegalArgumentException {
    if(tagRepository.countByNameIn(tagNames) > 0) {
      throw new IllegalArgumentException(
          "Tried to persist Tag(s) with name that already exists in database");
    }
  }
}
