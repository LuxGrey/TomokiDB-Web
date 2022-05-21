package luxgrey.tomokidbweb;

import java.util.Arrays;
import java.util.List;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.repository.ProfileRepository;
import luxgrey.tomokidbweb.service.ProfileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTests {

  @Mock
  private ProfileRepository profileRepository;

  @InjectMocks
  private ProfileService profileService;

  @Test
  public void whenGetProfilesPage_thenProfilesPageReturned() {
    final String SOUGHT_ALIAS_NAME = "SpecialAlias";
    final List<Long> SOUGHT_TAG_IDS = Arrays.asList(1L,3L);
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
