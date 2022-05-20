package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.List;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.repository.ProfileRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProfileRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private ProfileRepository profileRepository;

  @Test
  public void whenFindAllPaginated_thenReturnPageWithCorrectAmount() {
    // preparation
    final int amountProfiles = 10;
    final int pageSize = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(
        amountProfiles, 2, 3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(0, pageSize);

    Page<Profile> profilePage = profileRepository.findAll(pageable);

    Assert.assertEquals(pageSize, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withoutAliasOrTagIds_thenReturnPageWithCorrectAmount() {
    // preparation
    final int amountProfiles = 10;
    final int pageSize = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(amountProfiles, 2,
        3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(0, pageSize);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(pageable, null, null);

    Assert.assertEquals(pageSize, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withAliasAndTagIds_thenReturnPageWithCorrectAmount() {
    // preparation
    final int amountProfiles = 10;
    final int pageSize = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(amountProfiles, 2,
        3);
    List<Tag> tags = ModelTestHelper.createTags(6);
    for (Tag t : tags) {
      // persist earlier to assign IDs
      testEntityManager.persist(t);
    }

    final String expectedAliasName = "CustomAlias";
    // "Tag1, "Tag3", "Tag4"
    final int[] expectedTagsIndices = {0, 2, 3};
    final int[] expectedProfilesIndices = {1, 8};
    final int expectedAmountResults = expectedProfilesIndices.length;

    // set expected Tag IDs
    List<Long> expectedTagIds = new ArrayList<>();
    for (int k : expectedTagsIndices) {
      expectedTagIds.add(tags.get(k).getId());
    }

    // set expected result Profiles
    for (int i : expectedProfilesIndices) {
      Profile currentProfile = profiles.get(i);

      Alias customAlias = new Alias();
      // bring some variation into the expected Aliases
      customAlias.setName(expectedAliasName + i);
      currentProfile.getAliases().add(customAlias);

      for (int k : expectedTagsIndices) {
        Tag currentTag = tags.get(k);

        currentProfile.getTags().add(currentTag);
      }
    }

    // set Profile with expected Tags, but without expected Alias
    Profile unexpectedProfile = profiles.get(2);
    for (int k : expectedTagsIndices) {
      Tag currentTag = tags.get(k);

      unexpectedProfile.getTags().add(currentTag);
    }

    // set Profile with expected Alias, but only 1 of the expected Tags
    unexpectedProfile = profiles.get(4);
    Alias unexpectedProfileAlias = new Alias();
    // using profiles.size() to get a suffix that can not appear for any of the Aliases of the expected result profiles
    unexpectedProfileAlias.setName(expectedAliasName + profiles.size());
    unexpectedProfile.getAliases().add(unexpectedProfileAlias);

    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(0, pageSize);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(pageable, expectedAliasName,
        expectedTagIds);

    Assert.assertEquals(expectedAmountResults, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }
}
