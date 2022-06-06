package luxgrey.tomokidbweb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import luxgrey.tomokidbweb.mapper.ProfileMapper;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.model.Weblink;
import org.mapstruct.factory.Mappers;

/**
 * DTO for Profile that omits Profile ID
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ProfileDTOPostOrPut {

  private String note;

  @NotEmpty
  private Set<Alias> aliases = new HashSet<>();

  @NotEmpty
  private Set<Weblink> weblinks = new HashSet<>();

  private Set<Long> tagIdsExisting = new HashSet<>();

  private Set<String> tagNamesNew = new HashSet<>();

  /**
   * ATTENTION: Does not map tags
   */
  public static Profile toProfile(ProfileDTOPostOrPut profileDTOPostOrPut) {
    ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);
    return mapper.toProfile(profileDTOPostOrPut);
  }
}
