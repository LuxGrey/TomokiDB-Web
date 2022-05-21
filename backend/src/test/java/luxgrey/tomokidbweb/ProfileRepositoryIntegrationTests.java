package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.repository.ProfileRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
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
public class ProfileRepositoryIntegrationTests {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private ProfileRepository profileRepository;

  @Test
  public void whenFindAllPaginated_thenReturnPageWithCorrectAmount() {
    // preparation
    final int amountProfiles = 10;
    final int page = 0;
    final int pageSize = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(
        amountProfiles, 2, 3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(page, pageSize);

    Page<Profile> profilePage = profileRepository.findAll(pageable);

    Assert.assertEquals(pageSize, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withoutAliasOrTagIds_thenReturnPageWithCorrectAmount() {
    // preparation
    final int amountProfiles = 10;
    final int page = 0;
    final int pageSize = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(amountProfiles, 2,
        3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(page, pageSize);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(pageable, null, null);

    Assert.assertEquals(pageSize, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withAliasAndTagIds_thenReturnPageWithCorrectAmount() {
    final int amountProfiles = 10;
    final int amountTags = 6;
    final String expectedAliasName = "SpecialAlias";
    final int[] profileIndicesWithSoughtAlias = {0, 1, 2, 3,};
    final int[] soughtTagIndices = {1, 4, 5};
    final int[] profileIndicesWithSoughtTags = {2, 3, 4, 5};
    final int page = 0;
    final int pageSize = 5;
    // overlap between Profiles with sought Tags and Profiles with sought Alias
    final int expectedAmountResults = 2;

    List<Long> soughtTagIds = prepareDatabaseForFindByAliasAndTagIdsTest(
        amountProfiles,
        amountTags,
        expectedAliasName,
        profileIndicesWithSoughtAlias,
        soughtTagIndices,
        profileIndicesWithSoughtTags
    );

    Pageable pageable = PageRequest.of(page, pageSize);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(
        pageable, expectedAliasName, soughtTagIds);

    Assert.assertEquals(expectedAmountResults, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIsPaginated_withAliasWithoutTagIds_thenReturnPageWithCorrectAmount() {
    final int amountProfiles = 10;
    final int amountTags = 0;
    final String expectedAliasName = "SpecialAlias";
    final int[] profileIndicesWithSoughtAlias = {0, 1, 2, 3,};
    final int[] soughtTagIndices = {};
    final int[] profileIndicesWithSoughtTags = {};
    final int page = 0;
    final int pageSize = 5;
    // Profiles with sought Alias
    final int expectedAmountResults = 4;

    prepareDatabaseForFindByAliasAndTagIdsTest(
        amountProfiles,
        amountTags,
        expectedAliasName,
        profileIndicesWithSoughtAlias,
        soughtTagIndices,
        profileIndicesWithSoughtTags
    );

    Pageable pageable = PageRequest.of(page, pageSize);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(
        pageable, expectedAliasName, null);

    Assert.assertEquals(expectedAmountResults, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withoutAliasWithTagIds_thenReturnPageWithCorrectAmount() {
    final int amountProfiles = 10;
    final int amountTags = 6;
    final String expectedAliasName = null;
    final int[] profileIndicesWithSoughtAlias = {};
    final int[] soughtTagIndices = {1, 4, 5};
    final int[] profileIndicesWithSoughtTags = {2, 3, 4, 5};
    final int page = 0;
    final int pageSize = 5;
    // overlap between Profiles with sought Tags and Profiles with sought Alias
    final int expectedAmountResults = 4;

    List<Long> soughtTagIds = prepareDatabaseForFindByAliasAndTagIdsTest(
        amountProfiles,
        amountTags,
        expectedAliasName,
        profileIndicesWithSoughtAlias,
        soughtTagIndices,
        profileIndicesWithSoughtTags
    );

    Pageable pageable = PageRequest.of(page, pageSize);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(
        pageable, expectedAliasName, soughtTagIds);

    Assert.assertEquals(expectedAmountResults, profilePage.getNumberOfElements());
    Assert.assertEquals(amountProfiles, profilePage.getTotalElements());
  }

  /**
   * Sets up the database with data that are relevant for tests which test
   * findByAliasAndTagIds(...)
   *
   * WARNING: Ensure that you provide the indices-parameters with arguments that don't cause
   * Index out of bounds Exceptions; amountProfiles and amountTags parameters determine the sizes
   * of the respective collections
   *
   * @param amountProfiles                number of profiles to generate
   * @param amountTags                    number of tags to generate
   * @param soughtAliasName               name of the Alias to assign to selected Profiles
   * @param profileIndicesWithSoughtAlias indices of the collection with generated Profiles where a
   *                                      sought Alias should be associated
   * @param soughtTagIndices              indices of the collection with generated Tags that that
   *                                      should be associated with selected Profiles
   * @param profileIndicesWithSoughtTags  indices of the collection with gennerated Profiles where
   *                                      the sought Tags should be associated
   * @return a list of IDs of the sought Tags that were specified by soughtTagIndices
   * @see ProfileRepository#findByAliasAndTagIds(Pageable, String, Collection)
   */
  private List<Long> prepareDatabaseForFindByAliasAndTagIdsTest(
      int amountProfiles,
      int amountTags,
      String soughtAliasName,
      int[] profileIndicesWithSoughtAlias,
      int[] soughtTagIndices,
      int[] profileIndicesWithSoughtTags) {
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(
        amountProfiles, 2, 3);
    List<Tag> tags = ModelTestHelper.createTags(amountTags);

    for (Tag t : tags) {
      // persist earlier to assign IDs
      testEntityManager.persist(t);
    }

    // get IDs of sought Tags
    List<Long> soughtTagIds = new ArrayList<>();
    for(int l : soughtTagIndices) {
      soughtTagIds.add(tags.get(l).getId());
    }

    // assign sought Aliases
    if(soughtAliasName != null) {
      for (int i : profileIndicesWithSoughtAlias) {
        Alias alias = new Alias();
        alias.setName(soughtAliasName + i);
        profiles.get(i).getAliases().add(alias);
      }
    }

    // assing sought Tags
    for (int j : profileIndicesWithSoughtTags) {
      Profile profile = profiles.get(j);
      for (int k : soughtTagIndices) {
        profile.getTags().add(tags.get(k));
      }
    }

    // persist Profiles
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    return soughtTagIds;
  }
}
