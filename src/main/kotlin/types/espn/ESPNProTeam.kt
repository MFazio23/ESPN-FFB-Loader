package dev.mfazio.espnffb.types.espn

enum class ESPNProTeam(val teamCode: String, val espnId: Int) {
    Atlanta(teamCode = "ATL", espnId = 1),
    Buffalo(teamCode = "BUF", espnId = 2),
    Chicago(teamCode = "CHI", espnId = 3),
    Cincinnati(teamCode = "CIN", espnId = 4),
    Cleveland(teamCode = "CLE", espnId = 5),
    Dallas(teamCode = "DAL", espnId = 6),
    Denver(teamCode = "DEN", espnId = 7),
    Detroit(teamCode = "DET", espnId = 8),
    GreenBay(teamCode = "GB", espnId = 9),
    Tennessee(teamCode = "TEN", espnId = 10),
    Indianapolis(teamCode = "IND", espnId = 11),
    KansasCity(teamCode = "KC", espnId = 12),
    LasVegas(teamCode = "LV", espnId = 13),
    LosAngeles(teamCode = "LAR", espnId = 14),
    Miami(teamCode = "MIA", espnId = 15),
    Minnesota(teamCode = "MIN", espnId = 16),
    NewEngland(teamCode = "NE", espnId = 17),
    NewOrleans(teamCode = "NO", espnId = 18),
    NewYorkGiants(teamCode = "NYG", espnId = 19),
    NewYorkJets(teamCode = "NYJ", espnId = 20),
    Philadelphia(teamCode = "PHI", espnId = 21),
    Arizona(teamCode = "ARI", espnId = 22),
    Pittsburgh(teamCode = "PIT", espnId = 23),
    LosAngelesChargers(teamCode = "LAC", espnId = 24),
    SanFrancisco(teamCode = "SF", espnId = 25),
    Seattle(teamCode = "SEA", espnId = 26),
    TampaBay(teamCode = "TB", espnId = 27),
    Washington(teamCode = "WAS", espnId = 28),
    Carolina(teamCode = "CAR", espnId = 29),
    Jacksonville(teamCode = "JAX", espnId = 30),
    Baltimore(teamCode = "BAL", espnId = 33),
    Houston(teamCode = "HOU", espnId = 34);

    companion object {
        fun fromId(id: Int): ESPNProTeam? = ESPNProTeam.entries.find { it.espnId == id }
    }
}