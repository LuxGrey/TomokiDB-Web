package luxgrey.tomokidbweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import luxgrey.tomokidbweb.dto.ProfileDTOPostOrPut;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class ProfileControllerIntegrationTests {

  private final String PROFILES_BASE_URL = "/api/profiles";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  public void whenCreateProfile_withValidTags_thenResponseBodyHasExpectedContents()
      throws Exception {
    List<Tag> existingTags = TestModelHelper.createTags(2);
    Set<Long> existingTagIds = new HashSet<>();
    for (Tag tag : existingTags) {
      existingTagIds.add(
          (Long) testEntityManager.persistAndGetId(tag)
      );
    }
    testEntityManager.flush();

    ProfileDTOPostOrPut profileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    profileDTO.setTagIdsExisting(existingTagIds);
    profileDTO.setTagNamesNew(Set.of("Tag100", "Tag200"));
    ObjectMapper objectMapper = new ObjectMapper();
    String payload = objectMapper.writeValueAsString(profileDTO);

    this.mockMvc
        .perform(MockMvcRequestBuilders.post(PROFILES_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .characterEncoding("utf-8")
            .content(payload))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("aliases").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("aliases").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("weblinks").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("weblinks").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("tags").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("tags").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("tags[0].profiles").isEmpty());
  }

  @Test
  public void whenCreateProfile_withInvalidTags_thenStatusBadRequest() throws Exception {
    List<Tag> existingTags = TestModelHelper.createTags(2);
    Set<String> existingTagNames = new HashSet<>();
    for (Tag tag : existingTags) {
      testEntityManager.persist(tag);
      existingTagNames.add(tag.getName());
    }
    testEntityManager.flush();

    ProfileDTOPostOrPut profileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    profileDTO.setTagIdsExisting(Set.of(100L, 200L));
    profileDTO.setTagNamesNew(existingTagNames);
    ObjectMapper objectMapper = new ObjectMapper();
    String payload = objectMapper.writeValueAsString(profileDTO);

    this.mockMvc
        .perform(MockMvcRequestBuilders.post(PROFILES_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .characterEncoding("utf-8")
            .content(payload))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void givenProfile_whenGetProfile_withValidId_thenResponseBodyHasExpectedContents()
      throws Exception {
    final int AMOUNT_ALIASES = 2;
    final int AMOUNT_WEBLINKS = 4;
    final int AMOUNT_TAGS = 3;

    Profile profile = TestModelHelper.createProfileWithAliasesAndWeblinks(
        AMOUNT_ALIASES, AMOUNT_WEBLINKS, 1);
    List<Tag> tags = TestModelHelper.createTags(AMOUNT_TAGS);
    for (Tag tag : tags) {
      testEntityManager.persist(tag);
    }
    profile.getTags().addAll(tags);
    testEntityManager.persistAndFlush(profile);

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "/" + profile.getId()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("aliases").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("aliases.size()").value(AMOUNT_ALIASES))
        .andExpect(MockMvcResultMatchers.jsonPath("weblinks").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("weblinks.size()").value(AMOUNT_WEBLINKS))
        .andExpect(MockMvcResultMatchers.jsonPath("tags").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("tags.size()").value(AMOUNT_TAGS))
        .andExpect(MockMvcResultMatchers.jsonPath("tags[0].profiles").isEmpty());
  }

  @Test
  public void givenProfile_whenDeleteProfile_withValidId_thenProfileDeletedAndStatus200()
      throws Exception {
    Profile existingProfile = TestModelHelper.createProfileWithAliasesAndWeblinks(2, 3, 1);
    Long existingProfileId = (Long) testEntityManager.persistAndGetId(existingProfile);;
    testEntityManager.flush();

    // record exists in database
    Assertions.assertNotNull(testEntityManager.find(Profile.class, existingProfileId));

    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(PROFILES_BASE_URL + "/" + existingProfileId))
        .andExpect(MockMvcResultMatchers.status().isOk());

    // record no longer exists in database
    Assertions.assertNull(testEntityManager.find(Profile.class, existingProfileId));
  }

  @Test
  public void whenDeleteProfile_withNonExistingId_thenStatus404() throws Exception {
    final Long NON_EXISTING_PROFILE_ID = 100L;

    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(PROFILES_BASE_URL + "/" + NON_EXISTING_PROFILE_ID))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void whenDeleteProfile_withNegativeId_thenStatus400() throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(PROFILES_BASE_URL + "/-1"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void givenProfiles_whenGetProfilesPageByAliasAndTagIds_withPageAndPageSize_thenResponseBodyHasExpectedContents()
      throws Exception {
    final int AMOUNT_PROFILES = 2;
    final int AMOUNT_ALIASES_PER_PROFILE = 2;
    final int AMOUNT_WEBLINKS_PER_PROFILE = 4;
    final int AMOUNT_TAGS = 3;

    List<Profile> profiles = TestModelHelper.createProfilesWithAliasesAndWeblinks(
        AMOUNT_PROFILES, AMOUNT_ALIASES_PER_PROFILE, AMOUNT_WEBLINKS_PER_PROFILE);
    List<Tag> tags = TestModelHelper.createTags(AMOUNT_TAGS);
    for (Tag tag : tags) {
      testEntityManager.persist(tag);
    }

    profiles.get(0).getTags().add(tags.get(0));
    profiles.get(0).getTags().add(tags.get(1));
    profiles.get(1).getTags().add(tags.get(2));

    for (Profile profile : profiles) {
      testEntityManager.persist(profile);
    }
    testEntityManager.flush();

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=0&pageSize=5"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("content").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("content.size()").value(AMOUNT_PROFILES))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].aliases").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].aliases.size()")
            .value(AMOUNT_ALIASES_PER_PROFILE))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].tags").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].tags.size()")
            .value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].weblinks").doesNotExist())
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].note").doesNotExist());
  }

  @Test
  public void givenProfiles_whenGetProfilesPageByAliasAndTagIds_withoutPageOrPageSize_thenStatusBadRequest()
      throws Exception {
    final int AMOUNT_PROFILES = 2;
    final int AMOUNT_ALIASES_PER_PROFILE = 2;
    final int AMOUNT_WEBLINKS_PER_PROFILE = 4;
    final int AMOUNT_TAGS = 3;

    List<Profile> profiles = TestModelHelper.createProfilesWithAliasesAndWeblinks(
        AMOUNT_PROFILES, AMOUNT_ALIASES_PER_PROFILE, AMOUNT_WEBLINKS_PER_PROFILE);
    List<Tag> tags = TestModelHelper.createTags(AMOUNT_TAGS);
    for (Tag tag : tags) {
      testEntityManager.persist(tag);
    }

    profiles.get(0).getTags().add(tags.get(0));
    profiles.get(0).getTags().add(tags.get(1));
    profiles.get(1).getTags().add(tags.get(2));

    for (Profile profile : profiles) {
      testEntityManager.persist(profile);
    }
    testEntityManager.flush();

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void givenProfiles_whenGetProfilesPageByAliasAndTagIds_withInvalidPageAndValidPageSize_thenStatusBadRequest()
      throws Exception {
    final int AMOUNT_PROFILES = 2;
    final int AMOUNT_ALIASES_PER_PROFILE = 2;
    final int AMOUNT_WEBLINKS_PER_PROFILE = 4;
    final int AMOUNT_TAGS = 3;

    List<Profile> profiles = TestModelHelper.createProfilesWithAliasesAndWeblinks(
        AMOUNT_PROFILES, AMOUNT_ALIASES_PER_PROFILE, AMOUNT_WEBLINKS_PER_PROFILE);
    List<Tag> tags = TestModelHelper.createTags(AMOUNT_TAGS);
    for (Tag tag : tags) {
      testEntityManager.persist(tag);
    }

    profiles.get(0).getTags().add(tags.get(0));
    profiles.get(0).getTags().add(tags.get(1));
    profiles.get(1).getTags().add(tags.get(2));

    for (Profile profile : profiles) {
      testEntityManager.persist(profile);
    }
    testEntityManager.flush();

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=-1&pageSize=5"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void givenProfiles_whenGetProfilesPageByAliasAndTagIds_withValidPageAndInvalidPageSize_thenStatusBadRequest()
      throws Exception {
    final int AMOUNT_PROFILES = 2;
    final int AMOUNT_ALIASES_PER_PROFILE = 2;
    final int AMOUNT_WEBLINKS_PER_PROFILE = 4;
    final int AMOUNT_TAGS = 3;

    List<Profile> profiles = TestModelHelper.createProfilesWithAliasesAndWeblinks(
        AMOUNT_PROFILES, AMOUNT_ALIASES_PER_PROFILE, AMOUNT_WEBLINKS_PER_PROFILE);
    List<Tag> tags = TestModelHelper.createTags(AMOUNT_TAGS);
    for (Tag tag : tags) {
      testEntityManager.persist(tag);
    }

    profiles.get(0).getTags().add(tags.get(0));
    profiles.get(0).getTags().add(tags.get(1));
    profiles.get(1).getTags().add(tags.get(2));

    for (Profile profile : profiles) {
      testEntityManager.persist(profile);
    }
    testEntityManager.flush();

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=0&pageSize=0"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}
