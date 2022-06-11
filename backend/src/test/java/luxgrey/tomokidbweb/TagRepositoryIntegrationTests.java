package luxgrey.tomokidbweb;

import java.util.ArrayList;
import java.util.List;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class TagRepositoryIntegrationTests {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private TagRepository tagRepository;

  @Test
  public void whenFindByIdIn_withDuplicateIds_thenReturnUniqueTags() {
    List<Tag> tags = TestModelHelper.createTags(5);
    List<Long> persistedTagIds = new ArrayList<>();
    for (Tag tag : tags) {
      persistedTagIds.add((Long) testEntityManager.persistAndGetId(tag));
    }
    testEntityManager.flush();

    List<Long> soughtTagIds = new ArrayList<>();
    soughtTagIds.add(persistedTagIds.get(0));
    soughtTagIds.add(persistedTagIds.get(0)); // duplicate
    soughtTagIds.add(persistedTagIds.get(1));

    Assertions.assertEquals(2, tagRepository.countByIdIn(soughtTagIds));
  }

  @Test
  public void whenFindByIdName_withDuplicateNames_thenReturnUniqueTags() {
    List<Tag> tags = TestModelHelper.createTags(5);
    List<String> persistedTagNames = new ArrayList<>();
    for (Tag tag : tags) {
      testEntityManager.persistAndGetId(tag);
      persistedTagNames.add(tag.getName());
    }
    testEntityManager.flush();

    List<String> soughtTagNames = new ArrayList<>();
    soughtTagNames.add(persistedTagNames.get(0));
    soughtTagNames.add(persistedTagNames.get(0)); // duplicate
    soughtTagNames.add(persistedTagNames.get(1));

    Assertions.assertEquals(2, tagRepository.countByNameIn(soughtTagNames));
  }
}
