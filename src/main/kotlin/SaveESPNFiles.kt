package dev.mfazio.espnffb

import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.calculators.ESPNTeamRecordsCalculator
import dev.mfazio.espnffb.converters.*
import dev.mfazio.espnffb.extensions.toTeamMemberSummaries
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.types.RecordBooks

suspend fun main() {

    ESPNLocalFileHandler.saveRawDataToFiles(
        startYear = 2024,
        endYear = 2024,
        startWeek = 1,
        endWeek = 1,
    )

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()
    val teamsMap = getTeamYearMapFromScoreboards(scoreboards)
    val members = getMemberListFromScoreboards(scoreboards)
    val teams = getTeamListFromScoreboards(scoreboards)

    ESPNLocalFileHandler.saveTeamYearMap(scoreboards)
    ESPNLocalFileHandler.saveMemberList(scoreboards)

    val matchups = getMatchupsFromScoreboards(scoreboards, teamsMap).filterNotNull()
    ESPNLocalFileHandler.saveMatchups(matchups)

    val recordBooks = RecordBooks(
        standard = ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups),
        modern = ESPNRecordBookCalculator.getModernRecordBook(matchups),
        bestBall = ESPNRecordBookCalculator.getBestBallRecordBook(matchups),
        currentYear = ESPNRecordBookCalculator.getCurrentYearRecordBook(matchups),
    )
    ESPNLocalFileHandler.saveRecordBooks(recordBooks)

    val standings = ESPNStandingsCalculator.getStandingsFromMatchups(matchups, members, teams, teamsMap)
    ESPNLocalFileHandler.saveStandings(standings)

    val memberVsTeamRecords = ESPNTeamRecordsCalculator.getAllMemberRecordsFromMatchups(members, teams, teamsMap, matchups)
    ESPNLocalFileHandler.saveMemberVsTeamRecords(memberVsTeamRecords)

    val franchiseSeasons = ESPNTeamRecordsCalculator.getFranchiseSeasonsFromMatchups(members, teams, matchups)
    val franchiseSummaries = franchiseSeasons.map { (team, seasons) -> seasons.toTeamMemberSummaries(team.id.toString(), team.fullName) }
    ESPNLocalFileHandler.saveTeamSummaries(franchiseSummaries)

    val ownerSeasons = ESPNTeamRecordsCalculator.getOwnerSeasonsFromMatchups(members, teams, matchups)
    val ownerSummaries = ownerSeasons.map { (owner, seasons) -> seasons.toTeamMemberSummaries(owner.id, owner.fullName) }
    ESPNLocalFileHandler.saveOwnerSummaries(ownerSummaries)
}
