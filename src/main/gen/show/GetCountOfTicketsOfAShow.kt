package show

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetCountOfTicketsOfAShowParams(
  public val showId: Int?
)

public class GetCountOfTicketsOfAShowParamSetter : ParamSetter<GetCountOfTicketsOfAShowParams> {
  public override fun map(ps: PreparedStatement, params: GetCountOfTicketsOfAShowParams): Unit {
    ps.setObject(1, params.showId)
  }
}

public data class GetCountOfTicketsOfAShowResult(
  public val count: Long?
)

public class GetCountOfTicketsOfAShowRowMapper : RowMapper<GetCountOfTicketsOfAShowResult> {
  public override fun map(rs: ResultSet): GetCountOfTicketsOfAShowResult =
      GetCountOfTicketsOfAShowResult(
  count = rs.getObject("count") as kotlin.Long?)
}

public class GetCountOfTicketsOfAShowQuery : Query<GetCountOfTicketsOfAShowParams,
    GetCountOfTicketsOfAShowResult> {
  public override val sql: String = """
      |SELECT COUNT(*) FROM tickets WHERE show_id=?;
      |""".trimMargin()

  public override val mapper: RowMapper<GetCountOfTicketsOfAShowResult> =
      GetCountOfTicketsOfAShowRowMapper()

  public override val paramSetter: ParamSetter<GetCountOfTicketsOfAShowParams> =
      GetCountOfTicketsOfAShowParamSetter()
}
