package theatre

import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.Int
import kotlin.String
import kotlin.Unit
import norm.ParamSetter
import norm.Query
import norm.RowMapper

public data class GetTheatreFromIdParams(
  public val id: Int?
)

public class GetTheatreFromIdParamSetter : ParamSetter<GetTheatreFromIdParams> {
  public override fun map(ps: PreparedStatement, params: GetTheatreFromIdParams): Unit {
    ps.setObject(1, params.id)
  }
}

public data class GetTheatreFromIdResult(
  public val id: Int,
  public val name: String
)

public class GetTheatreFromIdRowMapper : RowMapper<GetTheatreFromIdResult> {
  public override fun map(rs: ResultSet): GetTheatreFromIdResult = GetTheatreFromIdResult(
  id = rs.getObject("id") as kotlin.Int,
    name = rs.getObject("name") as kotlin.String)
}

public class GetTheatreFromIdQuery : Query<GetTheatreFromIdParams, GetTheatreFromIdResult> {
  public override val sql: String = """
      |SELECT * FROM theatres WHERE id=?;
      |""".trimMargin()

  public override val mapper: RowMapper<GetTheatreFromIdResult> = GetTheatreFromIdRowMapper()

  public override val paramSetter: ParamSetter<GetTheatreFromIdParams> =
      GetTheatreFromIdParamSetter()
}
