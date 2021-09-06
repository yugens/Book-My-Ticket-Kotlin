package ticket

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetTicketsForAShowParams(
  public val showId: Int?,
  public val theatreId: Int?
)

public class GetTicketsForAShowParamSetter : ParamSetter<GetTicketsForAShowParams> {
  public override fun map(ps: PreparedStatement, params: GetTicketsForAShowParams): Unit {
    ps.setObject(1, params.showId)
    ps.setObject(2, params.theatreId)
  }
}

public data class GetTicketsForAShowResult(
  public val ticketNumber: Int,
  public val showId: Int?,
  public val userId: Int?,
  public val theatreId: Int?
)

public class GetTicketsForAShowRowMapper : RowMapper<GetTicketsForAShowResult> {
  public override fun map(rs: ResultSet): GetTicketsForAShowResult = GetTicketsForAShowResult(
  ticketNumber = rs.getObject("ticket_number") as kotlin.Int,
    showId = rs.getObject("show_id") as kotlin.Int?,
    userId = rs.getObject("user_id") as kotlin.Int?,
    theatreId = rs.getObject("theatre_id") as kotlin.Int?)
}

public class GetTicketsForAShowQuery : Query<GetTicketsForAShowParams, GetTicketsForAShowResult> {
  public override val sql: String = """
      |SELECT * FROM tickets WHERE show_id=? AND theatre_id=?;
      |""".trimMargin()

  public override val mapper: RowMapper<GetTicketsForAShowResult> = GetTicketsForAShowRowMapper()

  public override val paramSetter: ParamSetter<GetTicketsForAShowParams> =
      GetTicketsForAShowParamSetter()
}
