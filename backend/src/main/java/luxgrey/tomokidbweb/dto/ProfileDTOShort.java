package luxgrey.tomokidbweb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import luxgrey.tomokidbweb.mapper.ProfileMapper;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

/**
 * DTO for Profile that omits note and weblinks
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ProfileDTOShort {

  private Long id;

  private List<Alias> aliases = new ArrayList<>();

  private List<Tag> tags = new ArrayList<>();

  /**
   * Return a Page of Profiles without - weblinks - note
   */
  public static Page<ProfileDTOShort> toProfileDTOPage(Page<Profile> profilePage) {
    ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);
    return mapper.toProfileDTOShortPage(profilePage);
  }
}
