package movie

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetMoviesInATheatreParams(
  public val theatreId: Int?
)

public class GetMoviesInATheatreParamSetter : ParamSetter<GetMoviesInATheatreParams> {
  public override fun map(ps: PreparedStatement, params: GetMoviesInATheatreParams): Unit {
    ps.setObject(1, params.theatreId)
  }
}

public data class GetMoviesInATheatreResult(
  public val id: Int,
  public val title: String,
  public val durationInMinutes: Int,
  public val theatreId: Int
)

public class GetMoviesInATheatreRowMapper : RowMapper<GetMoviesInATheatreResult> {
  public override fun map(rs: ResultSet): GetMoviesInATheatreResult = GetMoviesInATheatreResult(
  id = rs.getObject("id") as kotlin.Int,
    title = rs.getObject("title") as kotlin.String,
    durationInMinutes = rs.getObject("duration_in_minutes") as kotlin.Int,
    theatreId = rs.getObject("theatre_id") as kotlin.Int)
}

public class GetMoviesInATheatreQuery : Query<GetMoviesInATheatreParams, GetMoviesInATheatreResult>
    {
  public override val sql: String = """
      |SELECT * FROM movies WHERE theatre_id=?
      |""".trimMargin()

  public override val mapper: RowMapper<GetMoviesInATheatreResult> = GetMoviesInATheatreRowMapper()

  public override val paramSetter: ParamSetter<GetMoviesInATheatreParams> =
      GetMoviesInATheatreParamSetter()
}
