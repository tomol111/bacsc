package bacsc


class FileRankingRepoSpec extends RankingRepoSpec:

  def newRepository(): core.RankingRepo =
    FileRankingRepo(java.io.File.createTempFile("ranking", ".csv"))
