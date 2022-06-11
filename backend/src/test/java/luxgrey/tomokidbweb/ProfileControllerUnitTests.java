package luxgrey.tomokidbweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import luxgrey.tomokidbweb.dto.ProfileDTOPostOrPut;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
import luxgrey.tomokidbweb.exception.InvalidProfileTagsException;
import luxgrey.tomokidbweb.mapper.ProfileMapper;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerUnitTests {

  private final String PROFILES_BASE_URL = "/api/profiles";

  @Autowired
  private ProfileMapper profileMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProfileService profileService;

  @Test
  public void whenCreateProfile_withValidPayload_thenStatus201() throws Exception {
    ProfileDTOPostOrPut profileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    profileDTO.setTagIdsExisting(Set.of(1L, 2L));
    profileDTO.setTagNamesNew(Set.of("Tag100", "Tag200"));
    ObjectMapper objectMapper = new ObjectMapper();
    String payload = objectMapper.writeValueAsString(profileDTO);

    Mockito.when(profileService.createProfile(ArgumentMatchers.any(ProfileDTOPostOrPut.class)))
        .thenReturn(profileMapper.toProfile(profileDTO));

    this.mockMvc
        .perform(MockMvcRequestBuilders.post(PROFILES_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .characterEncoding("utf-8")
            .content(payload))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void whenCreateProfile_withInvalidPayload_thenStatus400() throws Exception {
    ProfileDTOPostOrPut profileDTO = TestModelHelper.createValidProfileDTOPostOrPut(1);
    profileDTO.setTagIdsExisting(Set.of(100L, 200L));
    profileDTO.setTagNamesNew(Set.of("Tag1", "Tag2"));
    ObjectMapper objectMapper = new ObjectMapper();
    String payload = objectMapper.writeValueAsString(profileDTO);

    Mockito.when(profileService.createProfile(ArgumentMatchers.any(ProfileDTOPostOrPut.class)))
        .thenThrow(new InvalidProfileTagsException());

    this.mockMvc
        .perform(MockMvcRequestBuilders.post(PROFILES_BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .characterEncoding("utf-8")
            .content(payload))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void givenProfile_whenGetProfile_withValidId_thenStatus200() throws Exception {
    final Long SOUGHT_PROFILE_ID = 1L;
    Profile profile = TestModelHelper.createProfileWithAliasesAndWeblinks(2, 3, 1);
    Mockito.when(profileService.getProfile(SOUGHT_PROFILE_ID))
        .thenReturn(Optional.of(profile));

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "/" + SOUGHT_PROFILE_ID))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void givenProfile_whenGetProfile_withNonExistingId_thenStatus404() throws Exception {
    final Long SOUGHT_PROFILE_ID = 100L;
    Mockito.when(profileService.getProfile(SOUGHT_PROFILE_ID))
        .thenReturn(Optional.empty());

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "/" + SOUGHT_PROFILE_ID))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void givenProfile_whenGetProfile_withNegativeId_thenStatus400() throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "/-1"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void whenGetProfilesPageByAliasAndTagIds_withoutPageOrPageSize_thenStatus400()
      throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + ""))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void givenProfiles_whenGetProfilesPageByAliasAndTagIds_withPageAndPageSize_thenStatus200()
      throws Exception {
    List<ProfileDTOShort> profileDTOShortList = TestModelHelper.createProfileDTOShortsWithAliases(
        5, 2, true);
    Page<ProfileDTOShort> profileDTOShortPage = new PageImpl<>(profileDTOShortList);
    Mockito.when(profileService.getProfilesPageByAliasAndTagIds(0, 5, null, null))
        .thenReturn(profileDTOShortPage);

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=0&pageSize=5"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void givenNoProfiles_whenGetProfilesPageByAliasAndTagIds_withPageAndPageSize_thenStatus204()
      throws Exception {
    List<ProfileDTOShort> emptyList = new ArrayList<>();
    Page<ProfileDTOShort> emptyPage = new PageImpl<>(emptyList);
    Mockito.when(profileService.getProfilesPageByAliasAndTagIds(0, 5, null, null))
        .thenReturn(emptyPage);

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=0&pageSize=5"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void whenGetProfilesPageByAliasAndTagIds_withPageWithoutPageSize_thenStatus404()
      throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=0"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void whenGetProfilesPageByAliasAndTagIds_withoutPageWithPageSize_thenStatus404()
      throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?pageSize=5"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}
