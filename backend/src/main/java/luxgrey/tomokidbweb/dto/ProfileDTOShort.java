package luxgrey.tomokidbweb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import luxgrey.tomokidbweb.model.Alias;
import luxgrey.tomokidbweb.model.Tag;

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
}
