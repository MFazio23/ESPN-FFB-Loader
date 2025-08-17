package dev.mfazio.espnffb.handlers.schedule

import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.schedule.ScheduleMatchup

object ScheduleValidator {
    fun validateSchedule(
        teams: List<Team>,
        schedule: List<ScheduleMatchup>,
        vararg validationTypes: ValidationType
    ): Boolean {
        val validations = listOf(
            if (validationTypes.contains(ValidationType.WeekOneRivalry)) validateWeekOneRivalry(
                teams,
                schedule
            ) else true,
            if (validationTypes.contains(ValidationType.HomeAway)) validateHomeAway(teams, schedule) else true,
            if (validationTypes.contains(ValidationType.OnePerWeek)) validateOnePerWeek(teams, schedule) else true,
            if (validationTypes.contains(ValidationType.TwoInGroup)) validateTwoInGroup(teams, schedule) else true,
            if (validationTypes.contains(ValidationType.OneOutOfGroup)) validateOneOutOfGroup(teams, schedule) else true
        )
        return validations.all { it }
    }

    // Week one should feature rivalry matchups.
    fun validateWeekOneRivalry(teams: List<Team>, schedule: List<ScheduleMatchup>): Boolean {
        val weekOneGames = schedule.take(teams.size / 2)
        val areRivalryGamesFound = ESPNScheduleHandler.rivalryGames.fold(true) { isValid, (teamAId, teamBId) ->
            val isMatchupValid = weekOneGames.any { matchup -> matchup.contains(teamAId) && matchup.contains(teamBId) }
            return@fold isValid && isMatchupValid
        }
        val areMatchupsSet = weekOneGames.all { matchup ->
            ESPNScheduleHandler.rivalryGames.any { (teamAId, teamBId) ->
                matchup.contains(teamAId) && matchup.contains(teamBId)
            }
        }
        return areRivalryGamesFound && areMatchupsSet
    }

    // Each team should play seven home games and seven away games.
    // For each group, the teams should have one home and one away game against each other team in the group.
    fun validateHomeAway(teams: List<Team>, schedule: List<ScheduleMatchup>): Boolean {
        return teams.fold(true) { isValid, team ->
            val teamMatchups = schedule.filter { matchup -> matchup.contains(team) }
            val homeGameCount = teamMatchups.count { matchup -> matchup.home == team }
            val awayGameCount = teamMatchups.count { matchup -> matchup.away == team }

            isValid && homeGameCount == awayGameCount && homeGameCount == teamMatchups.size / 2
        }
    }

    // Each team should only be scheduled to play one game per week.
    fun validateOnePerWeek(teams: List<Team>, schedule: List<ScheduleMatchup>): Boolean {
        val weeks = schedule.chunked(teams.size / 2)
        return weeks.fold(true) { isValid, weekMatchups ->
            teams.fold(isValid) { teamIsValid, team ->
                val gamesThisWeek = weekMatchups.count { matchup -> matchup.contains(team) }
                teamIsValid && gamesThisWeek == 1
            }
        }
    }

    // Each team in each group should play every other team twice
    fun validateTwoInGroup(teams: List<Team>, schedule: List<ScheduleMatchup>): Boolean = teams.all { team ->
        val teamGroup = ESPNScheduleHandler.teamGroups.first { group -> group.contains(team.id) }

        teamGroup.filter { it != team.id}.all { opposingTeamId ->
            schedule.count { matchup ->
                matchup.contains(team) && matchup.contains(opposingTeamId)
            } == 2
        }
    }

    // Each team should play the other teams once
    fun validateOneOutOfGroup(teams: List<Team>, schedule: List<ScheduleMatchup>): Boolean = teams.all { team ->
        val teamGroup = ESPNScheduleHandler.teamGroups.first { group -> group.contains(team.id) }

        val outOfGroupTeams = teams.filter { it.id !in teamGroup }

        outOfGroupTeams.all { opposingTeam ->
            schedule.count { matchup ->
                matchup.contains(team) && matchup.contains(opposingTeam)
            } == 1
        }
    }
}

enum class ValidationType {
    WeekOneRivalry,
    HomeAway,
    OnePerWeek,
    TwoInGroup,
    OneOutOfGroup,
}