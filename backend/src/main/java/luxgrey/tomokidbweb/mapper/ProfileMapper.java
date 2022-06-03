package luxgrey.tomokidbweb.mapper;

import luxgrey.tomokidbweb.dto.ProfileDTOShort;
import luxgrey.tomokidbweb.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface ProfileMapper {

  ProfileDTOShort toProfileDTOShort(Profile profileEntity);

  default Page<ProfileDTOShort> toProfileDTOShortPage(Page<Profile> profilePage) {
    return profilePage.map(this::toProfileDTOShort);
  }
}
