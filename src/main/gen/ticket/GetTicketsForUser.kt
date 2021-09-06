package ticket

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetTicketsForUserParams(
  public val userId: Int?
)

public class GetTicketsForUserParamSetter : ParamSetter<GetTicketsForUserParams> {
  public override fun map(ps: PreparedStatement, params: GetTicketsForUserParams): Unit {
    ps.setObject(1, params.userId)
  }
}

public data class GetTicketsForUserResult(
  public val ticketNumber: Int,
  public val showId: Int?,
  public val userId: Int?,
  public val theatreId: Int?
)

public class GetTicketsForUserRowMapper : RowMapper<GetTicketsForUserResult> {
  public override fun map(rs: ResultSet): GetTicketsForUserResult = GetTicketsForUserResult(
  ticketNumber = rs.getObject("ticket_number") as kotlin.Int,
    showId = rs.getObject("show_id") as kotlin.Int?,
    userId = rs.getObject("user_id") as kotlin.Int?,
    theatreId = rs.getObject("theatre_id") as kotlin.Int?)
}

public class GetTicketsForUserQuery : Query<GetTicketsForUserParams, GetTicketsForUserResult> {
  public override val sql: String = """
      |SELECT * FROM tickets WHERE user_id=?;
      |""".trimMargin()

  public override val mapper: RowMapper<GetTicketsForUserResult> = GetTicketsForUserRowMapper()

  public override val paramSetter: ParamSetter<GetTicketsForUserParams> =
      GetTicketsForUserParamSetter()
}
