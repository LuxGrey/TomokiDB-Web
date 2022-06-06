package luxgrey.tomokidbweb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import luxgrey.tomokidbweb.mapper.ProfileMapper;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Tag;
import luxgrey.tomokidbweb.model.Weblink;
import org.mapstruct.factory.Mappers;

/**
 * DTO for Profile that omits Profile ID
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ProfileDTONoId {

  private String note;

  @NotEmpty
  private List<Alias> aliases = new ArrayList<>();

  @NotEmpty
  private List<Weblink> weblinks = new ArrayList<>();

  private List<Tag> tags = new ArrayList<>();

  public static Profile toProfile(ProfileDTONoId profileDTONoId) {
    ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);
    return mapper.toProfile(profileDTONoId);
  }
}
