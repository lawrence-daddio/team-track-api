package com.lawrence.daddio.TeamTrack.mapper;

import com.lawrence.daddio.TeamTrack.dto.TeamDto;
import com.lawrence.daddio.TeamTrack.entity.Project;
import com.lawrence.daddio.TeamTrack.entity.Team;
import com.lawrence.daddio.TeamTrack.entity.TeamMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TeamMapper {

    @Mapping(target = "createdTs", ignore = true)
    @Mapping(target = "updateTs", ignore = true)
    @Mapping(target = "projects",  ignore = true)
    @Mapping(target = "teamMemberships", ignore = true)
    Team teamDtoToTeam(TeamDto teamDto);

    @Mapping(target = "projectIds", source = "projects")
    @Mapping(target = "teamMembershipIds", source = "teamMemberships")
    TeamDto teamToTeamDto(Team team);

    List<TeamDto> teamsToTeamDtos(List<Team> team);

    List<Team> teamDtosToTeams(List<TeamDto> teamDto);

    default Long teamToId(Team team) {
        return team.getId();
    }

    default Long projectToId(Project project) {
        return project.getId();
    }

    default Long teamMembershipToId(TeamMembership teamMembership) {
        return teamMembership.getId();
    }

}
