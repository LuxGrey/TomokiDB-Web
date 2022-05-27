package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.repository.ProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class ProfileRepositoryIntegrationTests {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private ProfileRepository profileRepository;

  @Test
  public void whenFindAllPaginated_thenReturnPageWithCorrectAmount() {
    // preparation
    final int AMOUNT_PROFILES = 10;
    final int PAGE = 0;
    final int PAGE_SIZE = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(
        AMOUNT_PROFILES, 2, 3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    Page<Profile> profilePage = profileRepository.findAll(pageable);

    Assertions.assertEquals(PAGE_SIZE, profilePage.getNumberOfElements());
    Assertions.assertEquals(AMOUNT_PROFILES, profilePage.getTotalElements());
  }
  
  @Test
  public void whenFindAllPaginated_withPageOutOfRange_thenReturnEmptyPage() {
    // preparation
    final int AMOUNT_PROFILES = 10;
    final int PAGE = 20;
    final int PAGE_SIZE = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(
        AMOUNT_PROFILES, 2, 3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    Page<Profile> profilePage = profileRepository.findAll(pageable);

    Assertions.assertEquals(0, profilePage.getNumberOfElements());
    Assertions.assertEquals(AMOUNT_PROFILES, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withoutAliasOrTagIds_thenReturnPageWithCorrectAmount() {
    // preparation
    final int AMOUNT_PROFILES = 10;
    final int PAGE = 0;
    final int PAGE_SIZE = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(AMOUNT_PROFILES, 2,
        3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(pageable, null, null);

    Assertions.assertEquals(PAGE_SIZE, profilePage.getNumberOfElements());
    Assertions.assertEquals(AMOUNT_PROFILES, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withoutAliasOrTagIdsWithPageOutOfRange_thenReturnPageWithCorrectAmount() {
    // preparation
    final int AMOUNT_PROFILES = 10;
    final int PAGE = 20;
    final int PAGE_SIZE = 5;
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(AMOUNT_PROFILES, 2,
        3);
    for (Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    // test
    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(pageable, null, null);

    Assertions.assertEquals(0, profilePage.getNumberOfElements());
    Assertions.assertEquals(AMOUNT_PROFILES, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withAliasAndTagIds_thenReturnPageWithCorrectAmount() {
    final int AMOUNT_PROFILES = 10;
    final int AMOUNT_TAGS = 6;
    final String EXPECTED_ALIAS_NAME = "SpecialAlias";
    final int[] PROFILE_INDICES_WITH_SOUGHT_ALIAS = {0, 1, 2, 3,};
    final int[] SOUGHT_TAG_INDICES = {1, 4, 5};
    final int[] PROFILE_INDICES_WITH_SOUGHT_TAGS = {2, 3, 4, 5};
    final int PAGE = 0;
    final int PAGE_SIZE = 5;
    // overlap between Profiles with sought Tags and Profiles with sought Alias
    final int expectedAmountResults = 2;

    List<Long> soughtTagIds = prepareDatabaseForFindByAliasAndTagIdsTest(
        AMOUNT_PROFILES,
        AMOUNT_TAGS,
        EXPECTED_ALIAS_NAME,
        PROFILE_INDICES_WITH_SOUGHT_ALIAS,
        SOUGHT_TAG_INDICES,
        PROFILE_INDICES_WITH_SOUGHT_TAGS
    );

    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(
        pageable, EXPECTED_ALIAS_NAME, soughtTagIds);

    Assertions.assertEquals(expectedAmountResults, profilePage.getNumberOfElements());
    Assertions.assertEquals(AMOUNT_PROFILES, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIsPaginated_withAliasWithoutTagIds_thenReturnPageWithCorrectAmount() {
    final int AMOUNT_PROFILES = 10;
    final int AMOUNT_TAGS = 0;
    final String EXPECTED_ALIAS_NAME = "SpecialAlias";
    final int[] PROFILE_INDICES_WITH_SOUGHT_ALIAS = {0, 1, 2, 3,};
    final int[] SOUGHT_TAG_INDICES = {};
    final int[] PROFILE_INDICES_WITH_SOUGHT_TAGS = {};
    final int PAGE = 0;
    final int PAGE_SIZE = 5;
    // Profiles with sought Alias
    final int expectedAmountResults = 4;

    prepareDatabaseForFindByAliasAndTagIdsTest(
        AMOUNT_PROFILES,
        AMOUNT_TAGS,
        EXPECTED_ALIAS_NAME,
        PROFILE_INDICES_WITH_SOUGHT_ALIAS,
        SOUGHT_TAG_INDICES,
        PROFILE_INDICES_WITH_SOUGHT_TAGS
    );

    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(
        pageable, EXPECTED_ALIAS_NAME, null);

    Assertions.assertEquals(expectedAmountResults, profilePage.getNumberOfElements());
    Assertions.assertEquals(AMOUNT_PROFILES, profilePage.getTotalElements());
  }

  @Test
  public void whenFindByAliasAndTagIdsPaginated_withoutAliasWithTagIds_thenReturnPageWithCorrectAmount() {
    final int AMOUNT_PROFILES = 10;
    final int AMOUNT_TAGS = 6;
    final String EXPECTED_ALIAS_NAME = null;
    final int[] PROFILE_INDICES_WITH_SOUGHT_ALIAS = {};
    final int[] SOUGHT_TAG_INDICES = {1, 4, 5};
    final int[] PROFILE_INDICES_WITH_SOUGHT_TAGS = {2, 3, 4, 5};
    final int PAGE = 0;
    final int PAGE_SIZE = 5;
    // overlap between Profiles with sought Tags and Profiles with sought Alias
    final int expectedAmountResults = 4;

    List<Long> soughtTagIds = prepareDatabaseForFindByAliasAndTagIdsTest(
        AMOUNT_PROFILES,
        AMOUNT_TAGS,
        EXPECTED_ALIAS_NAME,
        PROFILE_INDICES_WITH_SOUGHT_ALIAS,
        SOUGHT_TAG_INDICES,
        PROFILE_INDICES_WITH_SOUGHT_TAGS
    );

    Pageable pageable = PageRequest.of(PAGE, PAGE_SIZE);

    Page<Profile> profilePage = profileRepository.findByAliasAndTagIds(
        pageable, EXPECTED_ALIAS_NAME, soughtTagIds);

    Assertions.assertEquals(expectedAmountResults, profilePage.getNumberOfElements());
    Assertions.assertEquals(AMOUNT_PROFILES, profilePage.getTotalElements());
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
