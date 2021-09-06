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

public data class SaveShowsParams(
  public val start_time: Timestamp?,
  public val movie_id: Int?,
  public val theatre_id: Int?,
  public val total_seats: Int?
)

public class SaveShowsParamSetter : ParamSetter<SaveShowsParams> {
  public override fun map(ps: PreparedStatement, params: SaveShowsParams): Unit {
    ps.setObject(1, params.start_time)
    ps.setObject(2, params.movie_id)
    ps.setObject(3, params.theatre_id)
    ps.setObject(4, params.total_seats)
  }
}

public data class SaveShowsResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val movieId: Int?,
  public val theatreId: Int?,
  public val totalSeats: Int
)

public class SaveShowsRowMapper : RowMapper<SaveShowsResult> {
  public override fun map(rs: ResultSet): SaveShowsResult = SaveShowsResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int?,
    theatreId = rs.getObject("theatre_id") as kotlin.Int?,
    totalSeats = rs.getObject("total_seats") as kotlin.Int)
}

public class SaveShowsQuery : Query<SaveShowsParams, SaveShowsResult> {
  public override val sql: String = """
      |INSERT INTO shows(start_time,movie_id,theatre_id, total_seats)
      |VALUES(?,?,?, ?)
      |returning *;
      |""".trimMargin()

  public override val mapper: RowMapper<SaveShowsResult> = SaveShowsRowMapper()

  public override val paramSetter: ParamSetter<SaveShowsParams> = SaveShowsParamSetter()
}
