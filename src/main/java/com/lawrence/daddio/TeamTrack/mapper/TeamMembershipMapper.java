package com.lawrence.daddio.TeamTrack.mapper;

import com.lawrence.daddio.TeamTrack.dto.TeamMembershipDto;
import com.lawrence.daddio.TeamTrack.entity.TeamMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TeamMembershipMapper {

    @Mapping(target = "createdTs", ignore = true)
    @Mapping(target = "updateTs", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "team", ignore = true)
    TeamMembership TeamMembershipDtoToTeamMembership(TeamMembershipDto teamMembershipDto);

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "teamId", source = "team.id")
    TeamMembershipDto TeamMembershipToTeamMembershipDto(TeamMembership teamMembership);

    List<TeamMembershipDto> TeamMembershipToTeamMembershipDtoList(List<TeamMembership> teamMemberships);

    List<TeamMembership> TeamMembershipDtoToTeamMembershipList(List<TeamMembershipDto> teamMembershipDtos);
}
