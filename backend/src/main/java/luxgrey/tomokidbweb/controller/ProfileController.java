package luxgrey.tomokidbweb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import java.util.Optional;
import luxgrey.tomokidbweb.dto.ProfileDTOShort;
import luxgrey.tomokidbweb.model.Profile;
import luxgrey.tomokidbweb.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Profile")
public class ProfileController {

  private final ProfileService profileService;

  @Autowired
  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a Profile")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "ok"),
      @ApiResponse(responseCode = "400", description = "bad request"),
      @ApiResponse(responseCode = "404", description = "not found"),
  })
  public ResponseEntity<?> getProfileId(@PathVariable final Long id) {
    if (id < 1L) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    Optional<Profile> optionalProfile = profileService.getProfile(id);
    if (optionalProfile.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body(optionalProfile.get());
  }

  @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get a page of Profiles filtered by Alias and Tags")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "ok"),
      @ApiResponse(responseCode = "204", description = "ok, no content"),
      @ApiResponse(responseCode = "400", description = "bad request")
  })
  public ResponseEntity<?> getProfilesPageByAliasAndTagIds(
      @RequestParam final int page,
      @RequestParam final int pageSize,
      @RequestParam(required = false) final String aliasName,
      @RequestParam(required = false) final Collection<Long> tagIds) {

    if (page < 0 || pageSize < 1) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    Page<ProfileDTOShort> profilesPage = profileService.getProfilesPageByAliasAndTagIds(
        page, pageSize, aliasName, tagIds
    );

    if (profilesPage.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body(profilesPage);
  }
}
