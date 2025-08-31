package dev.mfazio.espnffb

import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.calculators.ESPNTeamRecordsCalculator
import dev.mfazio.espnffb.converters.*
import dev.mfazio.espnffb.extensions.toTeamMemberSummaries
import dev.mfazio.espnffb.handlers.ChartsHandler
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.types.RecordBooks
import dev.mfazio.espnffb.various.VariousFactHandler

suspend fun main() {

    ESPNLocalFileHandler.saveRawWeeklyDataToFiles(
        startYear = 2025,
        endYear = 2025,
        startWeek = 1,
        endWeek = 1,
    )

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()
    val teamsMap = getTeamYearMapFromScoreboards(scoreboards)
    val members = getMemberListFromScoreboards(scoreboards)
    val teams = getTeamListFromScoreboards(scoreboards)

    ESPNLocalFileHandler.saveTeamYearMap(scoreboards)
    ESPNLocalFileHandler.saveMemberList(scoreboards)

    val matchups = getMatchupsFromScoreboards(scoreboards, teamsMap, includePlayers = true).filterNotNull()
    ESPNLocalFileHandler.saveMatchups(matchups)

    val (currentWeek, currentYear) = getCurrentWeekAndYear(matchups)

    val skipCurrentYear = currentWeek < 14
    val excludeDeadSlotTeams = true

    val recordBooks = RecordBooks(
        latestYear = currentYear,
        latestWeek = currentWeek,
        standard = ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups, skipCurrentYear = skipCurrentYear, excludeDeadSlotTeams = excludeDeadSlotTeams),
        modern = ESPNRecordBookCalculator.getModernRecordBook(matchups, skipCurrentYear = skipCurrentYear, excludeDeadSlotTeams = excludeDeadSlotTeams),
        bestBall = ESPNRecordBookCalculator.getBestBallRecordBook(matchups, skipCurrentYear = skipCurrentYear, excludeDeadSlotTeams = excludeDeadSlotTeams),
        currentYear = ESPNRecordBookCalculator.getCurrentYearRecordBook(matchups, excludeDeadSlotTeams = excludeDeadSlotTeams),
        currentYearBestBall = ESPNRecordBookCalculator.getCurrentYearBestBallRecordBook(matchups, excludeDeadSlotTeams = excludeDeadSlotTeams),
    )
    ESPNLocalFileHandler.saveRecordBooks(recordBooks)

    val standings = ESPNStandingsCalculator.getStandingsFromMatchups(matchups, members, teams, teamsMap)
    ESPNLocalFileHandler.saveStandings(standings)
    val modernStandings = ESPNStandingsCalculator.getStandingsFromMatchups(
        matchups.filter { it.year >= ESPNConfig.modernStartYear },
        members,
        teams,
        teamsMap
    )
    ESPNLocalFileHandler.saveModernStandings(modernStandings)

    val memberVsTeamRecords =
        ESPNTeamRecordsCalculator.getAllMemberRecordsFromMatchups(members, teams, teamsMap, matchups)
    ESPNLocalFileHandler.saveMemberVsTeamRecords(memberVsTeamRecords)

    val franchiseSeasons = ESPNTeamRecordsCalculator.getFranchiseSeasonsFromMatchups(members, teams, matchups)
    val franchiseSummaries =
        franchiseSeasons.map { (team, seasons) -> seasons.toTeamMemberSummaries(team.id.toString(), team.fullName) }
    ESPNLocalFileHandler.saveTeamSummaries(franchiseSummaries)

    val ownerSeasons = ESPNTeamRecordsCalculator.getOwnerSeasonsFromMatchups(members, teams, matchups)
    val ownerSummaries =
        ownerSeasons.map { (owner, seasons) -> seasons.toTeamMemberSummaries(owner.id, owner.fullName) }
    ESPNLocalFileHandler.saveOwnerSummaries(ownerSummaries)

    val variousFactCards = VariousFactHandler.generateList(scoreboards, matchups, teamsMap, teams, members)
    ESPNLocalFileHandler.saveVariousFactCards(variousFactCards)

    val chartsData = ChartsHandler.generateChartData(scoreboards, matchups, teamsMap, members)
    ESPNLocalFileHandler.saveChartsData(chartsData)
}
