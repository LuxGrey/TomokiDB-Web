package luxgrey.tomokidbweb;

import java.util.List;
import luxgrey.tomokidbweb.model.Profile;
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
  public void whenFindAllPaginated_thenReturnAllPaginated() {
    List<Profile> profiles = ModelTestHelper.createProfilesWithAliasesAndWeblinks(10, 2, 3);
    for(Profile p : profiles) {
      testEntityManager.persist(p);
    }
    testEntityManager.flush();

    final int pageSize = 5;
    Pageable pageable = PageRequest.of(0, pageSize);

    Page<Profile> profilePage = profileRepository.findAll(pageable);

    Assert.assertEquals(pageSize, profilePage.getNumberOfElements());
  }
}
