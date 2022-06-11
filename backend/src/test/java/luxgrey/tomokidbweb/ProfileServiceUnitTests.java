package luxgrey.tomokidbweb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import luxgrey.tomokidbweb.dto.ProfileDTOPostOrPut;
import luxgrey.tomokidbweb.exception.InvalidProfileTagsException;
import luxgrey.tomokidbweb.mapper.ProfileMapper;
import luxgrey.tomokidbweb.mapper.ProfileMapperImpl;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.repository.ProfileRepository;
import luxgrey.tomokidbweb.repository.TagRepository;
import luxgrey.tomokidbweb.service.ProfileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = ProfileMapperImpl.class)
public class ProfileServiceUnitTests {

  @Mock
  private ProfileRepository profileRepository;

  @Mock
  private TagRepository tagRepository;

  @Autowired
  private ProfileMapper profileMapper;

  private ProfileService profileService;

  @BeforeEach
  public void setUp() {
    profileService = new ProfileService(
        profileRepository, tagRepository, profileMapper
    );
  }

  @AfterEach
  public void tearDown() {
    profileService = null;
  }

  @Test
  public void whenCreateProfile_withValidArgument_thenCreatedProfileReturned() {
    ProfileDTOPostOrPut validProfileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    Profile expectedProfile = profileMapper.toProfile(validProfileDTO);
    expectedProfile.setId(1L);

    Mockito.when(
        profileRepository.save(ArgumentMatchers.any(Profile.class))
    ).thenReturn(expectedProfile);

    Mockito.when(
        tagRepository.countByIdIn(ArgumentMatchers.anyCollection())
    ).thenReturn(0L);

    Mockito.when(
        tagRepository.countByNameIn(ArgumentMatchers.anyCollection())
    ).thenReturn(0L);

    Profile savedProfile = Assertions.assertDoesNotThrow(
        () -> profileService.createProfile(validProfileDTO));
    Assertions.assertEquals(expectedProfile, savedProfile);
  }

  @Test
  public void whenCreateProfile_withExistingTagIdAndNewTagName_thenCreatedProfileReturned() {
    final Long EXISTING_TAG_ID = 1L;
    final String EXISTING_TAG_NAME = "Tag1";
    final Long NON_EXISTING_TAG_ID = 100L;
    final String NON_EXISTING_TAG_NAME = "Tag100";

    // test data setup
    ProfileDTOPostOrPut validProfileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    validProfileDTO.setTagIdsExisting(Set.of(EXISTING_TAG_ID));
    validProfileDTO.setTagNamesNew(Set.of(NON_EXISTING_TAG_NAME));

    Tag expectedTagExisting = new Tag();
    expectedTagExisting.setId(EXISTING_TAG_ID);
    expectedTagExisting.setName(EXISTING_TAG_NAME);

    Tag expectedTagNew = new Tag();
    expectedTagNew.setId(NON_EXISTING_TAG_ID);
    expectedTagNew.setName(NON_EXISTING_TAG_NAME);

    Profile expectedProfile = profileMapper.toProfile(validProfileDTO);
    expectedProfile.setId(1L);
    expectedProfile.setTags(List.of(expectedTagNew, expectedTagExisting));

    // mocks
    Mockito.when(
        tagRepository.countByIdIn(ArgumentMatchers.eq(Set.of(EXISTING_TAG_ID)))
    ).thenReturn(1L);

    Mockito.when(
        tagRepository.countByNameIn(ArgumentMatchers.eq(Set.of(NON_EXISTING_TAG_NAME)))
    ).thenReturn(0L);

    Mockito.when(
        tagRepository.getReferenceById(EXISTING_TAG_ID)
    ).thenReturn(expectedTagExisting);

    Mockito.when(
        profileRepository.save(ArgumentMatchers.any(Profile.class))
    ).thenReturn(expectedProfile);

    // assertions
    Profile savedProfile = Assertions.assertDoesNotThrow(
        () -> profileService.createProfile(validProfileDTO));
    Assertions.assertEquals(expectedProfile, savedProfile);
    Assertions.assertEquals(expectedProfile.getTags(), savedProfile.getTags());
  }

  @Test
  public void whenCreateProfile_withNewTagExistingName_thenThrowException() {
    final String EXISTING_TAG_NAME = "Tag1";
    Set<String> tagNamesNew = new HashSet<>();
    tagNamesNew.add(EXISTING_TAG_NAME);

    ProfileDTOPostOrPut validProfileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    validProfileDTO.setTagNamesNew(tagNamesNew);

    Mockito.when(
        tagRepository.countByIdIn(ArgumentMatchers.anyCollection())
    ).thenReturn(0L);
    Mockito.when(
        tagRepository.countByNameIn(ArgumentMatchers.eq(tagNamesNew))
    ).thenReturn(1L);

    Assertions.assertThrows(InvalidProfileTagsException.class,
        () -> profileService.createProfile(validProfileDTO));
  }

