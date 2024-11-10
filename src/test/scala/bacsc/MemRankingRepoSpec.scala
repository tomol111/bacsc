package bacsc


class MemRankingRepoSpec extends RankingRepoSpec:

  def newRepository(): core.RankingRepo = MemRankingRepo()
