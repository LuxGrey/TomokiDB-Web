package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.List;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerIntegrationTests {

  private final String PROFILES_BASE_URL = "/api/profiles";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProfileService profileService;

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
    List<Profile> profilesList = ModelTestHelper.createProfilesWithAliasesAndWeblinks(
        5, 2, 3, true);
    Page<Profile> profilesPage = new PageImpl<>(profilesList);
    Mockito.when(profileService.getProfilesPageByAliasAndTagIds(0, 5, null, null))
        .thenReturn(profilesPage);

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=0&pageSize=5"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void givenNoProfiles_whenGetProfilesPageByAliasAndTagIds_withPageAndPageSize_thenStatus204()
      throws Exception {
    List<Profile> emptyList = new ArrayList<>();
    Page<Profile> emptyPage = new PageImpl<>(emptyList);
    Mockito.when(profileService.getProfilesPageByAliasAndTagIds(0, 5, null, null))
        .thenReturn(emptyPage);

    this.mockMvc
        .perform(MockMvcRequestBuilders.get(PROFILES_BASE_URL + "?page=0&pageSize=5"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
