package ticket

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetTicketForAUserParams(
  public val ticket_number: Int?
)

public class GetTicketForAUserParamSetter : ParamSetter<GetTicketForAUserParams> {
  public override fun map(ps: PreparedStatement, params: GetTicketForAUserParams): Unit {
    ps.setObject(1, params.ticket_number)
  }
}

public data class GetTicketForAUserResult(
  public val ticketNumber: Int,
  public val theatreName: String,
  public val movieName: String,
  public val startTime: Timestamp,
  public val durationInMinutes: Int,
  public val userName: String
)

public class GetTicketForAUserRowMapper : RowMapper<GetTicketForAUserResult> {
  public override fun map(rs: ResultSet): GetTicketForAUserResult = GetTicketForAUserResult(
  ticketNumber = rs.getObject("ticket_number") as kotlin.Int,
    theatreName = rs.getObject("theatre_name") as kotlin.String,
    movieName = rs.getObject("movie_name") as kotlin.String,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    durationInMinutes = rs.getObject("duration_in_minutes") as kotlin.Int,
    userName = rs.getObject("user_name") as kotlin.String)
}

public class GetTicketForAUserQuery : Query<GetTicketForAUserParams, GetTicketForAUserResult> {
  public override val sql: String = """
      |SELECT t.ticket_number, th.name as theatre_name , m.title as movie_name,s.start_time,m.duration_in_minutes , u.name as user_name
      |FROM tickets t INNER JOIN shows s ON t.show_id =s.id INNER Join movies m on s.movie_id = m.id INNER join theatres th On m.theatre_id=th.id
      |inner join users u on t.user_id =u.id
      |where t.ticket_number =?
      |""".trimMargin()

  public override val mapper: RowMapper<GetTicketForAUserResult> = GetTicketForAUserRowMapper()

  public override val paramSetter: ParamSetter<GetTicketForAUserParams> =
      GetTicketForAUserParamSetter()
}