  @Test
  public void whenCreateProfile_withNonExistingTagId_thenThrowException() {
    final Long NON_EXISTING_TAG_ID = 100L;
    Set<Long> tagIdsNonExisting = new HashSet<>();
    tagIdsNonExisting.add(NON_EXISTING_TAG_ID);

    ProfileDTOPostOrPut validProfileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    validProfileDTO.setTagIdsExisting(tagIdsNonExisting);

    Mockito.when(
        tagRepository.countByIdIn(ArgumentMatchers.eq(tagIdsNonExisting))
    ).thenReturn(0L);

    Assertions.assertThrows(InvalidProfileTagsException.class,
        () -> profileService.createProfile(validProfileDTO));
  }

  @Test
  public void whenGetProfile_withExistingId_thenProfileReturned() {
    final Long SOUGHT_ID = 1L;
    Profile soughtProfile = new Profile();
    soughtProfile.setId(SOUGHT_ID);

    Mockito.when(
        profileRepository.findById(SOUGHT_ID)
    ).thenReturn(Optional.of(soughtProfile));

    Optional<Profile> resultOptionalProfile = profileService.getProfile(SOUGHT_ID);
    Assertions.assertTrue(resultOptionalProfile.isPresent());
    Assertions.assertEquals(SOUGHT_ID, resultOptionalProfile.get().getId());
  }

  @Test
  public void whenGetProfile_withNonExistingId_thenEmptyReturned() {
    final Long SOUGHT_ID = 100L;

    Mockito.when(
        profileRepository.findById(SOUGHT_ID)
    ).thenReturn(Optional.empty());

    Optional<Profile> resultOptionalProfile = profileService.getProfile(SOUGHT_ID);
    Assertions.assertTrue(resultOptionalProfile.isEmpty());
  }

  @Test
  public void whenDeleteProfile_withExistingId_thenTrueReturned() {
    final Long EXISTING_ID = 100L;

    Mockito.when(
        profileRepository.existsById(EXISTING_ID)
    ).thenReturn(true);

    Assertions.assertTrue(profileService.deleteProfile(EXISTING_ID));
  }

  @Test
  public void whenDeleteProfile_withNonExistingId_thenFalseReturned() {
    final Long NON_EXISTING_ID = 100L;

    Mockito.when(
        profileRepository.existsById(NON_EXISTING_ID)
    ).thenReturn(false);

    Assertions.assertFalse(profileService.deleteProfile(NON_EXISTING_ID));
  }

  @Test
  public void whenGetProfilesPage_thenProfilesPageReturned() {
    final String SOUGHT_ALIAS_NAME = "SpecialAlias";
    final List<Long> SOUGHT_TAG_IDS = Arrays.asList(1L, 3L);
    final int PAGE = 0;
    final int PAGE_SIZE = 5;

    // prepare test data
    Tag tag1 = new Tag();
    tag1.setId(SOUGHT_TAG_IDS.get(0));
    tag1.setName("Tag" + SOUGHT_TAG_IDS.get(0));

    Tag tag2 = new Tag();
    tag2.setId(SOUGHT_TAG_IDS.get(1));
    tag2.setName("Tag" + SOUGHT_TAG_IDS.get(1));

    Alias alias1 = new Alias();
    alias1.setName(SOUGHT_ALIAS_NAME + 1);
    Profile profile1 = new Profile();
    profile1.getAliases().add(alias1);
    List<Tag> profile1Tags = profile1.getTags();
    profile1Tags.add(tag1);
    profile1Tags.add(tag2);

    Alias alias2 = new Alias();
    alias1.setName(SOUGHT_ALIAS_NAME + 2);
    Profile profile2 = new Profile();
    profile2.getAliases().add(alias2);
    List<Tag> profile2Tags = profile2.getTags();
    profile2Tags.add(tag1);
    profile2Tags.add(tag2);

    List<Profile> profilesList = Arrays.asList(profile1, profile2);

    Page<Profile> pageExpected = new PageImpl<>(profilesList);

    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    // defined behavior of mocked components
    Mockito.when(
        profileRepository.findByAliasAndTagIds(
            pageable, SOUGHT_ALIAS_NAME, SOUGHT_TAG_IDS
        )).thenReturn(pageExpected);

    // test
    int returnSize = profileService.getProfilesPageByAliasAndTagIds(
        PAGE, PAGE_SIZE, SOUGHT_ALIAS_NAME, SOUGHT_TAG_IDS
    ).getSize();

    Assertions.assertEquals(profilesList.size(), returnSize);
  }
}
