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

public data class GetShowFromIdParams(
  public val id: Int?
)

public class GetShowFromIdParamSetter : ParamSetter<GetShowFromIdParams> {
  public override fun map(ps: PreparedStatement, params: GetShowFromIdParams): Unit {
    ps.setObject(1, params.id)
  }
}

public data class GetShowFromIdResult(
  public val id: Int,
  public val startTime: Timestamp,
  public val movieId: Int?,
  public val theatreId: Int?,
  public val totalSeats: Int
)

public class GetShowFromIdRowMapper : RowMapper<GetShowFromIdResult> {
  public override fun map(rs: ResultSet): GetShowFromIdResult = GetShowFromIdResult(
  id = rs.getObject("id") as kotlin.Int,
    startTime = rs.getObject("start_time") as java.sql.Timestamp,
    movieId = rs.getObject("movie_id") as kotlin.Int?,
    theatreId = rs.getObject("theatre_id") as kotlin.Int?,
    totalSeats = rs.getObject("total_seats") as kotlin.Int)
}

public class GetShowFromIdQuery : Query<GetShowFromIdParams, GetShowFromIdResult> {
  public override val sql: String = """
      |SELECT * FROM shows WHERE id=?;
      |""".trimMargin()

  public override val mapper: RowMapper<GetShowFromIdResult> = GetShowFromIdRowMapper()

  public override val paramSetter: ParamSetter<GetShowFromIdParams> = GetShowFromIdParamSetter()
}
