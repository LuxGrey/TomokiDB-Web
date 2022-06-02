package luxgrey.tomokidbweb;

import java.util.List;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
  public void givenProfile_whenGetProfile_withValidId_thenResponseBodyHasLoadedRelationships()
      throws Exception {
    final int AMOUNT_ALIASES = 2;
    final int AMOUNT_WEBLINKS = 4;
    final int AMOUNT_TAGS = 3;

    Profile profile = ModelTestHelper.createProfileWithAliasesAndWeblinks(
        AMOUNT_ALIASES, AMOUNT_WEBLINKS, 1);
    List<Tag> tags = ModelTestHelper.createTags(AMOUNT_TAGS);
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
}
