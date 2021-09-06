package ticket

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class AddTicketParams(
  public val showId: Int?,
  public val userId: Int?,
  public val theatreId: Int?
)

public class AddTicketParamSetter : ParamSetter<AddTicketParams> {
  public override fun map(ps: PreparedStatement, params: AddTicketParams): Unit {
    ps.setObject(1, params.showId)
    ps.setObject(2, params.userId)
    ps.setObject(3, params.theatreId)
  }
}

public data class AddTicketResult(
  public val ticketNumber: Int,
  public val showId: Int?,
  public val userId: Int?,
  public val theatreId: Int?
)

public class AddTicketRowMapper : RowMapper<AddTicketResult> {
  public override fun map(rs: ResultSet): AddTicketResult = AddTicketResult(
  ticketNumber = rs.getObject("ticket_number") as kotlin.Int,
    showId = rs.getObject("show_id") as kotlin.Int?,
    userId = rs.getObject("user_id") as kotlin.Int?,
    theatreId = rs.getObject("theatre_id") as kotlin.Int?)
}

public class AddTicketQuery : Query<AddTicketParams, AddTicketResult> {
  public override val sql: String = """
      |INSERT INTO tickets(show_id, user_id, theatre_id)
      |VALUES ( ?, ?, ?)
      |returning *;
      |""".trimMargin()

  public override val mapper: RowMapper<AddTicketResult> = AddTicketRowMapper()

  public override val paramSetter: ParamSetter<AddTicketParams> = AddTicketParamSetter()
}
