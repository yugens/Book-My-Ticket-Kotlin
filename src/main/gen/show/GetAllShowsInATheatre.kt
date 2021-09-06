package show

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetAllShowsInATheatreParams(
  public val theatreId: Int?
)

public class GetAllShowsInATheatreParamSetter : ParamSetter<GetAllShowsInATheatreParams> {
  public override fun map(ps: PreparedStatement, params: GetAllShowsInATheatreParams): Unit {
    ps.setObject(1, params.theatreId)
  }
}

public data class GetAllShowsInATheatreResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val movieId: Int?,
  public val theatreId: Int?,
  public val totalSeats: Int
)

public class GetAllShowsInATheatreRowMapper : RowMapper<GetAllShowsInATheatreResult> {
  public override fun map(rs: ResultSet): GetAllShowsInATheatreResult = GetAllShowsInATheatreResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int?,
    theatreId = rs.getObject("theatre_id") as kotlin.Int?,
    totalSeats = rs.getObject("total_seats") as kotlin.Int)
}

public class GetAllShowsInATheatreQuery : Query<GetAllShowsInATheatreParams,
    GetAllShowsInATheatreResult> {
  public override val sql: String = """
      |SELECT * FROM shows WHERE theatre_id=?
      |""".trimMargin()

  public override val mapper: RowMapper<GetAllShowsInATheatreResult> =
      GetAllShowsInATheatreRowMapper()

  public override val paramSetter: ParamSetter<GetAllShowsInATheatreParams> =
      GetAllShowsInATheatreParamSetter()
}
