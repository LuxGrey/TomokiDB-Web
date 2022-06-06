package luxgrey.tomokidbweb.mapper;

import luxgrey.tomokidbweb.dto.ProfileDTONoId;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
import luxgrey.tomokidbweb.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface ProfileMapper {

  ProfileDTOShort toProfileDTOShort(Profile profileEntity);

  default Page<ProfileDTOShort> toProfileDTOShortPage(Page<Profile> profilePage) {
    return profilePage.map(this::toProfileDTOShort);
  }

  @Mapping(target = "id", ignore = true)
  Profile toProfile(ProfileDTONoId profileDTONoId);
}
